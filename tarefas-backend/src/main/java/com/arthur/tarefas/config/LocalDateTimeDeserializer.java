package com.arthur.tarefas.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String dateString = node.asText();
        
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            // Tentar parsear como LocalDateTime completo
            if (dateString.contains("T")) {
                return LocalDateTime.parse(dateString, DATETIME_FORMATTER);
            } else {
                // Se for apenas data, converter para LocalDateTime no início do dia
                LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
                return date.atStartOfDay();
            }
        } catch (Exception e) {
            throw new IOException("Erro ao deserializar data: " + dateString, e);
        }
    }
}
