package com.arthur.tarefas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource dataSource() {
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Converte URL do Render (postgresql://) para formato JDBC (jdbc:postgresql://)
            String jdbcUrl = databaseUrl;
            if (databaseUrl.startsWith("postgresql://")) {
                jdbcUrl = "jdbc:" + databaseUrl;
            }
            
            // Extrai componentes da URL
            URI uri = URI.create(jdbcUrl.replace("jdbc:", ""));
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
            
            String finalJdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);
            
            return org.springframework.boot.jdbc.DataSourceBuilder.create()
                    .url(finalJdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
        
        return null;
    }
}
