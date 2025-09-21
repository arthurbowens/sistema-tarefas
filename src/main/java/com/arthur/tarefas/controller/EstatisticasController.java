package com.arthur.tarefas.controller;

import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstatisticasController {
    
    private final TarefaRepository tarefaRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEstatisticas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        Long totalTarefas = tarefaRepository.countByUsuarioAndStatus(usuario, StatusTarefa.PENDENTE) +
                           tarefaRepository.countByUsuarioAndStatus(usuario, StatusTarefa.EM_ANDAMENTO) +
                           tarefaRepository.countByUsuarioAndStatus(usuario, StatusTarefa.CONCLUIDA);
        
        Long tarefasConcluidas = tarefaRepository.countByUsuarioAndStatus(usuario, StatusTarefa.CONCLUIDA);
        Long tarefasPendentes = tarefaRepository.countByUsuarioAndStatus(usuario, StatusTarefa.PENDENTE);
        Long tarefasEmAndamento = tarefaRepository.countByUsuarioAndStatus(usuario, StatusTarefa.EM_ANDAMENTO);
        
        // Calcular tarefas atrasadas (pendentes com data de vencimento passada)
        Long tarefasAtrasadas = tarefaRepository.findByUsuarioAndDataVencimentoBetween(
            usuario, LocalDateTime.now().minusYears(1), LocalDateTime.now())
            .stream()
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
