package com.arthur.tarefas.controller;

import com.arthur.tarefas.dto.UsuarioDTO;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.security.JwtUtil;
import com.arthur.tarefas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend is running");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );
            
            Usuario usuario = (Usuario) authentication.getPrincipal();
            String token = jwtUtil.generateToken(usuario);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Credenciais inválidas");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registro(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            usuarioService.criarUsuario(usuarioDTO);
            
            // Fazer login automático após registro
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), usuarioDTO.getSenha())
            );
            
            Usuario usuario = (Usuario) authentication.getPrincipal();
            String token = jwtUtil.generateToken(usuario);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao criar conta: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> getPerfil(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(usuarioService.converterParaDTO(usuario));
    }
    
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> atualizarPerfil(@RequestBody UsuarioDTO usuarioDTO, 
                                                     Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioDTO usuarioAtualizado = usuarioService.atualizarUsuario(usuario.getId(), usuarioDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }
    
    @GetMapping("/usuario/{uuid}")
    public ResponseEntity<UsuarioDTO> buscarPorUuid(@PathVariable("uuid") String uuid) {
        UsuarioDTO usuario = usuarioService.buscarPorUuid(uuid);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping("/usuarios/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuariosPorEmail(@RequestParam("email") String email) {
        List<UsuarioDTO> usuarios = usuarioService.buscarUsuariosPorEmail(email);
        return ResponseEntity.ok(usuarios);
    }
    
    // Classe interna para o request de login
    public static class LoginRequest {
        private String email;
        private String senha;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}
