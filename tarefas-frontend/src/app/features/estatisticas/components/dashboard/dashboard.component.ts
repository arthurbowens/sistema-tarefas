import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TarefaService } from '../../../../core/services/tarefa.service';
import { Estatisticas, PrioridadeTarefa } from '../../../../core/models/tarefa.model';

@Component({
  selector: 'app-dashboard-estatisticas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardEstatisticasComponent implements OnInit {
  estatisticas: Estatisticas | null = null;
  isLoading = true;
  PrioridadeTarefa = PrioridadeTarefa;

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.carregarEstatisticas();
  }

  carregarEstatisticas(): void {
    this.isLoading = true;
    this.tarefaService.obterEstatisticas().subscribe({
      next: (estatisticas) => {
        this.estatisticas = estatisticas;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  obterCorPrioridade(prioridade: PrioridadeTarefa): string {
    switch (prioridade) {
      case PrioridadeTarefa.URGENTE:
        return 'bg-red-100 text-red-800';
      case PrioridadeTarefa.ALTA:
        return 'bg-orange-100 text-orange-800';
      case PrioridadeTarefa.MEDIA:
        return 'bg-yellow-100 text-yellow-800';
      case PrioridadeTarefa.BAIXA:
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  obterLabelPrioridade(prioridade: PrioridadeTarefa): string {
    switch (prioridade) {
      case PrioridadeTarefa.URGENTE:
        return 'Urgente';
      case PrioridadeTarefa.ALTA:
        return 'Alta';
      case PrioridadeTarefa.MEDIA:
        return 'Média';
      case PrioridadeTarefa.BAIXA:
        return 'Baixa';
      default:
        return prioridade;
    }
  }

  obterPercentualConcluidas(): number {
    if (!this.estatisticas || this.estatisticas.totalTarefas === 0) return 0;
    return Math.round((this.estatisticas.tarefasConcluidas / this.estatisticas.totalTarefas) * 100);
  }

  obterPercentualAtrasadas(): number {
    if (!this.estatisticas || this.estatisticas.totalTarefas === 0) return 0;
    return Math.round((this.estatisticas.tarefasAtrasadas / this.estatisticas.totalTarefas) * 100);
  }

  obterCategorias(): string[] {
    if (!this.estatisticas || !this.estatisticas.tarefasPorCategoria) return [];
    return Object.keys(this.estatisticas.tarefasPorCategoria);
  }

  obterPrioridades(): PrioridadeTarefa[] {
    return [PrioridadeTarefa.URGENTE, PrioridadeTarefa.ALTA, PrioridadeTarefa.MEDIA, PrioridadeTarefa.BAIXA];
  }

  obterTarefasPorPrioridade(prioridade: PrioridadeTarefa): number {
    if (!this.estatisticas || !this.estatisticas.tarefasPorPrioridade) return 0;
    return this.estatisticas.tarefasPorPrioridade[prioridade] || 0;
  }
}
