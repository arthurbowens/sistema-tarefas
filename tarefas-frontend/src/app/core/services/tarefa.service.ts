import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa, StatusTarefa, PrioridadeTarefa, CategoriaTarefa, ChecklistItem, CompartilhamentoTarefa, Estatisticas } from '../models/tarefa.model';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class TarefaService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // Tarefas
  criarTarefa(tarefa: Partial<Tarefa>): Observable<Tarefa> {
    return this.http.post<Tarefa>(`${this.API_URL}/tarefas`, tarefa);
  }

  listarTarefas(): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas`);
  }

  listarTarefasCompartilhadas(): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas/compartilhadas`);
  }

  buscarTarefaPorId(id: number): Observable<Tarefa> {
    return this.http.get<Tarefa>(`${this.API_URL}/tarefas/${id}`);
  }

  atualizarTarefa(id: number, tarefa: Partial<Tarefa>): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.API_URL}/tarefas/${id}`, tarefa);
  }

  excluirTarefa(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/tarefas/${id}`);
  }

  marcarComoConcluida(id: number): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.API_URL}/tarefas/${id}/concluir`, {});
  }

  buscarComFiltros(filtros: {
    status?: StatusTarefa;
    prioridade?: PrioridadeTarefa;
    categoria?: CategoriaTarefa;
    termo?: string;
    inicio?: string;
    fim?: string;
  }): Observable<Tarefa[]> {
    let params = new HttpParams();
    
    if (filtros.status) {
      params = params.set('status', filtros.status);
    }
    if (filtros.prioridade) {
      params = params.set('prioridade', filtros.prioridade);
    }
    if (filtros.categoria) {
      params = params.set('categoria', filtros.categoria);
    }
    if (filtros.termo) {
      params = params.set('termo', filtros.termo);
    }
    if (filtros.inicio) {
      params = params.set('inicio', filtros.inicio);
    }
    if (filtros.fim) {
      params = params.set('fim', filtros.fim);
    }
    
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas/filtros`, { params });
  }

  buscarPorStatus(status: StatusTarefa): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas/status/${status}`);
  }

  buscarPorPrioridade(prioridade: PrioridadeTarefa): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas/prioridade/${prioridade}`);
  }

  buscarPorPeriodo(inicio: string, fim: string): Observable<Tarefa[]> {
    const params = new HttpParams()
      .set('inicio', inicio)
      .set('fim', fim);
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas/periodo`, { params });
  }

  buscarPorTermo(termo: string): Observable<Tarefa[]> {
    const params = new HttpParams().set('termo', termo);
    return this.http.get<Tarefa[]>(`${this.API_URL}/tarefas/buscar`, { params });
  }

  // Checklist
  criarChecklistItem(tarefaId: number, item: Partial<ChecklistItem>): Observable<ChecklistItem> {
    return this.http.post<ChecklistItem>(`${this.API_URL}/tarefas/${tarefaId}/checklist`, item);
  }

  atualizarChecklistItem(tarefaId: number, id: number, item: Partial<ChecklistItem>): Observable<ChecklistItem> {
    return this.http.put<ChecklistItem>(`${this.API_URL}/tarefas/${tarefaId}/checklist/${id}`, item);
  }

  excluirChecklistItem(tarefaId: number, id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/tarefas/${tarefaId}/checklist/${id}`);
  }

  marcarChecklistItemConcluido(tarefaId: number, id: number): Observable<ChecklistItem> {
    return this.http.put<ChecklistItem>(`${this.API_URL}/tarefas/${tarefaId}/checklist/${id}/concluir`, {});
  }

  // Compartilhamento
  compartilharTarefa(tarefaId: number, emailUsuario: string, tipo: string, dataExpiracao?: string): Observable<CompartilhamentoTarefa> {
    const params = new HttpParams()
      .set('emailUsuario', emailUsuario)
      .set('tipo', tipo);
    
    if (dataExpiracao) {
      params.set('dataExpiracao', dataExpiracao);
    }
    
    return this.http.post<CompartilhamentoTarefa>(`${this.API_URL}/tarefas/${tarefaId}/compartilhamento`, null, { params });
  }

  listarCompartilhamentos(tarefaId: number): Observable<CompartilhamentoTarefa[]> {
    return this.http.get<CompartilhamentoTarefa[]>(`${this.API_URL}/tarefas/${tarefaId}/compartilhamento`);
  }

  removerCompartilhamento(tarefaId: number, id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/tarefas/${tarefaId}/compartilhamento/${id}`);
  }

  buscarConvitesPendentes(): Observable<CompartilhamentoTarefa[]> {
    return this.http.get<CompartilhamentoTarefa[]>(`${this.API_URL}/compartilhamento/convites`);
  }

  aceitarConvite(compartilhamentoId: number): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/compartilhamento/${compartilhamentoId}/aceitar`, {});
  }

  rejeitarConvite(compartilhamentoId: number): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/compartilhamento/${compartilhamentoId}/rejeitar`, {});
  }

  // Categorias
  obterCategorias(): Observable<CategoriaTarefa[]> {
    return this.http.get<CategoriaTarefa[]>(`${this.API_URL}/tarefas/categorias`);
  }

  // Estatísticas
  obterEstatisticas(): Observable<Estatisticas> {
    return this.http.get<Estatisticas>(`${this.API_URL}/estatisticas`);
  }

  // Usuários
  buscarUsuariosPorEmail(email: string): Observable<Usuario[]> {
    const params = new HttpParams().set('email', email);
    return this.http.get<Usuario[]>(`${this.API_URL}/auth/usuarios/buscar`, { params });
  }

  // Google Calendar
  obterUrlAutenticacaoGoogle(): Observable<{ authUrl: string }> {
    return this.http.get<{ authUrl: string }>(`${this.API_URL}/google-calendar/auth-url`);
  }

  sincronizarComGoogle(code: string): Observable<any> {
    return this.http.post(`${this.API_URL}/google-calendar/callback`, { code });
  }

  desconectarGoogleCalendar(): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/google-calendar/desconectar`);
  }
}
