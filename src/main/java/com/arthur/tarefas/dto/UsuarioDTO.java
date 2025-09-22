package com.arthur.tarefas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private Long id;
    private String uuid; // UUID público para segurança
    private String email;
    private String senha; // Apenas para registro, nunca retornado
    private String nome;
    private String sobrenome;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimoAcesso;
    private boolean ativo;
}
