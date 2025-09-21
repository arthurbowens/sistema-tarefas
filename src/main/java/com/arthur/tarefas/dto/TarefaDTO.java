package com.arthur.tarefas.dto;

import com.arthur.tarefas.enums.CategoriaTarefa;
import com.arthur.tarefas.enums.PrioridadeTarefa;
import com.arthur.tarefas.enums.StatusTarefa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {
    
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private PrioridadeTarefa prioridade;
    private CategoriaTarefa categoria;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataVencimento;
    private LocalDateTime dataConclusao;
    private LocalDateTime dataAtualizacao;
    private String cor;
    private String tags;
    private String googleEventId;
    private Long tarefaPaiId;
    private List<ChecklistItemDTO> checklist;
    private List<CompartilhamentoTarefaDTO> compartilhamentos;
    private List<TarefaDTO> subtarefas;
    private UsuarioDTO usuario;
}
