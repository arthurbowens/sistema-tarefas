package com.arthur.tarefas.service;

import com.arthur.tarefas.dto.CompartilhamentoTarefaDTO;
import com.arthur.tarefas.enums.TipoCompartilhamento;
import com.arthur.tarefas.model.CompartilhamentoTarefa;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.repository.CompartilhamentoTarefaRepository;
import com.arthur.tarefas.repository.TarefaRepository;
import com.arthur.tarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompartilhamentoTarefaService {
    
    private final CompartilhamentoTarefaRepository compartilhamentoRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    
    public CompartilhamentoTarefaDTO compartilharTarefa(Long tarefaId, Long usuarioId, String emailUsuario, 
                                                       TipoCompartilhamento tipo, LocalDateTime dataExpiracao) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (compartilhamentoRepository.existsByTarefaAndUsuario(tarefa, usuario)) {
            throw new RuntimeException("Tarefa já está compartilhada com este usuário");
        }
        
        CompartilhamentoTarefa compartilhamento = new CompartilhamentoTarefa();
        compartilhamento.setTarefa(tarefa);
        compartilhamento.setUsuario(usuario);
        compartilhamento.setTipo(tipo);
        compartilhamento.setDataExpiracao(dataExpiracao);
        compartilhamento.setConviteAceito(false);
        
        CompartilhamentoTarefa compartilhamentoSalvo = compartilhamentoRepository.save(compartilhamento);
        return converterParaDTO(compartilhamentoSalvo);
    }
    
    public void aceitarConvite(Long compartilhamentoId, Long usuarioId) {
        CompartilhamentoTarefa compartilhamento = compartilhamentoRepository.findById(compartilhamentoId)
                .orElseThrow(() -> new RuntimeException("Compartilhamento não encontrado"));
        
        if (!compartilhamento.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para aceitar este convite");
        }
        
        compartilhamento.setConviteAceito(true);
        compartilhamentoRepository.save(compartilhamento);
    }
    
    public void rejeitarConvite(Long compartilhamentoId, Long usuarioId) {
        CompartilhamentoTarefa compartilhamento = compartilhamentoRepository.findById(compartilhamentoId)
                .orElseThrow(() -> new RuntimeException("Compartilhamento não encontrado"));
        
        if (!compartilhamento.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para rejeitar este convite");
        }
        
        compartilhamentoRepository.delete(compartilhamento);
    }
    
    public void removerCompartilhamento(Long compartilhamentoId, Long usuarioId) {
        CompartilhamentoTarefa compartilhamento = compartilhamentoRepository.findById(compartilhamentoId)
                .orElseThrow(() -> new RuntimeException("Compartilhamento não encontrado"));
        
        // Apenas o dono da tarefa pode remover compartilhamentos
        if (!compartilhamento.getTarefa().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para remover este compartilhamento");
        }
        
        compartilhamentoRepository.delete(compartilhamento);
    }
    
    public List<CompartilhamentoTarefaDTO> buscarCompartilhamentosDaTarefa(Long tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        return compartilhamentoRepository.findByTarefa(tarefa)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<CompartilhamentoTarefaDTO> buscarConvitesPendentes(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return compartilhamentoRepository.findPendentesByUsuario(usuario)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public boolean usuarioPodeVisualizar(Tarefa tarefa, Long usuarioId) {
        // O dono da tarefa sempre pode visualizar
        if (tarefa.getUsuario().getId().equals(usuarioId)) {
            return true;
        }
        
        // Verificar se há compartilhamento ativo
        return compartilhamentoRepository.findByTarefaAndUsuario(tarefa, 
                usuarioRepository.findById(usuarioId).orElse(null))
                .map(compartilhamento -> compartilhamento.isConviteAceito() && 
                     (compartilhamento.getDataExpiracao() == null || 
                      compartilhamento.getDataExpiracao().isAfter(LocalDateTime.now())))
                .orElse(false);
    }
    
    public boolean usuarioPodeEditar(Tarefa tarefa, Long usuarioId) {
        // O dono da tarefa sempre pode editar
        if (tarefa.getUsuario().getId().equals(usuarioId)) {
            return true;
        }
        
        // Verificar se há compartilhamento com permissão de escrita
        return compartilhamentoRepository.findByTarefaAndUsuario(tarefa, 
                usuarioRepository.findById(usuarioId).orElse(null))
                .map(compartilhamento -> compartilhamento.isConviteAceito() && 
                     (compartilhamento.getTipo() == TipoCompartilhamento.ESCRITA || 
                      compartilhamento.getTipo() == TipoCompartilhamento.ADMIN) &&
                     (compartilhamento.getDataExpiracao() == null || 
                      compartilhamento.getDataExpiracao().isAfter(LocalDateTime.now())))
                .orElse(false);
    }
    
    public CompartilhamentoTarefaDTO converterParaDTO(CompartilhamentoTarefa compartilhamento) {
        CompartilhamentoTarefaDTO dto = new CompartilhamentoTarefaDTO();
        dto.setId(compartilhamento.getId());
        dto.setTarefaId(compartilhamento.getTarefa().getId());
        
        // Converter tarefa para DTO
        com.arthur.tarefas.dto.TarefaDTO tarefaDTO = new com.arthur.tarefas.dto.TarefaDTO();
        tarefaDTO.setId(compartilhamento.getTarefa().getId());
        tarefaDTO.setTitulo(compartilhamento.getTarefa().getTitulo());
        tarefaDTO.setDescricao(compartilhamento.getTarefa().getDescricao());
        tarefaDTO.setStatus(compartilhamento.getTarefa().getStatus());
        tarefaDTO.setPrioridade(compartilhamento.getTarefa().getPrioridade());
        tarefaDTO.setCategoria(compartilhamento.getTarefa().getCategoria());
        tarefaDTO.setDataCriacao(compartilhamento.getTarefa().getDataCriacao());
        tarefaDTO.setDataVencimento(compartilhamento.getTarefa().getDataVencimento());
        tarefaDTO.setDataConclusao(compartilhamento.getTarefa().getDataConclusao());
        tarefaDTO.setTags(compartilhamento.getTarefa().getTags());
        tarefaDTO.setCor(compartilhamento.getTarefa().getCor());
        dto.setTarefa(tarefaDTO);
        
        // Converter usuário para DTO
        com.arthur.tarefas.dto.UsuarioDTO usuarioDTO = new com.arthur.tarefas.dto.UsuarioDTO();
        usuarioDTO.setId(compartilhamento.getUsuario().getId());
        usuarioDTO.setEmail(compartilhamento.getUsuario().getEmail());
        usuarioDTO.setNome(compartilhamento.getUsuario().getNome());
        usuarioDTO.setSobrenome(compartilhamento.getUsuario().getSobrenome());
        dto.setUsuarioCompartilhado(usuarioDTO);
        
        dto.setTipoCompartilhamento(compartilhamento.getTipo());
        dto.setDataCompartilhamento(compartilhamento.getDataCompartilhamento());
        dto.setDataExpiracao(compartilhamento.getDataExpiracao());
        dto.setConviteAceito(compartilhamento.isConviteAceito());
        return dto;
    }
}
