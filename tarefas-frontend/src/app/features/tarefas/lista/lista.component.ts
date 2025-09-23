import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TarefaService } from '../../../core/services/tarefa.service';
import { Tarefa, StatusTarefa, PrioridadeTarefa, CategoriaTarefa } from '../../../core/models/tarefa.model';
import { CalendarioTarefasComponent } from './calendario-tarefas.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, CalendarioTarefasComponent],
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.scss']
})
export class ListaComponent implements OnInit {
  @ViewChild('calendarioRef') calendarioRef: any;
  
  tarefas: Tarefa[] = [];
  isLoading = true;
  filtroStatus: StatusTarefa | null = null;
  filtroPrioridade: PrioridadeTarefa | null = null;
  filtroCategoria: CategoriaTarefa | null = null;
  termoBusca = '';
  categorias: CategoriaTarefa[] = [];
  visualizacaoAtual: 'lista' | 'calendario' = 'lista';
  dataSelecionada = new Date();

  constructor(
    private tarefaService: TarefaService,
    public router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.carregarTarefas();
    this.carregarCategorias();
    
    // Verificar se deve mostrar o calendário por padrão
    this.route.queryParams.subscribe(params => {
      if (params['view'] === 'calendario') {
        this.visualizacaoAtual = 'calendario';
      }
    });
  }

  carregarTarefas(): void {
    this.isLoading = true;
    this.tarefaService.listarTarefas().subscribe({
      next: (tarefas) => {
        this.tarefas = [...tarefas]; // Criar nova referência para disparar ngOnChanges
        this.isLoading = false;
        
        // Regenerar calendário se estiver na visualização de calendário
        if (this.visualizacaoAtual === 'calendario' && this.calendarioRef) {
          setTimeout(() => {
            this.calendarioRef.regenerarCalendario();
          }, 100);
        }
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  filtrarPorStatus(status: StatusTarefa | null): void {
    this.filtroStatus = status;
    this.aplicarFiltros();
  }

  filtrarPorPrioridade(prioridade: PrioridadeTarefa | null): void {
    this.filtroPrioridade = prioridade;
    this.aplicarFiltros();
  }

  filtrarPorCategoria(categoria: CategoriaTarefa | null): void {
    this.filtroCategoria = categoria;
    this.aplicarFiltros();
  }

  buscarPorTermo(): void {
    this.aplicarFiltros();
  }

  aplicarFiltros(): void {
    this.isLoading = true;
    
    const filtros: any = {};
    
    if (this.filtroStatus) {
      filtros.status = this.filtroStatus;
    }
    
    if (this.filtroPrioridade) {
      filtros.prioridade = this.filtroPrioridade;
    }
    
    if (this.filtroCategoria) {
      filtros.categoria = this.filtroCategoria;
    }
    
    if (this.termoBusca && this.termoBusca.trim()) {
      filtros.termo = this.termoBusca.trim();
    }
    
    this.tarefaService.buscarComFiltros(filtros).subscribe({
      next: (tarefas) => {
        this.tarefas = tarefas;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  limparFiltros(): void {
    this.filtroStatus = null;
    this.filtroPrioridade = null;
    this.filtroCategoria = null;
    this.termoBusca = '';
    this.carregarTarefas();
  }

  carregarCategorias(): void {
    this.tarefaService.obterCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      }
    });
  }


  navegarParaDetalhes(id: number): void {
    this.router.navigate(['/tarefas/detalhes', id]);
  }

  navegarParaEditar(id: number): void {
    this.router.navigate(['/tarefas/editar', id]);
  }

  excluirTarefa(id: number): void {
    Swal.fire({
      title: 'Excluir Tarefa',
      text: 'Tem certeza que deseja excluir esta tarefa?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sim, excluir!',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.tarefaService.excluirTarefa(id).subscribe({
          next: () => {
            Swal.fire({
              title: 'Excluída!',
              text: 'Tarefa excluída com sucesso.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.carregarTarefas();
          },
          error: (error) => {
            Swal.fire({
              title: 'Erro!',
              text: error.error?.message || 'Erro ao excluir tarefa',
              icon: 'error',
              confirmButtonText: 'OK'
            });
          }
        });
      }
    });
  }

  marcarComoConcluida(id: number): void {
    Swal.fire({
      title: 'Marcar como Concluída',
      text: 'Deseja marcar esta tarefa como concluída?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sim, concluir!',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.tarefaService.marcarComoConcluida(id).subscribe({
          next: () => {
            Swal.fire({
              title: 'Concluída!',
              text: 'Tarefa marcada como concluída.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.carregarTarefas();
          },
          error: (error) => {
            Swal.fire({
              title: 'Erro!',
              text: error.error?.message || 'Erro ao marcar como concluída',
              icon: 'error',
              confirmButtonText: 'OK'
            });
          }
        });
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

  getChecklistConcluidos(tarefa: Tarefa): number {
    if (!tarefa.checklist) return 0;
    return tarefa.checklist.filter(item => item.concluido).length;
  }

  getChecklistTotal(tarefa: Tarefa): number {
    return tarefa.checklist ? tarefa.checklist.length : 0;
  }

  getCompartilhamentosTotal(tarefa: Tarefa): number {
    return tarefa.compartilhamentos ? tarefa.compartilhamentos.length : 0;
  }

  alternarVisualizacao(): void {
    this.visualizacaoAtual = this.visualizacaoAtual === 'lista' ? 'calendario' : 'lista';
  }

  onDiaSelecionado(data: Date): void {
    this.dataSelecionada = data;
    // Filtrar tarefas pela data selecionada
    this.filtrarPorData(data);
  }


  filtrarPorData(data: Date): void {
    const dataInicio = new Date(data);
    dataInicio.setHours(0, 0, 0, 0);
    
    const dataFim = new Date(data);
    dataFim.setHours(23, 59, 59, 999);

    this.tarefaService.buscarComFiltros({
      inicio: dataInicio.toISOString(),
      fim: dataFim.toISOString()
    }).subscribe({
      next: (tarefas) => {
        this.tarefas = tarefas;
      },
      error: () => {
        // Em caso de erro, manter lista atual
      }
    });
  }
}
