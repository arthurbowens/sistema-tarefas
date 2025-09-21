package com.arthur.tarefas.service;

import com.arthur.tarefas.dto.ChecklistItemDTO;
import com.arthur.tarefas.model.ChecklistItem;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.repository.ChecklistItemRepository;
import com.arthur.tarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChecklistItemService {
    
    private final ChecklistItemRepository checklistItemRepository;
    private final TarefaRepository tarefaRepository;
    
    public ChecklistItemDTO criarItem(Long tarefaId, ChecklistItemDTO itemDTO) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        ChecklistItem item = new ChecklistItem();
        item.setDescricao(itemDTO.getDescricao());
        item.setConcluido(false);
        item.setTarefa(tarefa);
        item.setOrdem(calcularProximaOrdem(tarefa));
        
        ChecklistItem itemSalvo = checklistItemRepository.save(item);
        return converterParaDTO(itemSalvo);
    }
    
    public ChecklistItemDTO atualizarItem(Long id, ChecklistItemDTO itemDTO) {
        ChecklistItem item = checklistItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        
        item.setDescricao(itemDTO.getDescricao());
        item.setConcluido(itemDTO.isConcluido());
        item.setOrdem(itemDTO.getOrdem());
        
        ChecklistItem itemAtualizado = checklistItemRepository.save(item);
        return converterParaDTO(itemAtualizado);
    }
    
    public void excluirItem(Long id) {
        ChecklistItem item = checklistItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        
        checklistItemRepository.delete(item);
    }
    
    public ChecklistItemDTO marcarComoConcluido(Long id) {
        ChecklistItem item = checklistItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        
        item.setConcluido(true);
        item.setDataConclusao(LocalDateTime.now());
        
        ChecklistItem itemAtualizado = checklistItemRepository.save(item);
        return converterParaDTO(itemAtualizado);
    }
    
    public List<ChecklistItemDTO> buscarItensDaTarefa(Long tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        return checklistItemRepository.findByTarefaOrderByOrdem(tarefa)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public void reordenarItens(Long tarefaId, List<Long> idsOrdenados) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        
        List<ChecklistItem> itens = checklistItemRepository.findByTarefaOrderByOrdem(tarefa);
        
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long itemId = idsOrdenados.get(i);
            ChecklistItem item = itens.stream()
                    .filter(it -> it.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Item não encontrado"));
            item.setOrdem(i + 1);
        }
        
        checklistItemRepository.saveAll(itens);
    }
    
    private Integer calcularProximaOrdem(Tarefa tarefa) {
        Long count = checklistItemRepository.countByTarefa(tarefa);
        return count.intValue() + 1;
    }
    
    public ChecklistItemDTO converterParaDTO(ChecklistItem item) {
        ChecklistItemDTO dto = new ChecklistItemDTO();
        dto.setId(item.getId());
        dto.setDescricao(item.getDescricao());
        dto.setConcluido(item.isConcluido());
        dto.setDataCriacao(item.getDataCriacao());
        dto.setDataConclusao(item.getDataConclusao());
        dto.setOrdem(item.getOrdem());
        return dto;
    }
}
