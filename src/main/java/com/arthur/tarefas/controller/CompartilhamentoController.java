package com.arthur.tarefas.controller;

import com.arthur.tarefas.dto.CompartilhamentoTarefaDTO;
import com.arthur.tarefas.enums.TipoCompartilhamento;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.service.CompartilhamentoTarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas/{tarefaId}/compartilhamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompartilhamentoController {
    
    private final CompartilhamentoTarefaService compartilhamentoService;
    
    @PostMapping
    public ResponseEntity<CompartilhamentoTarefaDTO> compartilharTarefa(
            @PathVariable Long tarefaId,
            @RequestParam String emailUsuario,
            @RequestParam TipoCompartilhamento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataExpiracao,
            Authentication authentication) {
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        CompartilhamentoTarefaDTO compartilhamento = compartilhamentoService.compartilharTarefa(
            tarefaId, usuario.getId(), emailUsuario, tipo, dataExpiracao);
        return ResponseEntity.ok(compartilhamento);
    }
    
    @GetMapping
    public ResponseEntity<List<CompartilhamentoTarefaDTO>> buscarCompartilhamentos(@PathVariable Long tarefaId) {
        List<CompartilhamentoTarefaDTO> compartilhamentos = compartilhamentoService.buscarCompartilhamentosDaTarefa(tarefaId);
        return ResponseEntity.ok(compartilhamentos);
    }
    
    @PutMapping("/{id}/aceitar")
    public ResponseEntity<Void> aceitarConvite(@PathVariable Long tarefaId,
                                              @PathVariable Long id,
                                              Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        compartilhamentoService.aceitarConvite(id, usuario.getId());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarConvite(@PathVariable Long tarefaId,
                                               @PathVariable Long id,
                                               Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        compartilhamentoService.rejeitarConvite(id, usuario.getId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCompartilhamento(@PathVariable Long tarefaId,
                                                        @PathVariable Long id,
                                                        Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        compartilhamentoService.removerCompartilhamento(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/convites")
    public ResponseEntity<List<CompartilhamentoTarefaDTO>> buscarConvitesPendentes(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<CompartilhamentoTarefaDTO> convites = compartilhamentoService.buscarConvitesPendentes(usuario.getId());
        return ResponseEntity.ok(convites);
    }
}
