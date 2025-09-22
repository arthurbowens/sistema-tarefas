package com.arthur.tarefas.service;

import com.arthur.tarefas.dto.TarefaDTO;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.repository.TarefaRepository;
import com.arthur.tarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaRecorrenteService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<TarefaDTO> criarTarefasRecorrentes(TarefaDTO tarefaDTO, Long usuarioId) {
        List<TarefaDTO> tarefasCriadas = new ArrayList<>();
        
        if (!tarefaDTO.getIsRecorrente()) {
            // Criar tarefa simples
            Tarefa tarefa = criarTarefaSimples(tarefaDTO, usuarioId);
            tarefaRepository.save(tarefa);
            tarefasCriadas.add(converterParaDTO(tarefa));
            return tarefasCriadas;
        }

        // Criar apenas uma tarefa recorrente com configuração de recorrência
        // As instâncias futuras serão criadas por um job/scheduler
        Tarefa tarefa = criarTarefaRecorrente(tarefaDTO, usuarioId);
        tarefaRepository.save(tarefa);
        tarefasCriadas.add(converterParaDTO(tarefa));

        return tarefasCriadas;
    }

    private Tarefa criarTarefaSimples(TarefaDTO tarefaDTO, Long usuarioId) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setDescricao(tarefaDTO.getDescricao());
        tarefa.setPrioridade(tarefaDTO.getPrioridade());
        tarefa.setCategoria(tarefaDTO.getCategoria());
        tarefa.setDataVencimento(tarefaDTO.getDataVencimento());
        tarefa.setCor(tarefaDTO.getCor() != null ? tarefaDTO.getCor() : "#3498db");
        tarefa.setTags(tarefaDTO.getTags());
        tarefa.setIsRecorrente(false);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        tarefa.setUsuario(usuario);
        
        return tarefa;
    }

    private Tarefa criarTarefaRecorrente(TarefaDTO tarefaDTO, Long usuarioId) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setDescricao(tarefaDTO.getDescricao());
        tarefa.setPrioridade(tarefaDTO.getPrioridade());
        tarefa.setCategoria(tarefaDTO.getCategoria());
        
        // Data de vencimento é opcional para tarefas recorrentes
        tarefa.setDataVencimento(tarefaDTO.getDataVencimento());
        
        tarefa.setCor(tarefaDTO.getCor() != null ? tarefaDTO.getCor() : "#3498db");
        tarefa.setTags(tarefaDTO.getTags());
        tarefa.setIsRecorrente(true);
        tarefa.setTipoRecorrencia(tarefaDTO.getTipoRecorrencia());
        tarefa.setIntervaloRecorrencia(tarefaDTO.getIntervaloRecorrencia());
        tarefa.setDataFimRecorrencia(tarefaDTO.getDataFimRecorrencia());
        tarefa.setDiasDaSemana(tarefaDTO.getDiasDaSemana());
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        tarefa.setUsuario(usuario);
        
        return tarefa;
    }


    private TarefaDTO converterParaDTO(Tarefa tarefa) {
        TarefaDTO dto = new TarefaDTO();
        dto.setId(tarefa.getId());
        dto.setTitulo(tarefa.getTitulo());
        dto.setDescricao(tarefa.getDescricao());
        dto.setStatus(tarefa.getStatus());
        dto.setPrioridade(tarefa.getPrioridade());
        dto.setCategoria(tarefa.getCategoria());
        dto.setDataCriacao(tarefa.getDataCriacao());
        dto.setDataVencimento(tarefa.getDataVencimento());
        dto.setDataConclusao(tarefa.getDataConclusao());
        dto.setDataAtualizacao(tarefa.getDataAtualizacao());
        dto.setCor(tarefa.getCor());
        dto.setTags(tarefa.getTags());
        dto.setTarefaPaiId(tarefa.getTarefaPai() != null ? tarefa.getTarefaPai().getId() : null);
        dto.setIsRecorrente(tarefa.getIsRecorrente());
        dto.setTipoRecorrencia(tarefa.getTipoRecorrencia());
        dto.setIntervaloRecorrencia(tarefa.getIntervaloRecorrencia());
        dto.setDataFimRecorrencia(tarefa.getDataFimRecorrencia());
        dto.setDiasDaSemana(tarefa.getDiasDaSemana());
        
        return dto;
    }
}
