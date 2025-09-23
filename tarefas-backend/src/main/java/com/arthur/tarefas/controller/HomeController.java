package com.arthur.tarefas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bem-vindo ao Sistema de Tarefas API!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "ONLINE");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "health", "/api/public/health",
            "info", "/api/public/",
            "auth", "/api/auth/**",
            "swagger", "/swagger-ui.html",
            "docs", "/v3/api-docs"
        ));
        return ResponseEntity.ok(response);
    }
}
