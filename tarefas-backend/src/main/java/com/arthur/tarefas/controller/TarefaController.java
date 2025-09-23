package com.arthur.tarefas.controller;

import com.arthur.tarefas.dto.TarefaDTO;
import com.arthur.tarefas.enums.CategoriaTarefa;
import com.arthur.tarefas.enums.PrioridadeTarefa;
import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.service.TarefaService;
import com.arthur.tarefas.service.TarefaRecorrenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TarefaController {
    
    private final TarefaService tarefaService;
    private final TarefaRecorrenteService tarefaRecorrenteService;
    
    @PostMapping
    public ResponseEntity<TarefaDTO> criarTarefa(@RequestBody TarefaDTO tarefaDTO, 
                                                 Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        TarefaDTO tarefaCriada = tarefaService.criarTarefa(tarefaDTO, usuario.getId());
        return ResponseEntity.ok(tarefaCriada);
    }
    
    @GetMapping
    public ResponseEntity<List<TarefaDTO>> buscarTarefas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarTarefasDoUsuario(usuario.getId());
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/compartilhadas")
    public ResponseEntity<List<TarefaDTO>> buscarTarefasCompartilhadas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarTarefasCompartilhadas(usuario.getId());
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TarefaDTO> buscarPorId(@PathVariable("id") Long id, 
                                                Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        TarefaDTO tarefa = tarefaService.buscarPorId(id, usuario.getId());
        return ResponseEntity.ok(tarefa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizarTarefa(@PathVariable("id") Long id, 
                                                    @RequestBody TarefaDTO tarefaDTO,
                                                    Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        TarefaDTO tarefaAtualizada = tarefaService.atualizarTarefa(id, tarefaDTO, usuario.getId());
        return ResponseEntity.ok(tarefaAtualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTarefa(@PathVariable("id") Long id, 
                                             Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        tarefaService.excluirTarefa(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/filtros")
    public ResponseEntity<List<TarefaDTO>> buscarComFiltros(
            @RequestParam(value = "status", required = false) StatusTarefa status,
            @RequestParam(value = "prioridade", required = false) PrioridadeTarefa prioridade,
            @RequestParam(value = "categoria", required = false) CategoriaTarefa categoria,
            @RequestParam(value = "termo", required = false) String termo,
            @RequestParam(value = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(value = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarComFiltros(usuario.getId(), status, prioridade, categoria, termo, inicio, fim);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TarefaDTO>> buscarPorStatus(@PathVariable("status") StatusTarefa status,
                                                          Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarPorStatus(usuario.getId(), status);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<TarefaDTO>> buscarPorPrioridade(@PathVariable("prioridade") PrioridadeTarefa prioridade,
                                                              Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarPorPrioridade(usuario.getId(), prioridade);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<TarefaDTO>> buscarPorPeriodo(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarPorPeriodo(usuario.getId(), inicio, fim);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<TarefaDTO>> buscarPorTermo(@RequestParam("termo") String termo,
                                                         Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefas = tarefaService.buscarPorTermo(usuario.getId(), termo);
        return ResponseEntity.ok(tarefas);
    }
    
    @PutMapping("/{id}/concluir")
    public ResponseEntity<TarefaDTO> marcarComoConcluida(@PathVariable("id") Long id,
                                                        Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        TarefaDTO tarefa = tarefaService.marcarComoConcluida(id, usuario.getId());
        return ResponseEntity.ok(tarefa);
    }
    
    @GetMapping("/categorias")
    public ResponseEntity<CategoriaTarefa[]> getCategorias() {
        return ResponseEntity.ok(CategoriaTarefa.values());
    }
    
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticas(Authentication authentication) {
        // Implementar estatísticas
        Map<String, Object> stats = Map.of(
            "totalTarefas", 0,
            "tarefasConcluidas", 0,
            "tarefasPendentes", 0,
            "tarefasAtrasadas", 0
        );
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/recorrente")
    public ResponseEntity<List<TarefaDTO>> criarTarefaRecorrente(@RequestBody TarefaDTO tarefaDTO, 
                                                               Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<TarefaDTO> tarefasCriadas = tarefaRecorrenteService.criarTarefasRecorrentes(tarefaDTO, usuario.getId());
        return ResponseEntity.ok(tarefasCriadas);
    }
}
