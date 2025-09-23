package com.arthur.tarefas.dto;

import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.config.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaRecorrenteInstanciaDTO {
    
    private Long id;
    private Long tarefaRecorrenteId;
    private LocalDate dataInstancia;
    private StatusTarefa status;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataConclusao;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataCriacao;
    
    // Dados da tarefa recorrente para facilitar o frontend
    private String titulo;
    private String descricao;
    private String prioridade;
    private String categoria;
    private String cor;
    private String tags;
}
