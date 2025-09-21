import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TarefaService } from '../../../core/services/tarefa.service';
import { PrioridadeTarefa, CategoriaTarefa } from '../../../core/models/tarefa.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-criar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './criar.component.html',
  styleUrls: ['./criar.component.scss']
})
export class CriarComponent {
  tarefaForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  categorias: CategoriaTarefa[] = [];
  categoriaCustomizada = '';

  prioridades = [
    { value: PrioridadeTarefa.BAIXA, label: 'Baixa' },
    { value: PrioridadeTarefa.MEDIA, label: 'Média' },
    { value: PrioridadeTarefa.ALTA, label: 'Alta' },
    { value: PrioridadeTarefa.URGENTE, label: 'Urgente' }
  ];

  constructor(
    private fb: FormBuilder,
    private tarefaService: TarefaService,
    public router: Router
  ) {
    this.tarefaForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(3)]],
      descricao: [''],
      prioridade: [PrioridadeTarefa.MEDIA, [Validators.required]],
      dataVencimento: [''],
      categoria: [''],
      categoriaCustomizada: [''],
      cor: ['#3498db']
    });

    this.carregarCategorias();
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

      this.tarefaService.criarTarefa(tarefaData).subscribe({
        next: () => {
          Swal.fire({
            title: 'Criada!',
            text: 'Tarefa criada com sucesso.',
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
            text: error.error?.message || 'Erro ao criar tarefa. Tente novamente.',
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
