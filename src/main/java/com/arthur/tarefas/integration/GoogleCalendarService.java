package com.arthur.tarefas.integration;

import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {
    
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${google.calendar.client.id:}")
    private String clientId;
    
    @Value("${google.calendar.client.secret:}")
    private String clientSecret;
    
    @Value("${google.calendar.redirect.uri:http://localhost:8080/api/google-calendar/callback}")
    private String redirectUri;
    
    public String getAuthorizationUrl(Long usuarioId) {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline" +
                "&state=" + usuarioId;
        
        return authUrl;
    }
    
    public void processarCallback(String code, Long usuarioId) {
        try {
            // Trocar código por token
            String tokenUrl = "https://oauth2.googleapis.com/token";
            
            Map<String, String> params = new HashMap<>();
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("code", code);
            params.put("grant_type", "authorization_code");
            params.put("redirect_uri", redirectUri);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tokenResponse = (Map<String, Object>) response.getBody();
                String accessToken = (String) tokenResponse.get("access_token");
                String refreshToken = (String) tokenResponse.get("refresh_token");
                
                Usuario usuario = usuarioRepository.findById(usuarioId)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                
                usuario.setGoogleCalendarToken(accessToken);
                usuario.setGoogleCalendarRefreshToken(refreshToken);
                usuarioRepository.save(usuario);
                
                log.info("Google Calendar conectado para usuário: {}", usuarioId);
            } else {
                throw new RuntimeException("Erro ao obter token do Google Calendar");
            }
            
        } catch (Exception e) {
            log.error("Erro ao processar callback do Google Calendar", e);
            throw new RuntimeException("Erro ao conectar com Google Calendar", e);
        }
    }
    
    public String criarEvento(Tarefa tarefa) {
        try {
            Usuario usuario = tarefa.getUsuario();
            if (usuario.getGoogleCalendarToken() == null) {
                throw new RuntimeException("Usuário não conectado ao Google Calendar");
            }
            
            String eventUrl = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
            
            Map<String, Object> event = new HashMap<>();
            event.put("summary", tarefa.getTitulo());
            event.put("description", tarefa.getDescricao());
            
            if (tarefa.getDataVencimento() != null) {
                Map<String, Object> start = new HashMap<>();
                start.put("dateTime", tarefa.getDataVencimento().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                start.put("timeZone", "America/Sao_Paulo");
                event.put("start", start);
                
                Map<String, Object> end = new HashMap<>();
                end.put("dateTime", tarefa.getDataVencimento().plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                end.put("timeZone", "America/Sao_Paulo");
                event.put("end", end);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(usuario.getGoogleCalendarToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(event, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(eventUrl, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> eventResponse = (Map<String, Object>) response.getBody();
                return (String) eventResponse.get("id");
            } else {
                throw new RuntimeException("Erro ao criar evento no Google Calendar");
            }
            
        } catch (Exception e) {
            log.error("Erro ao criar evento no Google Calendar", e);
            throw new RuntimeException("Erro ao sincronizar com Google Calendar", e);
        }
    }
    
    public void atualizarEvento(Tarefa tarefa) {
        try {
            if (tarefa.getGoogleEventId() == null) {
                return;
            }
            
            Usuario usuario = tarefa.getUsuario();
            String eventUrl = "https://www.googleapis.com/calendar/v3/calendars/primary/events/" + tarefa.getGoogleEventId();
            
            Map<String, Object> event = new HashMap<>();
            event.put("summary", tarefa.getTitulo());
            event.put("description", tarefa.getDescricao());
            
            if (tarefa.getDataVencimento() != null) {
                Map<String, Object> start = new HashMap<>();
                start.put("dateTime", tarefa.getDataVencimento().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                start.put("timeZone", "America/Sao_Paulo");
                event.put("start", start);
                
                Map<String, Object> end = new HashMap<>();
                end.put("dateTime", tarefa.getDataVencimento().plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                end.put("timeZone", "America/Sao_Paulo");
                event.put("end", end);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(usuario.getGoogleCalendarToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(event, headers);
            restTemplate.put(eventUrl, request);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar evento no Google Calendar", e);
            throw new RuntimeException("Erro ao sincronizar com Google Calendar", e);
        }
    }
    
    public void excluirEvento(Tarefa tarefa) {
        try {
            if (tarefa.getGoogleEventId() == null) {
                return;
            }
            
            Usuario usuario = tarefa.getUsuario();
            String eventUrl = "https://www.googleapis.com/calendar/v3/calendars/primary/events/" + tarefa.getGoogleEventId();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(usuario.getGoogleCalendarToken());
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.exchange(eventUrl, HttpMethod.DELETE, request, Void.class);
            
        } catch (Exception e) {
            log.error("Erro ao excluir evento no Google Calendar", e);
            throw new RuntimeException("Erro ao sincronizar com Google Calendar", e);
        }
    }
}
