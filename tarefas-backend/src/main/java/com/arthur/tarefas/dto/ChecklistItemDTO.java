package com.arthur.tarefas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemDTO {
    
    private Long id;
    private String titulo;
    private String descricao;
    private boolean concluido;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;
    private Integer ordem;
}
