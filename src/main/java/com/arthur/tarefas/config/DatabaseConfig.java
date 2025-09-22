package com.arthur.tarefas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Converte URL do Render (postgresql://) para formato JDBC (jdbc:postgresql://)
            if (databaseUrl.startsWith("postgresql://")) {
                databaseUrl = "jdbc:" + databaseUrl;
            }
            
            // Extrai componentes da URL
            URI uri = URI.create(databaseUrl.replace("jdbc:", ""));
            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();
            String userInfo = uri.getUserInfo();
            
            String username = "";
            String password = "";
            if (userInfo != null && userInfo.contains(":")) {
                String[] credentials = userInfo.split(":");
                username = credentials[0];
                password = credentials[1];
            }
            
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);
            
            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
        
        // Fallback para configuração padrão
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/tarefas_db")
                .username("arthurbowens")
                .password("")
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
