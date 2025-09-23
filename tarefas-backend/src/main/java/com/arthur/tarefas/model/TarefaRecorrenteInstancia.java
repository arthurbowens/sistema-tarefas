package com.arthur.tarefas.model;

import com.arthur.tarefas.enums.StatusTarefa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefa_recorrente_instancias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaRecorrenteInstancia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_recorrente_id", nullable = false)
    private Tarefa tarefaRecorrente;
    
    @Column(name = "data_instancia", nullable = false)
    private LocalDate dataInstancia;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusTarefa status = StatusTarefa.PENDENTE;
    
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (status == StatusTarefa.CONCLUIDA && dataConclusao == null) {
            dataConclusao = LocalDateTime.now();
        }
    }
}
