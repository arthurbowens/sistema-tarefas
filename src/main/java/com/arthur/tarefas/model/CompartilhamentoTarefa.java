package com.arthur.tarefas.model;

import com.arthur.tarefas.enums.TipoCompartilhamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "compartilhamentos_tarefa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompartilhamentoTarefa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCompartilhamento tipo;
    
    @Column(name = "data_compartilhamento")
    private LocalDateTime dataCompartilhamento;
    
    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;
    
    @Column(name = "convite_aceito")
    private boolean conviteAceito = false;
    
    @PrePersist
    protected void onCreate() {
        dataCompartilhamento = LocalDateTime.now();
    }
}
