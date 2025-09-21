package com.arthur.tarefas.repository;

import com.arthur.tarefas.model.CompartilhamentoTarefa;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompartilhamentoTarefaRepository extends JpaRepository<CompartilhamentoTarefa, Long> {
    
    List<CompartilhamentoTarefa> findByTarefa(Tarefa tarefa);
    
    List<CompartilhamentoTarefa> findByUsuario(Usuario usuario);
    
    Optional<CompartilhamentoTarefa> findByTarefaAndUsuario(Tarefa tarefa, Usuario usuario);
    
    @Query("SELECT c FROM CompartilhamentoTarefa c WHERE c.usuario = :usuario AND c.conviteAceito = true")
    List<CompartilhamentoTarefa> findAceitosByUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT c FROM CompartilhamentoTarefa c WHERE c.usuario = :usuario AND c.conviteAceito = false")
    List<CompartilhamentoTarefa> findPendentesByUsuario(@Param("usuario") Usuario usuario);
    
    boolean existsByTarefaAndUsuario(Tarefa tarefa, Usuario usuario);
}
