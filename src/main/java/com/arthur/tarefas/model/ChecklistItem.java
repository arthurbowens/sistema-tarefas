package com.arthur.tarefas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "checklist_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false)
    private boolean concluido = false;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
    
    @Column(name = "ordem")
    private Integer ordem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (concluido && dataConclusao == null) {
            dataConclusao = LocalDateTime.now();
        } else if (!concluido) {
            dataConclusao = null;
        }
    }
}
