package com.arthur.tarefas.repository;

import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.TarefaRecorrenteInstancia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRecorrenteInstanciaRepository extends JpaRepository<TarefaRecorrenteInstancia, Long> {
    
    Optional<TarefaRecorrenteInstancia> findByTarefaRecorrenteAndDataInstancia(Tarefa tarefaRecorrente, LocalDate dataInstancia);
    
    List<TarefaRecorrenteInstancia> findByTarefaRecorrenteAndDataInstanciaBetween(
        Tarefa tarefaRecorrente, 
        LocalDate dataInicio, 
        LocalDate dataFim
    );
    
    @Query("SELECT t FROM TarefaRecorrenteInstancia t WHERE t.tarefaRecorrente.usuario.id = :usuarioId AND t.dataInstancia BETWEEN :dataInicio AND :dataFim")
    List<TarefaRecorrenteInstancia> findByUsuarioAndDataInstanciaBetween(
        @Param("usuarioId") Long usuarioId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    @Query("SELECT t FROM TarefaRecorrenteInstancia t WHERE t.tarefaRecorrente.id = :tarefaId AND t.dataInstancia = :dataInstancia")
    Optional<TarefaRecorrenteInstancia> findByTarefaIdAndDataInstancia(
        @Param("tarefaId") Long tarefaId,
        @Param("dataInstancia") LocalDate dataInstancia
    );
}
