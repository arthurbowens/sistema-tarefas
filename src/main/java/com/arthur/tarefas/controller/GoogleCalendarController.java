package com.arthur.tarefas.controller;

import com.arthur.tarefas.integration.GoogleCalendarService;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/google-calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoogleCalendarController {
    
    private final GoogleCalendarService googleCalendarService;
    private final UsuarioService usuarioService;
    
    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthUrl(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String authUrl = googleCalendarService.getAuthorizationUrl(usuario.getId());
        
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }
    
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> processCallback(@RequestParam("code") String code,
                                                              Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        googleCalendarService.processarCallback(code, usuario.getId());
        
        return ResponseEntity.ok(Map.of("message", "Google Calendar conectado com sucesso!"));
    }
    
    @PostMapping("/conectar")
    public ResponseEntity<Map<String, String>> conectar(@RequestParam("token") String token,
                                                       @RequestParam("refreshToken") String refreshToken,
                                                       Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        usuarioService.conectarGoogleCalendar(usuario.getId(), token, refreshToken);
        
        return ResponseEntity.ok(Map.of("message", "Google Calendar conectado com sucesso!"));
    }
    
    @DeleteMapping("/desconectar")
    public ResponseEntity<Map<String, String>> desconectar(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        usuarioService.desconectarGoogleCalendar(usuario.getId());
        
        return ResponseEntity.ok(Map.of("message", "Google Calendar desconectado com sucesso!"));
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean conectado = usuario.getGoogleCalendarToken() != null;
        
        return ResponseEntity.ok(Map.of("conectado", conectado));
    }
}
