import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TarefaService } from '../../../core/services/tarefa.service';
import { PrioridadeTarefa, CategoriaTarefa, TipoRecorrencia, DiaSemana } from '../../../core/models/tarefa.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-criar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './criar.component.html',
  styleUrls: ['./criar.component.scss']
})
export class CriarComponent implements OnInit {
  tarefaForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  categorias: CategoriaTarefa[] = [];
  categoriaCustomizada = '';

  // Objeto para nova tarefa (igual ao do calendário)
  novaTarefa = {
    titulo: '',
    descricao: '',
    prioridade: PrioridadeTarefa.MEDIA,
    categoria: null as CategoriaTarefa | null,
    dataVencimento: '',
    isRecorrente: false,
    tipoRecorrencia: TipoRecorrencia.SEMANAL,
    intervaloRecorrencia: 1,
    diasDaSemana: [] as DiaSemana[],
    dataFimRecorrencia: ''
  };

  prioridades = [
    { value: PrioridadeTarefa.BAIXA, label: 'Baixa' },
    { value: PrioridadeTarefa.MEDIA, label: 'Média' },
    { value: PrioridadeTarefa.ALTA, label: 'Alta' },
    { value: PrioridadeTarefa.URGENTE, label: 'Urgente' }
  ];

  tiposRecorrencia = [
    { value: TipoRecorrencia.DIARIA, label: 'Diária' },
    { value: TipoRecorrencia.SEMANAL, label: 'Semanal' },
    { value: TipoRecorrencia.MENSAL, label: 'Mensal' },
    { value: TipoRecorrencia.ANUAL, label: 'Anual' }
  ];

  diasSemana = [
    { value: DiaSemana.SEGUNDA, label: 'Seg' },
    { value: DiaSemana.TERCA, label: 'Ter' },
    { value: DiaSemana.QUARTA, label: 'Qua' },
    { value: DiaSemana.QUINTA, label: 'Qui' },
    { value: DiaSemana.SEXTA, label: 'Sex' },
    { value: DiaSemana.SABADO, label: 'Sáb' },
    { value: DiaSemana.DOMINGO, label: 'Dom' }
  ];

  constructor(
    private fb: FormBuilder,
    private tarefaService: TarefaService,
    public router: Router,
    private route: ActivatedRoute
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

  ngOnInit(): void {
    // Verificar se há uma data pré-selecionada nos query params
    this.route.queryParams.subscribe(params => {
      if (params['data']) {
        this.tarefaForm.patchValue({
          dataVencimento: params['data']
        });
      }
    });
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

  onTipoRecorrenciaChange(): void {
    // Limpar dias da semana quando mudar o tipo de recorrência
    if (this.novaTarefa.tipoRecorrencia !== TipoRecorrencia.SEMANAL) {
      this.novaTarefa.diasDaSemana = [];
    }
  }

  alternarDiaSemana(dia: DiaSemana): void {
    const index = this.novaTarefa.diasDaSemana.indexOf(dia);
    if (index > -1) {
      this.novaTarefa.diasDaSemana.splice(index, 1);
    } else {
      this.novaTarefa.diasDaSemana.push(dia);
    }
  }

  isDiaSelecionado(dia: DiaSemana): boolean {
    return this.novaTarefa.diasDaSemana.includes(dia);
  }

  onSubmit(): void {
    if (this.novaTarefa.titulo.trim()) {
      this.isLoading = true;
      this.errorMessage = '';

      const tarefaData: any = {
        titulo: this.novaTarefa.titulo,
        descricao: this.novaTarefa.descricao,
        prioridade: this.novaTarefa.prioridade,
        categoria: this.novaTarefa.categoria,
        isRecorrente: this.novaTarefa.isRecorrente,
        tipoRecorrencia: this.novaTarefa.isRecorrente ? this.novaTarefa.tipoRecorrencia : undefined,
        intervaloRecorrencia: this.novaTarefa.isRecorrente ? this.novaTarefa.intervaloRecorrencia : undefined,
        diasDaSemana: this.novaTarefa.isRecorrente && this.novaTarefa.tipoRecorrencia === TipoRecorrencia.SEMANAL 
          ? this.novaTarefa.diasDaSemana : undefined
      };

      // Só adicionar data de vencimento se foi preenchida
      if (this.novaTarefa.dataVencimento && this.novaTarefa.dataVencimento.trim()) {
        tarefaData.dataVencimento = this.novaTarefa.dataVencimento + 'T08:00:00';
      }

      // Adicionar data de fim da recorrência se foi preenchida
      if (this.novaTarefa.isRecorrente && this.novaTarefa.dataFimRecorrencia && this.novaTarefa.dataFimRecorrencia.trim()) {
        tarefaData.dataFimRecorrencia = this.novaTarefa.dataFimRecorrencia + 'T23:59:59';
      }

      if (this.novaTarefa.isRecorrente) {
        this.criarTarefaRecorrente(tarefaData);
      } else {
        this.criarTarefaSimples(tarefaData);
      }
    }
  }

  criarTarefaSimples(tarefaData: any): void {
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
        this.isLoading = false;
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

  criarTarefaRecorrente(tarefaData: any): void {
    this.tarefaService.criarTarefaRecorrente(tarefaData).subscribe({
      next: () => {
        Swal.fire({
          title: 'Criada!',
          text: 'Tarefa recorrente criada com sucesso.',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        }).then(() => {
          this.router.navigate(['/tarefas']);
        });
        this.isLoading = false;
      },
      error: (error) => {
        Swal.fire({
          title: 'Erro!',
          text: error.error?.message || 'Erro ao criar tarefa recorrente. Tente novamente.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
        this.isLoading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/tarefas']);
  }
}
