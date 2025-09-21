import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompartilhamentoTarefa, TipoCompartilhamento } from '../../../../core/models/tarefa.model';
import { TarefaService } from '../../../../core/services/tarefa.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Usuario } from '../../../../core/models/usuario.model';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { of, Observable } from 'rxjs';

@Component({
  selector: 'app-compartilhar-tarefa',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './compartilhar-tarefa.component.html',
  styleUrls: ['./compartilhar-tarefa.component.scss']
})
export class CompartilharTarefaComponent implements OnInit {
  @Input() tarefaId!: number;
  @Input() compartilhamentos: CompartilhamentoTarefa[] = [];
  @Output() compartilhamentoAdicionado = new EventEmitter<CompartilhamentoTarefa>();
  @Output() compartilhamentoRemovido = new EventEmitter<number>();

  isCompartilhando = false;
  emailUsuario = '';
  tipoCompartilhamento: TipoCompartilhamento = TipoCompartilhamento.LEITURA;
  isLoading = false;
  errorMessage = '';
  usuariosSugeridos: Usuario[] = [];
  mostrarSugestoes = false;

  tiposCompartilhamento = [
    { value: TipoCompartilhamento.LEITURA, label: 'Apenas Leitura', description: 'Pode visualizar a tarefa' },
    { value: TipoCompartilhamento.ESCRITA, label: 'Leitura e Escrita', description: 'Pode editar a tarefa' },
    { value: TipoCompartilhamento.ADMIN, label: 'Administrador', description: 'Controle total da tarefa' }
  ];

  constructor(
    private tarefaService: TarefaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {}

  onEmailChange(): void {
    if (this.emailUsuario.length >= 2) {
      this.tarefaService.buscarUsuariosPorEmail(this.emailUsuario)
        .pipe(
          debounceTime(300),
          distinctUntilChanged(),
          catchError(() => of([]))
        )
        .subscribe(usuarios => {
          this.usuariosSugeridos = usuarios;
          this.mostrarSugestoes = usuarios.length > 0;
        });
    } else {
      this.usuariosSugeridos = [];
      this.mostrarSugestoes = false;
    }
  }

  selecionarUsuario(usuario: Usuario): void {
    this.emailUsuario = usuario.email;
    this.usuariosSugeridos = [];
    this.mostrarSugestoes = false;
  }

  fecharSugestoes(): void {
    setTimeout(() => {
      this.mostrarSugestoes = false;
    }, 200);
  }

  iniciarCompartilhamento(): void {
    this.isCompartilhando = true;
    this.emailUsuario = '';
    this.tipoCompartilhamento = TipoCompartilhamento.LEITURA;
    this.errorMessage = '';
  }

  compartilharTarefa(): void {
    if (this.emailUsuario.trim()) {
      this.isLoading = true;
      this.errorMessage = '';

      this.tarefaService.compartilharTarefa(
        this.tarefaId,
        this.emailUsuario.trim(),
        this.tipoCompartilhamento
      ).subscribe({
        next: (novoCompartilhamento) => {
          this.compartilhamentoAdicionado.emit(novoCompartilhamento);
          this.cancelarCompartilhamento();
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Erro ao compartilhar tarefa';
          this.isLoading = false;
        }
      });
    }
  }

  cancelarCompartilhamento(): void {
    this.isCompartilhando = false;
    this.emailUsuario = '';
    this.tipoCompartilhamento = TipoCompartilhamento.LEITURA;
    this.errorMessage = '';
  }

  removerCompartilhamento(compartilhamentoId: number): void {
    if (confirm('Tem certeza que deseja remover este compartilhamento?')) {
      this.isLoading = true;
      this.tarefaService.removerCompartilhamento(this.tarefaId, compartilhamentoId).subscribe({
        next: () => {
          this.compartilhamentoRemovido.emit(compartilhamentoId);
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }

  obterCorTipoCompartilhamento(tipo: TipoCompartilhamento): string {
    switch (tipo) {
      case TipoCompartilhamento.ADMIN:
        return 'bg-red-100 text-red-800';
      case TipoCompartilhamento.ESCRITA:
        return 'bg-blue-100 text-blue-800';
      case TipoCompartilhamento.LEITURA:
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  obterLabelTipoCompartilhamento(tipo: TipoCompartilhamento): string {
    const tipoObj = this.tiposCompartilhamento.find(t => t.value === tipo);
    return tipoObj ? tipoObj.label : tipo;
  }
}
