import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TarefaService } from '../../core/services/tarefa.service';
import { Tarefa, Estatisticas, StatusTarefa, PrioridadeTarefa } from '../../core/models/tarefa.model';
import { Usuario } from '../../core/models/usuario.model';
import { ConvitesPendentesComponent } from '../compartilhamento/components/convites-pendentes/convites-pendentes.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ConvitesPendentesComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  usuario: Usuario | null = null;
  tarefas: Tarefa[] = [];
  estatisticas: Estatisticas | null = null;
  isLoading = true;

  constructor(
    private authService: AuthService,
    private tarefaService: TarefaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.usuario = this.authService.getCurrentUser();
    
    this.tarefaService.listarTarefas().subscribe({
      next: (tarefas) => {
        this.tarefas = tarefas;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });

    this.tarefaService.obterEstatisticas().subscribe({
      next: (estatisticas) => {
        this.estatisticas = estatisticas;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  navegarParaTarefas(): void {
    this.router.navigate(['/tarefas']);
  }

  navegarParaCalendario(): void {
    this.router.navigate(['/tarefas'], { queryParams: { view: 'calendario' } });
  }

  navegarParaEstatisticas(): void {
    this.router.navigate(['/estatisticas']);
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
}
