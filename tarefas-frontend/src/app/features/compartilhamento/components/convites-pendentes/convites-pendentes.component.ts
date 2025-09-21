import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CompartilhamentoTarefa, TipoCompartilhamento } from '../../../../core/models/tarefa.model';
import { TarefaService } from '../../../../core/services/tarefa.service';

@Component({
  selector: 'app-convites-pendentes',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card" *ngIf="convites.length > 0">
      <div class="card-header">
        <h3 class="text-lg font-medium text-gray-900">Convites Pendentes</h3>
      </div>
      
      <div class="space-y-3">
        <div *ngFor="let convite of convites" 
             class="flex items-center justify-between p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
          <div class="flex items-center space-x-3">
            <div class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
              <span class="text-sm font-medium text-primary-600">
                {{ convite.tarefa.titulo.charAt(0) }}
              </span>
            </div>
            <div>
              <p class="text-sm font-medium text-gray-900">
                {{ convite.tarefa.titulo }}
              </p>
              <p class="text-xs text-gray-500">
                Compartilhado por: {{ convite.tarefa.usuario.nome }}
              </p>
              <p class="text-xs text-gray-400">
                Tipo: {{ obterLabelTipoCompartilhamento(convite.tipoCompartilhamento) }}
              </p>
            </div>
          </div>
          <div class="flex items-center space-x-2">
            <button
              (click)="aceitarConvite(convite.id)"
              [disabled]="isLoading"
              class="px-3 py-1 bg-green-600 text-white text-sm rounded-md hover:bg-green-700 disabled:opacity-50">
              Aceitar
            </button>
            <button
              (click)="rejeitarConvite(convite.id)"
              [disabled]="isLoading"
              class="px-3 py-1 bg-red-600 text-white text-sm rounded-md hover:bg-red-700 disabled:opacity-50">
              Rejeitar
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .card {
      @apply bg-white rounded-lg shadow-sm border border-gray-200 p-4;
    }
    .card-header {
      @apply border-b border-gray-200 pb-3 mb-4;
    }
  `]
})
export class ConvitesPendentesComponent implements OnInit {
  convites: CompartilhamentoTarefa[] = [];
  isLoading = false;

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.carregarConvites();
  }

  carregarConvites(): void {
    console.log('Carregando convites pendentes...');
    this.tarefaService.buscarConvitesPendentes().subscribe({
      next: (convites) => {
        console.log('Convites carregados:', convites);
        this.convites = convites;
      },
      error: (error) => {
        console.error('Erro ao carregar convites:', error);
      }
    });
  }

  aceitarConvite(compartilhamentoId: number): void {
    console.log('Aceitando convite:', compartilhamentoId);
    this.isLoading = true;
    this.tarefaService.aceitarConvite(compartilhamentoId).subscribe({
      next: () => {
        console.log('Convite aceito com sucesso');
        this.carregarConvites();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao aceitar convite:', error);
        this.isLoading = false;
      }
    });
  }

  rejeitarConvite(compartilhamentoId: number): void {
    this.isLoading = true;
    this.tarefaService.rejeitarConvite(compartilhamentoId).subscribe({
      next: () => {
        this.carregarConvites();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao rejeitar convite:', error);
        this.isLoading = false;
      }
    });
  }

  obterLabelTipoCompartilhamento(tipo: TipoCompartilhamento): string {
    switch (tipo) {
      case TipoCompartilhamento.ADMIN:
        return 'Administrador';
      case TipoCompartilhamento.ESCRITA:
        return 'Leitura e Escrita';
      case TipoCompartilhamento.LEITURA:
        return 'Apenas Leitura';
      default:
        return tipo;
    }
  }
}
