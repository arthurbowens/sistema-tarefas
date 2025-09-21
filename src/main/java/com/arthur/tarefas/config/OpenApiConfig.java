package com.arthur.tarefas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Tarefas API")
                        .description("API completa para gerenciamento de tarefas com integração ao Google Calendar")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Arthur Bowens")
                                .email("arthur@exemplo.com")
                                .url("https://github.com/arthurbowens"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://tarefas-backend.onrender.com")
                                .description("Servidor de Produção")
                ));
    }
}
