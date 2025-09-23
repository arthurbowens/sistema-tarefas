package com.arthur.tarefas.repository;

import com.arthur.tarefas.model.ChecklistItem;
import com.arthur.tarefas.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    
    List<ChecklistItem> findByTarefaOrderByOrdem(Tarefa tarefa);
    
    @Query("SELECT c FROM ChecklistItem c WHERE c.tarefa = :tarefa AND c.concluido = false ORDER BY c.ordem")
    List<ChecklistItem> findPendentesByTarefa(@Param("tarefa") Tarefa tarefa);
    
    @Query("SELECT COUNT(c) FROM ChecklistItem c WHERE c.tarefa = :tarefa")
    Long countByTarefa(@Param("tarefa") Tarefa tarefa);
    
    @Query("SELECT COUNT(c) FROM ChecklistItem c WHERE c.tarefa = :tarefa AND c.concluido = true")
    Long countConcluidosByTarefa(@Param("tarefa") Tarefa tarefa);
}
