package com.arthur.tarefas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Sistema de Tarefas API");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sistema de Tarefas API está funcionando! (v1.0.1)");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints", Map.of(
            "health", "/api/public/health",
            "auth", "/api/auth/**",
            "swagger", "/swagger-ui.html"
        ));
        return ResponseEntity.ok(response);
    }
}
