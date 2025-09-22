package com.arthur.tarefas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir origens específicas
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",  // Angular dev server
            "http://localhost:3000",  // React dev server
            "http://127.0.0.1:4200",
            "http://127.0.0.1:3000",
            "https://*.onrender.com",  // Render deployments
            "https://*.vercel.app",    // Vercel deployments
            "https://*.netlify.app"    // Netlify deployments
        ));
        
        // Permitir métodos HTTP
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Permitir headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"
        ));
        
        // Permitir credenciais
        configuration.setAllowCredentials(true);
        
        // Expor headers
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
