package com.arthur.tarefas.service;

import com.arthur.tarefas.dto.TarefaRecorrenteInstanciaDTO;
import com.arthur.tarefas.enums.DiaSemana;
import com.arthur.tarefas.enums.StatusTarefa;
import com.arthur.tarefas.model.Tarefa;
import com.arthur.tarefas.model.TarefaRecorrenteInstancia;
import com.arthur.tarefas.repository.TarefaRecorrenteInstanciaRepository;
import com.arthur.tarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TarefaRecorrenteInstanciaService {
    
    private final TarefaRecorrenteInstanciaRepository instanciaRepository;
    private final TarefaRepository tarefaRepository;
    
    /**
     * Gera instâncias de tarefas recorrentes para um período específico
     */
    public List<TarefaRecorrenteInstanciaDTO> gerarInstanciasParaPeriodo(Long usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        List<Tarefa> tarefasRecorrentes = tarefaRepository.findByUsuarioIdAndIsRecorrenteTrue(usuarioId);
        List<TarefaRecorrenteInstanciaDTO> instancias = new ArrayList<>();
        
        for (Tarefa tarefa : tarefasRecorrentes) {
            List<TarefaRecorrenteInstanciaDTO> instanciasTarefa = gerarInstanciasParaTarefa(tarefa, dataInicio, dataFim);
            instancias.addAll(instanciasTarefa);
        }
        
        return instancias;
    }
    
    /**
     * Gera instâncias para uma tarefa recorrente específica
     */
    public List<TarefaRecorrenteInstanciaDTO> gerarInstanciasParaTarefa(Tarefa tarefa, LocalDate dataInicio, LocalDate dataFim) {
        List<TarefaRecorrenteInstanciaDTO> instancias = new ArrayList<>();
        
        if (!tarefa.getIsRecorrente()) {
            return instancias;
        }
        
        LocalDate dataAtual = dataInicio;
        
        while (!dataAtual.isAfter(dataFim)) {
            if (deveGerarInstanciaParaData(tarefa, dataAtual)) {
                // Verificar se já existe instância para esta data
                Optional<TarefaRecorrenteInstancia> instanciaExistente = 
                    instanciaRepository.findByTarefaRecorrenteAndDataInstancia(tarefa, dataAtual);
                
                if (instanciaExistente.isEmpty()) {
                    // Criar nova instância
                    TarefaRecorrenteInstancia instancia = new TarefaRecorrenteInstancia();
                    instancia.setTarefaRecorrente(tarefa);
                    instancia.setDataInstancia(dataAtual);
                    instancia.setStatus(StatusTarefa.PENDENTE);
                    
                    instanciaRepository.save(instancia);
                    instancias.add(converterParaDTO(instancia));
                } else {
                    // Usar instância existente
                    instancias.add(converterParaDTO(instanciaExistente.get()));
                }
            }
            
            dataAtual = dataAtual.plusDays(1);
        }
        
        return instancias;
    }
    
    /**
     * Verifica se deve gerar uma instância para a data específica
     */
    private boolean deveGerarInstanciaParaData(Tarefa tarefa, LocalDate data) {
        if (tarefa.getTipoRecorrencia() == null) {
            return false;
        }
        
        switch (tarefa.getTipoRecorrencia()) {
            case DIARIA:
                return true;
            case SEMANAL:
                if (tarefa.getDiasDaSemana() == null || tarefa.getDiasDaSemana().isEmpty()) {
                    return false;
                }
                DiaSemana diaSemana = DiaSemana.values()[data.getDayOfWeek().getValue() % 7];
                return tarefa.getDiasDaSemana().contains(diaSemana);
            case MENSAL:
                // Implementar lógica para recorrência mensal
                return data.getDayOfMonth() == tarefa.getDataCriacao().getDayOfMonth();
            case ANUAL:
                // Implementar lógica para recorrência anual
                return data.getMonthValue() == tarefa.getDataCriacao().getMonthValue() && 
                       data.getDayOfMonth() == tarefa.getDataCriacao().getDayOfMonth();
            default:
                return false;
        }
    }
    
    /**
     * Marca uma instância específica como concluída
     */
    public TarefaRecorrenteInstanciaDTO marcarInstanciaComoConcluida(Long tarefaId, LocalDate dataInstancia) {
        Optional<TarefaRecorrenteInstancia> instanciaOpt = 
            instanciaRepository.findByTarefaIdAndDataInstancia(tarefaId, dataInstancia);
        
        if (instanciaOpt.isEmpty()) {
            throw new RuntimeException("Instância não encontrada");
        }
        
        TarefaRecorrenteInstancia instancia = instanciaOpt.get();
        instancia.setStatus(StatusTarefa.CONCLUIDA);
        instancia.setDataConclusao(LocalDateTime.now());
        
        instanciaRepository.save(instancia);
        return converterParaDTO(instancia);
    }
    
    /**
     * Converte entidade para DTO
     */
    private TarefaRecorrenteInstanciaDTO converterParaDTO(TarefaRecorrenteInstancia instancia) {
        TarefaRecorrenteInstanciaDTO dto = new TarefaRecorrenteInstanciaDTO();
        dto.setId(instancia.getId());
        dto.setTarefaRecorrenteId(instancia.getTarefaRecorrente().getId());
        dto.setDataInstancia(instancia.getDataInstancia());
        dto.setStatus(instancia.getStatus());
        dto.setDataConclusao(instancia.getDataConclusao());
        dto.setDataCriacao(instancia.getDataCriacao());
        
        // Dados da tarefa recorrente
        Tarefa tarefa = instancia.getTarefaRecorrente();
        dto.setTitulo(tarefa.getTitulo());
        dto.setDescricao(tarefa.getDescricao());
        dto.setPrioridade(tarefa.getPrioridade().toString());
        dto.setCategoria(tarefa.getCategoria() != null ? tarefa.getCategoria().toString() : null);
        dto.setCor(tarefa.getCor());
        dto.setTags(tarefa.getTags());
        
        return dto;
    }
}
