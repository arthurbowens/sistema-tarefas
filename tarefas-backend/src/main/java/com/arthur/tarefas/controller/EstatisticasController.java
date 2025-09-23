package com.arthur.tarefas.controller;

import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.service.TarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstatisticasController {
    
    private final TarefaService tarefaService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEstatisticas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        // Buscar todas as tarefas do usuário (incluindo compartilhadas aceitas)
        List<com.arthur.tarefas.dto.TarefaDTO> todasTarefas = tarefaService.buscarTarefasDoUsuario(usuario.getId());
        
        // Contar por status
        long totalTarefas = todasTarefas.size();
        long tarefasConcluidas = todasTarefas.stream()
                .filter(tarefa -> tarefa.getStatus() == StatusTarefa.CONCLUIDA)
                .count();
        long tarefasPendentes = todasTarefas.stream()
                .filter(tarefa -> tarefa.getStatus() == StatusTarefa.PENDENTE)
                .count();
        long tarefasEmAndamento = todasTarefas.stream()
                .filter(tarefa -> tarefa.getStatus() == StatusTarefa.EM_ANDAMENTO)
                .count();
        
        // Calcular tarefas atrasadas (pendentes com data de vencimento passada)
        long tarefasAtrasadas = todasTarefas.stream()
                .filter(tarefa -> tarefa.getStatus() != StatusTarefa.CONCLUIDA && 
                                 tarefa.getDataVencimento() != null && 
                                 tarefa.getDataVencimento().isBefore(LocalDateTime.now()))
                .count();
        
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalTarefas", totalTarefas);
        estatisticas.put("tarefasConcluidas", tarefasConcluidas);
        estatisticas.put("tarefasPendentes", tarefasPendentes);
        estatisticas.put("tarefasEmAndamento", tarefasEmAndamento);
        estatisticas.put("tarefasAtrasadas", tarefasAtrasadas);
        
        if (totalTarefas > 0) {
            double percentualConcluidas = (double) tarefasConcluidas / totalTarefas * 100;
            estatisticas.put("percentualConcluidas", Math.round(percentualConcluidas * 100.0) / 100.0);
        } else {
            estatisticas.put("percentualConcluidas", 0.0);
        }
        
        return ResponseEntity.ok(estatisticas);
    }
}
