package com.arthur.tarefas.controller;

import com.arthur.tarefas.dto.TarefaRecorrenteInstanciaDTO;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.service.TarefaRecorrenteInstanciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas-recorrentes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TarefaRecorrenteInstanciaController {
    
    private final TarefaRecorrenteInstanciaService instanciaService;
    
    @GetMapping("/instancias")
    public ResponseEntity<List<TarefaRecorrenteInstanciaDTO>> buscarInstancias(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication) {
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaRecorrenteInstanciaDTO> instancias = 
            instanciaService.gerarInstanciasParaPeriodo(usuario.getId(), dataInicio, dataFim);
        
        return ResponseEntity.ok(instancias);
    }
    
    @PostMapping("/{tarefaId}/instancias/{dataInstancia}/concluir")
    public ResponseEntity<TarefaRecorrenteInstanciaDTO> marcarInstanciaComoConcluida(
            @PathVariable("tarefaId") Long tarefaId,
            @PathVariable("dataInstancia") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInstancia,
            Authentication authentication) {
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        TarefaRecorrenteInstanciaDTO instancia = 
            instanciaService.marcarInstanciaComoConcluida(tarefaId, dataInstancia);
        
        return ResponseEntity.ok(instancia);
    }
}
