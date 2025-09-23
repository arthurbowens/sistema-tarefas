package com.arthur.tarefas.dto;

import com.arthur.tarefas.enums.CategoriaTarefa;
import com.arthur.tarefas.enums.PrioridadeTarefa;
import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.enums.TipoRecorrencia;
import com.arthur.tarefas.enums.DiaSemana;
import com.arthur.tarefas.config.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataCriacao;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataVencimento;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataConclusao;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataAtualizacao;
    private String cor;
    private String tags;
    private Long tarefaPaiId;
    private List<ChecklistItemDTO> checklist;
    private List<CompartilhamentoTarefaDTO> compartilhamentos;
    private List<TarefaDTO> subtarefas;
    private UsuarioDTO usuario;
    
    // Campos de recorrência
    private Boolean isRecorrente;
    private TipoRecorrencia tipoRecorrencia;
    private Integer intervaloRecorrencia;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataFimRecorrencia;
    
    private List<DiaSemana> diasDaSemana;
}
