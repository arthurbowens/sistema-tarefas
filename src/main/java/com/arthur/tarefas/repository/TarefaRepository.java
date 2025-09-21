package com.arthur.tarefas.repository;

import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    
    List<Tarefa> findByUsuario(Usuario usuario);
    
    List<Tarefa> findByUsuarioAndStatus(Usuario usuario, StatusTarefa status);
    
    List<Tarefa> findByUsuarioAndPrioridade(Usuario usuario, com.arthur.tarefas.enums.PrioridadeTarefa prioridade);
    
    @Query("SELECT t FROM Tarefa t WHERE t.usuario = :usuario AND t.dataVencimento BETWEEN :inicio AND :fim")
    List<Tarefa> findByUsuarioAndDataVencimentoBetween(@Param("usuario") Usuario usuario, 
                                                      @Param("inicio") LocalDateTime inicio, 
                                                      @Param("fim") LocalDateTime fim);
    
    @Query("SELECT t FROM Tarefa t WHERE t.usuario = :usuario AND (t.titulo LIKE %:termo% OR t.descricao LIKE %:termo%)")
    List<Tarefa> findByUsuarioAndTituloOrDescricaoContaining(@Param("usuario") Usuario usuario, 
                                                            @Param("termo") String termo);
    
    List<Tarefa> findByTarefaPai(Tarefa tarefaPai);
    
    @Query("SELECT t FROM Tarefa t WHERE t.usuario = :usuario AND t.tarefaPai IS NULL")
    List<Tarefa> findTarefasPrincipaisByUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT t FROM Tarefa t JOIN t.compartilhamentos c WHERE c.usuario = :usuario AND c.conviteAceito = true")
    List<Tarefa> findTarefasCompartilhadasComUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT COUNT(t) FROM Tarefa t WHERE t.usuario = :usuario AND t.status = :status")
    Long countByUsuarioAndStatus(@Param("usuario") Usuario usuario, @Param("status") StatusTarefa status);
}
