import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TarefaService } from '../../../core/services/tarefa.service';
import { Tarefa, StatusTarefa, PrioridadeTarefa, ChecklistItem, CompartilhamentoTarefa } from '../../../core/models/tarefa.model';
import { ListaChecklistComponent } from '../../checklist/components/lista-checklist/lista-checklist.component';
import { CompartilharTarefaComponent } from '../../compartilhamento/components/compartilhar-tarefa/compartilhar-tarefa.component';

@Component({
  selector: 'app-detalhes',
  standalone: true,
  imports: [CommonModule, ListaChecklistComponent, CompartilharTarefaComponent],
  templateUrl: './detalhes.component.html',
  styleUrls: ['./detalhes.component.scss']
})
export class DetalhesComponent implements OnInit {
  tarefa: Tarefa | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private tarefaService: TarefaService,
    private route: ActivatedRoute,
    public router: Router
  ) {}

  ngOnInit(): void {
    const id = +this.route.snapshot.paramMap.get('id')!;
    this.carregarTarefa(id);
  }

  carregarTarefa(id: number): void {
    this.tarefaService.buscarTarefaPorId(id).subscribe({
      next: (tarefa) => {
        this.tarefa = tarefa;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar tarefa.';
        this.isLoading = false;
      }
    });
  }

  navegarParaEditar(): void {
    this.router.navigate(['/tarefas/editar', this.tarefa?.id]);
  }

  excluirTarefa(): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.tarefaService.excluirTarefa(this.tarefa!.id).subscribe({
        next: () => {
          this.router.navigate(['/tarefas']);
        }
      });
    }
  }

  marcarComoConcluida(): void {
    this.tarefaService.marcarComoConcluida(this.tarefa!.id).subscribe({
      next: () => {
        this.carregarTarefa(this.tarefa!.id);
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

  obterCorStatus(status: StatusTarefa): string {
    switch (status) {
      case StatusTarefa.CONCLUIDA:
        return 'bg-green-100 text-green-800';
      case StatusTarefa.EM_ANDAMENTO:
        return 'bg-blue-100 text-blue-800';
      case StatusTarefa.PENDENTE:
        return 'bg-yellow-100 text-yellow-800';
      case StatusTarefa.CANCELADA:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  onChecklistAtualizado(checklist: ChecklistItem[]): void {
    if (this.tarefa) {
      this.tarefa.checklist = checklist;
    }
  }

  onCompartilhamentoAdicionado(compartilhamento: CompartilhamentoTarefa): void {
    if (this.tarefa) {
      this.tarefa.compartilhamentos = [...this.tarefa.compartilhamentos, compartilhamento];
    }
  }

  onCompartilhamentoRemovido(compartilhamentoId: number): void {
    if (this.tarefa) {
      this.tarefa.compartilhamentos = this.tarefa.compartilhamentos.filter(
        c => c.id !== compartilhamentoId
      );
    }
  }
}
