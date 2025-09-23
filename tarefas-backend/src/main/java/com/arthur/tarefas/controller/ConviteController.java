package com.arthur.tarefas.controller;

import com.arthur.tarefas.dto.CompartilhamentoTarefaDTO;
import com.arthur.tarefas.model.Usuario;
import com.arthur.tarefas.service.CompartilhamentoTarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compartilhamento")
@RequiredArgsConstructor
public class ConviteController {
    
    private final CompartilhamentoTarefaService compartilhamentoService;
    
    @GetMapping("/convites")
    public ResponseEntity<List<CompartilhamentoTarefaDTO>> buscarConvitesPendentes(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<CompartilhamentoTarefaDTO> convites = compartilhamentoService.buscarConvitesPendentes(usuario.getId());
        return ResponseEntity.ok(convites);
    }
    
    @PutMapping("/{id}/aceitar")
    public ResponseEntity<Void> aceitarConvite(@PathVariable("id") Long id,
                                              Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        compartilhamentoService.aceitarConvite(id, usuario.getId());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarConvite(@PathVariable("id") Long id,
                                               Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        compartilhamentoService.rejeitarConvite(id, usuario.getId());
        return ResponseEntity.ok().build();
    }
}
