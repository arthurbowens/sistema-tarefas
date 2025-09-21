package com.arthur.tarefas.controller;

import com.arthur.tarefas.dto.ChecklistItemDTO;
import com.arthur.tarefas.service.ChecklistItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas/{tarefaId}/checklist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChecklistController {
    
    private final ChecklistItemService checklistItemService;
    
    @PostMapping
    public ResponseEntity<ChecklistItemDTO> criarItem(@PathVariable Long tarefaId,
                                                     @RequestBody ChecklistItemDTO itemDTO) {
        ChecklistItemDTO itemCriado = checklistItemService.criarItem(tarefaId, itemDTO);
        return ResponseEntity.ok(itemCriado);
    }
    
    @GetMapping
    public ResponseEntity<List<ChecklistItemDTO>> buscarItens(@PathVariable Long tarefaId) {
        List<ChecklistItemDTO> itens = checklistItemService.buscarItensDaTarefa(tarefaId);
        return ResponseEntity.ok(itens);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ChecklistItemDTO> atualizarItem(@PathVariable Long tarefaId,
                                                         @PathVariable Long id,
                                                         @RequestBody ChecklistItemDTO itemDTO) {
        ChecklistItemDTO itemAtualizado = checklistItemService.atualizarItem(id, itemDTO);
        return ResponseEntity.ok(itemAtualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirItem(@PathVariable Long tarefaId,
                                           @PathVariable Long id) {
        checklistItemService.excluirItem(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/concluir")
    public ResponseEntity<ChecklistItemDTO> marcarComoConcluido(@PathVariable Long tarefaId,
                                                               @PathVariable Long id) {
        ChecklistItemDTO item = checklistItemService.marcarComoConcluido(id);
        return ResponseEntity.ok(item);
    }
    
    @PutMapping("/reordenar")
    public ResponseEntity<Void> reordenarItens(@PathVariable Long tarefaId,
                                              @RequestBody List<Long> idsOrdenados) {
        checklistItemService.reordenarItens(tarefaId, idsOrdenados);
        return ResponseEntity.ok().build();
    }
}
