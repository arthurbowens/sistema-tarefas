package com.arthur.tarefas.service;

import com.arthur.tarefas.dto.TarefaDTO;
import com.arthur.tarefas.enums.CategoriaTarefa;
import com.arthur.tarefas.enums.PrioridadeTarefa;
import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.repository.TarefaRepository;
import com.arthur.tarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TarefaService {
    
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ChecklistItemService checklistItemService;
    private final CompartilhamentoTarefaService compartilhamentoService;
    
    public TarefaDTO criarTarefa(TarefaDTO tarefaDTO, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setDescricao(tarefaDTO.getDescricao());
        tarefa.setStatus(tarefaDTO.getStatus() != null ? tarefaDTO.getStatus() : StatusTarefa.PENDENTE);
        tarefa.setPrioridade(tarefaDTO.getPrioridade() != null ? tarefaDTO.getPrioridade() : PrioridadeTarefa.MEDIA);
        tarefa.setCategoria(tarefaDTO.getCategoria());
        tarefa.setDataVencimento(tarefaDTO.getDataVencimento());
        tarefa.setCor(tarefaDTO.getCor() != null ? tarefaDTO.getCor() : "#3498db");
        tarefa.setTags(tarefaDTO.getTags());
        tarefa.setUsuario(usuario);
        
        if (tarefaDTO.getTarefaPaiId() != null) {
            Tarefa tarefaPai = tarefaRepository.findById(tarefaDTO.getTarefaPaiId())
                    .orElseThrow(() -> new RuntimeException("Tarefa pai não encontrada"));
            tarefa.setTarefaPai(tarefaPai);
        }
        
        Tarefa tarefaSalva = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefaSalva);
    }
    
    public TarefaDTO atualizarTarefa(Long id, TarefaDTO tarefaDTO, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        // Verificar se a tarefa está concluída
        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new RuntimeException("Não é possível editar uma tarefa concluída");
        }
        
        // Verificar se o usuário tem permissão para editar
        if (!tarefa.getUsuario().getId().equals(usuarioId) && 
            !compartilhamentoService.usuarioPodeEditar(tarefa, usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para editar esta tarefa");
        }
        
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setDescricao(tarefaDTO.getDescricao());
        tarefa.setStatus(tarefaDTO.getStatus());
        tarefa.setPrioridade(tarefaDTO.getPrioridade());
        tarefa.setCategoria(tarefaDTO.getCategoria());
        tarefa.setDataVencimento(tarefaDTO.getDataVencimento());
        tarefa.setCor(tarefaDTO.getCor());
        tarefa.setTags(tarefaDTO.getTags());
        
        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefaAtualizada);
    }
    
    public void excluirTarefa(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        // Verificar se a tarefa está concluída
        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new RuntimeException("Não é possível excluir uma tarefa concluída");
        }
        
        if (!tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para excluir esta tarefa");
        }
        
        tarefaRepository.delete(tarefa);
    }
    
    public TarefaDTO buscarPorId(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        if (!tarefa.getUsuario().getId().equals(usuarioId) && 
            !compartilhamentoService.usuarioPodeVisualizar(tarefa, usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para visualizar esta tarefa");
        }
        
        return converterParaDTO(tarefa);
    }
    
    public List<TarefaDTO> buscarTarefasDoUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Buscar tarefas próprias do usuário
        List<TarefaDTO> tarefasProprias = tarefaRepository.findTarefasPrincipaisByUsuario(usuario)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        
        // Buscar tarefas compartilhadas aceitas
        List<TarefaDTO> tarefasCompartilhadas = buscarTarefasCompartilhadas(usuarioId);
        
        // Combinar as listas
        List<TarefaDTO> todasTarefas = new ArrayList<>();
        todasTarefas.addAll(tarefasProprias);
        todasTarefas.addAll(tarefasCompartilhadas);
        
        return todasTarefas;
    }
    
    public List<TarefaDTO> buscarTarefasCompartilhadas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return tarefaRepository.findTarefasCompartilhadasComUsuario(usuario)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<TarefaDTO> buscarPorStatus(Long usuarioId, StatusTarefa status) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return tarefaRepository.findByUsuarioAndStatus(usuario, status)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<TarefaDTO> buscarPorPrioridade(Long usuarioId, PrioridadeTarefa prioridade) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return tarefaRepository.findByUsuarioAndPrioridade(usuario, prioridade)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<TarefaDTO> buscarPorPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return tarefaRepository.findByUsuarioAndDataVencimentoBetween(usuario, inicio, fim)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<TarefaDTO> buscarPorTermo(Long usuarioId, String termo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return tarefaRepository.findByUsuarioAndTituloOrDescricaoContaining(usuario, termo)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<TarefaDTO> buscarComFiltros(Long usuarioId, StatusTarefa status, PrioridadeTarefa prioridade, 
                                          CategoriaTarefa categoria, String termo, LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        List<Tarefa> tarefas = tarefaRepository.findTarefasPrincipaisByUsuario(usuario);
        
        // Aplicar filtros
        if (status != null) {
            tarefas = tarefas.stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());
        }
        
        if (prioridade != null) {
            tarefas = tarefas.stream()
                    .filter(t -> t.getPrioridade() == prioridade)
                    .collect(Collectors.toList());
        }
        
        if (categoria != null) {
            tarefas = tarefas.stream()
                    .filter(t -> t.getCategoria() == categoria)
                    .collect(Collectors.toList());
        }
        
        if (termo != null && !termo.trim().isEmpty()) {
            String termoLower = termo.toLowerCase();
            tarefas = tarefas.stream()
                    .filter(t -> (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(termoLower)) ||
                               (t.getDescricao() != null && t.getDescricao().toLowerCase().contains(termoLower)))
                    .collect(Collectors.toList());
        }
        
        if (inicio != null && fim != null) {
            tarefas = tarefas.stream()
                    .filter(t -> t.getDataVencimento() != null && 
                               !t.getDataVencimento().isBefore(inicio) && 
                               !t.getDataVencimento().isAfter(fim))
                    .collect(Collectors.toList());
        }
        
        return tarefas.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public TarefaDTO marcarComoConcluida(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        if (!tarefa.getUsuario().getId().equals(usuarioId) && 
            !compartilhamentoService.usuarioPodeEditar(tarefa, usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para editar esta tarefa");
        }
        
        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        tarefa.setDataConclusao(LocalDateTime.now());
        
        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefaAtualizada);
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
        
        // Converter usuário
        if (tarefa.getUsuario() != null) {
            dto.setUsuario(usuarioService.converterParaDTO(tarefa.getUsuario()));
        }
        
        // Converter checklist
        dto.setChecklist(tarefa.getChecklist().stream()
                .map(checklistItemService::converterParaDTO)
                .collect(Collectors.toList()));
        
        // Converter compartilhamentos
        dto.setCompartilhamentos(tarefa.getCompartilhamentos().stream()
                .map(compartilhamentoService::converterParaDTO)
                .collect(Collectors.toList()));
        
        // Converter subtarefas
        dto.setSubtarefas(tarefa.getSubtarefas().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList()));
        
        return dto;
    }
}
