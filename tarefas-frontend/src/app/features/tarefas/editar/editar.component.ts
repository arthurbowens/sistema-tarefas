import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TarefaService } from '../../../core/services/tarefa.service';
import { PrioridadeTarefa, StatusTarefa, CategoriaTarefa } from '../../../core/models/tarefa.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-editar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './editar.component.html',
  styleUrls: ['./editar.component.scss']
})
export class EditarComponent implements OnInit {
  tarefaForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  tarefaId: number = 0;
  categorias: CategoriaTarefa[] = [];

  prioridades = [
    { value: PrioridadeTarefa.BAIXA, label: 'Baixa' },
    { value: PrioridadeTarefa.MEDIA, label: 'Média' },
    { value: PrioridadeTarefa.ALTA, label: 'Alta' },
    { value: PrioridadeTarefa.URGENTE, label: 'Urgente' }
  ];

  status = [
    { value: StatusTarefa.PENDENTE, label: 'Pendente' },
    { value: StatusTarefa.EM_ANDAMENTO, label: 'Em Andamento' },
    { value: StatusTarefa.CONCLUIDA, label: 'Concluída' },
    { value: StatusTarefa.CANCELADA, label: 'Cancelada' }
  ];

  constructor(
    private fb: FormBuilder,
    private tarefaService: TarefaService,
    private route: ActivatedRoute,
    public router: Router
  ) {
    this.tarefaForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(3)]],
      descricao: [''],
      status: [StatusTarefa.PENDENTE, [Validators.required]],
      prioridade: [PrioridadeTarefa.MEDIA, [Validators.required]],
      dataVencimento: [''],
      categoria: [''],
      categoriaCustomizada: [''],
      cor: ['#3498db']
    });

    this.carregarCategorias();
  }

  ngOnInit(): void {
    this.tarefaId = +this.route.snapshot.paramMap.get('id')!;
    this.carregarTarefa();
  }

  carregarCategorias(): void {
    this.tarefaService.obterCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar categorias.';
      }
    });
  }

  carregarTarefa(): void {
    this.tarefaService.buscarTarefaPorId(this.tarefaId).subscribe({
      next: (tarefa) => {
        this.tarefaForm.patchValue({
          titulo: tarefa.titulo,
          descricao: tarefa.descricao,
          status: tarefa.status,
          prioridade: tarefa.prioridade,
          dataVencimento: tarefa.dataVencimento ? new Date(tarefa.dataVencimento).toISOString().slice(0, 16) : '',
          categoria: tarefa.categoria || '',
          categoriaCustomizada: '',
          cor: tarefa.cor
        });
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar tarefa.';
      }
    });
  }

  onSubmit(): void {
    if (this.tarefaForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const categoria = this.tarefaForm.value.categoria === 'OUTROS' 
        ? this.tarefaForm.value.categoriaCustomizada 
        : this.tarefaForm.value.categoria;

      const tarefaData = {
        ...this.tarefaForm.value,
        categoria: categoria || null,
        dataVencimento: this.tarefaForm.value.dataVencimento || null
      };

      this.tarefaService.atualizarTarefa(this.tarefaId, tarefaData).subscribe({
        next: () => {
          Swal.fire({
            title: 'Atualizada!',
            text: 'Tarefa atualizada com sucesso.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          }).then(() => {
            this.router.navigate(['/tarefas']);
          });
        },
        error: (error) => {
          Swal.fire({
            title: 'Erro!',
            text: error.error?.message || 'Erro ao atualizar tarefa. Tente novamente.',
            icon: 'error',
            confirmButtonText: 'OK'
          });
          this.isLoading = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/tarefas']);
  }
}
