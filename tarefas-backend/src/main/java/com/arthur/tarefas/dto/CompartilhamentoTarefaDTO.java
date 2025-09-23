package com.arthur.tarefas.dto;

import com.arthur.tarefas.enums.TipoCompartilhamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompartilhamentoTarefaDTO {
    
    private Long id;
    private Long tarefaId;
    private TarefaDTO tarefa;
    private UsuarioDTO usuarioCompartilhado;
    private TipoCompartilhamento tipoCompartilhamento;
    private LocalDateTime dataCompartilhamento;
    private LocalDateTime dataExpiracao;
    private boolean conviteAceito;
}
