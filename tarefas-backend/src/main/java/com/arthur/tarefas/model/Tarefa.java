package com.arthur.tarefas.model;

import com.arthur.tarefas.enums.CategoriaTarefa;
import com.arthur.tarefas.enums.PrioridadeTarefa;
import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.enums.TipoRecorrencia;
import com.arthur.tarefas.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarefas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTarefa status = StatusTarefa.PENDENTE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeTarefa prioridade = PrioridadeTarefa.MEDIA;
    
    @Enumerated(EnumType.STRING)
    private CategoriaTarefa categoria;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_vencimento")
    private LocalDateTime dataVencimento;
    
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_pai_id")
    private Tarefa tarefaPai;
    
    @OneToMany(mappedBy = "tarefaPai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tarefa> subtarefas = new ArrayList<>();
    
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChecklistItem> checklist = new ArrayList<>();
    
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompartilhamentoTarefa> compartilhamentos = new ArrayList<>();
    
    
    @Column(name = "cor")
    private String cor = "#3498db";
    
    @Column(name = "tags")
    private String tags; // Separadas por vírgula
    
    // Campos de recorrência
    @Column(name = "is_recorrente")
    private Boolean isRecorrente = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recorrencia")
    private TipoRecorrencia tipoRecorrencia;
    
    @Column(name = "intervalo_recorrencia")
    private Integer intervaloRecorrencia = 1;
    
    @Column(name = "data_fim_recorrencia")
    private LocalDateTime dataFimRecorrencia;
    
    @ElementCollection(targetClass = DiaSemana.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "tarefa_dias_semana", joinColumns = @JoinColumn(name = "tarefa_id"))
    @Column(name = "dia_semana")
    private List<DiaSemana> diasDaSemana = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        if (status == StatusTarefa.CONCLUIDA && dataConclusao == null) {
            dataConclusao = LocalDateTime.now();
        }
    }
}
