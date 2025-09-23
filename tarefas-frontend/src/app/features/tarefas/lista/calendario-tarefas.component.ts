import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Tarefa, StatusTarefa, PrioridadeTarefa, TipoRecorrencia, DiaSemana, CategoriaTarefa, CompartilhamentoTarefa } from '../../../core/models/tarefa.model';
import { TarefaRecorrenteInstancia } from '../../../core/models/tarefa-recorrente-instancia.model';
import { TarefaService } from '../../../core/services/tarefa.service';
import { CompartilharTarefaComponent } from '../../compartilhamento/components/compartilhar-tarefa/compartilhar-tarefa.component';
import Swal from 'sweetalert2';

export interface DiaCalendario {
  data: Date;
  numero: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  isSelected: boolean;
  tarefas: Tarefa[];
}

@Component({
  selector: 'app-calendario-tarefas',
  standalone: true,
  imports: [CommonModule, FormsModule, CompartilharTarefaComponent],
  templateUrl: './calendario-tarefas.component.html',
  styleUrls: ['./calendario-tarefas.component.scss']
})
export class CalendarioTarefasComponent implements OnInit, OnChanges {
  @Input() tarefas: Tarefa[] = [];
  @Input() dataSelecionada: Date = new Date();
  @Output() diaSelecionado = new EventEmitter<Date>();
  @Output() tarefaCriada = new EventEmitter<void>();

  diasCalendario: DiaCalendario[] = [];
  mesAtual: Date = new Date();
  mostrarModalCriacao = false;
  mostrarModalTarefasDia = false;
  diaSelecionadoParaVisualizacao: Date | null = null;
  tarefasDoDia: Tarefa[] = [];
  mostrarModalCompartilhamento = false;
  tarefaParaCompartilhar: Tarefa | null = null;
  
  // Instâncias de tarefas recorrentes
  instanciasRecorrentes: TarefaRecorrenteInstancia[] = [];
  novaTarefa = {
    titulo: '',
    descricao: '',
    prioridade: PrioridadeTarefa.MEDIA,
    categoria: null as CategoriaTarefa | null,
    dataVencimento: '',
    isRecorrente: false,
    tipoRecorrencia: TipoRecorrencia.SEMANAL,
    intervaloRecorrencia: 1,
    diasDaSemana: [] as DiaSemana[]
  };
  
  categorias: CategoriaTarefa[] = [];
  
  tiposRecorrencia = [
    { value: TipoRecorrencia.DIARIA, label: 'Diária' },
    { value: TipoRecorrencia.SEMANAL, label: 'Semanal' },
    { value: TipoRecorrencia.MENSAL, label: 'Mensal' },
    { value: TipoRecorrencia.ANUAL, label: 'Anual' }
  ];
  
  diasSemana = [
    { value: DiaSemana.DOMINGO, label: 'Dom' },
    { value: DiaSemana.SEGUNDA, label: 'Seg' },
    { value: DiaSemana.TERCA, label: 'Ter' },
    { value: DiaSemana.QUARTA, label: 'Qua' },
    { value: DiaSemana.QUINTA, label: 'Qui' },
    { value: DiaSemana.SEXTA, label: 'Sex' },
    { value: DiaSemana.SABADO, label: 'Sáb' }
  ];
  
  nomesMeses = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ];
  nomesDias = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'  ];

  constructor(
    private tarefaService: TarefaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarInstanciasRecorrentes();
    this.gerarCalendario();
    this.carregarCategorias();
    // Remover carregarTarefasDoMes() - as tarefas já vêm do componente pai
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tarefas'] || changes['dataSelecionada']) {
      console.log('🔄 Calendário: tarefas recebidas:', this.tarefas.length);
      console.log('📋 Tarefas:', this.tarefas);
      
      // Verificar se há tarefas recorrentes
      const tarefasRecorrentes = this.tarefas.filter(t => t.isRecorrente);
      console.log('🔄 Tarefas recorrentes encontradas:', tarefasRecorrentes.length);
      tarefasRecorrentes.forEach(t => {
        console.log(`- ${t.titulo}: isRecorrente=${t.isRecorrente}, diasDaSemana=${JSON.stringify(t.diasDaSemana)}`);
      });
      
      // Regenerar calendário quando tarefas mudam
      this.gerarCalendario();
    }
  }

  // Método para forçar regeneração do calendário
  regenerarCalendario(): void {
    this.gerarCalendario();
  }

  // Verificar se uma tarefa está vencida
  tarefaVencida(tarefa: Tarefa, data: Date): boolean {
    if (!tarefa.dataVencimento) return false;
    
    const dataVencimento = new Date(tarefa.dataVencimento);
    dataVencimento.setHours(23, 59, 59, 999);
    
    return data > dataVencimento && tarefa.status !== StatusTarefa.CONCLUIDA;
  }

  // Remover tarefa vencida
  removerTarefaVencida(tarefa: Tarefa, event: Event): void {
    event.stopPropagation();
    
    if (confirm(`Deseja remover a tarefa "${tarefa.titulo}" do calendário?`)) {
      // Marcar como cancelada para não aparecer mais
      this.tarefaService.atualizarTarefa(tarefa.id, { status: StatusTarefa.CANCELADA }).subscribe({
        next: () => {
          this.tarefaCriada.emit(); // Recarregar tarefas
        },
        error: (error) => {
          console.error('Erro ao remover tarefa:', error);
        }
      });
    }
  }

  // Obter tooltip com informações da tarefa
  obterTooltipTarefa(tarefa: Tarefa): string {
    let tooltip = `Tarefa: ${tarefa.titulo}\nStatus: ${tarefa.status}`;
    
    if (tarefa.dataInicio) {
      const dataInicio = new Date(tarefa.dataInicio);
      tooltip += `\nInício: ${dataInicio.toLocaleDateString('pt-BR')}`;
    }
    
    if (tarefa.dataVencimento) {
      const dataVencimento = new Date(tarefa.dataVencimento);
      tooltip += `\nVencimento: ${dataVencimento.toLocaleDateString('pt-BR')}`;
    }
    
    if (tarefa.isRecorrente) {
      tooltip += `\nRecorrente: ${tarefa.tipoRecorrencia}`;
    }
    
    return tooltip;
  }

  gerarCalendario(): void {
    this.diasCalendario = [];
    
    console.log('📅 Gerando calendário...');
    console.log('📋 Total de tarefas:', this.tarefas.length);
    
    const primeiroDiaMes = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth(), 1);
    const ultimoDiaMes = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() + 1, 0);
    const primeiroDiaSemana = primeiroDiaMes.getDay();
    const ultimoDiaSemana = ultimoDiaMes.getDay();
    
    // Dias do mês anterior
    const mesAnterior = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() - 1, 0);
    for (let i = primeiroDiaSemana - 1; i >= 0; i--) {
      const data = new Date(primeiroDiaMes);
      data.setDate(data.getDate() - (i + 1));
      this.diasCalendario.push(this.criarDiaCalendario(data, false));
    }
    
    // Dias do mês atual
    for (let dia = 1; dia <= ultimoDiaMes.getDate(); dia++) {
      const data = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth(), dia);
      this.diasCalendario.push(this.criarDiaCalendario(data, true));
    }
    
    // Dias do próximo mês
    for (let dia = 1; dia <= (6 - ultimoDiaSemana); dia++) {
      const data = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() + 1, dia);
      this.diasCalendario.push(this.criarDiaCalendario(data, false));
    }
  }

  criarDiaCalendario(data: Date, isCurrentMonth: boolean): DiaCalendario {
    const hoje = new Date();
    const isToday = this.isMesmoDia(data, hoje);
    const isSelected = this.isMesmoDia(data, this.dataSelecionada);
    
    const tarefasDoDia = this.tarefas.filter(tarefa => {
      // Verificar se a tarefa deve aparecer neste dia
      return this.tarefaDeveAparecerNoDia(tarefa, data);
    });

    // CORREÇÃO: Não adicionar tarefas recorrentes aqui pois já estão incluídas no filtro acima
    // As tarefas recorrentes já são processadas por tarefaDeveAparecerNoDia()

    return {
      data: new Date(data),
      numero: data.getDate(),
      isCurrentMonth,
      isToday,
      isSelected,
      tarefas: tarefasDoDia
    };
  }

  isMesmoDia(data1: Date, data2: Date): boolean {
    return data1.getFullYear() === data2.getFullYear() &&
           data1.getMonth() === data2.getMonth() &&
           data1.getDate() === data2.getDate();
  }

  tarefaDeveAparecerNoDia(tarefa: Tarefa, data: Date): boolean {
    // Para tarefas recorrentes, verificar se existe instância para esta data
    if (tarefa.isRecorrente) {
      const dataStr = data.toISOString().split('T')[0];
      const instancia = this.instanciasRecorrentes.find(i => 
        i.tarefaRecorrenteId === tarefa.id && i.dataInstancia === dataStr
      );
      
      // Se existe instância, mostrar (independente do status)
      return instancia !== undefined;
    }

    // Para tarefas normais, verificar status global
    if (tarefa.status === StatusTarefa.CONCLUIDA) {
      return false;
    }

    // Se a tarefa foi cancelada, não aparece
    if (tarefa.status === StatusTarefa.CANCELADA) {
      return false;
    }

    // Para tarefas não recorrentes
    const dataComparacao = new Date(data);
    dataComparacao.setHours(0, 0, 0, 0);

    // Se tem data de início, verificar se já passou
    if (tarefa.dataInicio) {
      const dataInicio = new Date(tarefa.dataInicio);
      dataInicio.setHours(0, 0, 0, 0);
      if (dataComparacao < dataInicio) {
        return false; // Ainda não começou
      }
    }

    // Se tem data de vencimento, verificar se já passou
    if (tarefa.dataVencimento) {
      const dataVencimento = new Date(tarefa.dataVencimento);
      dataVencimento.setHours(23, 59, 59, 999);
      if (dataComparacao > dataVencimento) {
        return false; // Já venceu
      }
    }

    // Se tem data de vencimento, aparecer desde hoje até o dia de vencimento
    if (tarefa.dataVencimento) {
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);
      return dataComparacao >= hoje; // Aparece desde hoje até o vencimento
    }

    // Se não tem data de vencimento, aparecer em todos os dias (tarefas pendentes)
    return true;
  }

  tarefaRecorrenteDeveAparecerNoDia(tarefa: Tarefa, data: Date): boolean {
    if (!tarefa.isRecorrente) return false;

    console.log(`🔍 Verificando tarefa recorrente: ${tarefa.titulo}`);
    console.log(`📅 Data: ${data.toDateString()}`);
    console.log(`📋 Dados:`, {
      isRecorrente: tarefa.isRecorrente,
      tipoRecorrencia: tarefa.tipoRecorrencia,
      diasDaSemana: tarefa.diasDaSemana,
      dataInicio: tarefa.dataInicio,
      dataVencimento: tarefa.dataVencimento,
      dataCriacao: tarefa.dataCriacao
    });

    const dataComparacao = new Date(data);
    dataComparacao.setHours(0, 0, 0, 0);

    // Verificar data de início
    if (tarefa.dataInicio) {
      const dataInicio = new Date(tarefa.dataInicio);
      dataInicio.setHours(0, 0, 0, 0);
      if (dataComparacao < dataInicio) {
        console.log(`❌ Não começou ainda`);
        return false; // Ainda não começou
      }
    }

    // Verificar data de fim da recorrência (dataVencimento para tarefas recorrentes)
    if (tarefa.dataVencimento) {
      const dataFim = new Date(tarefa.dataVencimento);
      dataFim.setHours(23, 59, 59, 999);
      if (dataComparacao > dataFim) {
        console.log(`❌ Recorrência terminou`);
        return false; // Já terminou a recorrência
      }
    }

    // Se não tem data de início, usar data de criação
    const dataInicio = tarefa.dataInicio ? new Date(tarefa.dataInicio) : new Date(tarefa.dataCriacao);
    dataInicio.setHours(0, 0, 0, 0);

    // Verificar se a data está dentro do período de recorrência
    if (dataComparacao < dataInicio) {
      console.log(`❌ Antes da data de início`);
      return false;
    }

    // Se tem dias da semana configurados, verificar se o dia atual está na lista
    if (tarefa.diasDaSemana && tarefa.diasDaSemana.length > 0) {
      const diaSemana = data.getDay(); // 0 = Domingo, 1 = Segunda, etc.
      const diasSemanaMap: { [key: number]: DiaSemana } = {
        0: DiaSemana.DOMINGO,
        1: DiaSemana.SEGUNDA,
        2: DiaSemana.TERCA,
        3: DiaSemana.QUARTA,
        4: DiaSemana.QUINTA,
        5: DiaSemana.SEXTA,
        6: DiaSemana.SABADO
      };
      
      const diaSemanaAtual = diasSemanaMap[diaSemana];
      const deveAparecer = tarefa.diasDaSemana.includes(diaSemanaAtual);
      
      console.log(`📅 Dia da semana atual: ${diaSemanaAtual} (${diaSemana})`);
      console.log(`📋 Dias configurados: [${tarefa.diasDaSemana.join(', ')}]`);
      console.log(`✅ Deve aparecer: ${deveAparecer ? 'SIM' : 'NÃO'}`);
      
      return deveAparecer;
    }

    // Se não tem dias específicos configurados, NÃO aparecer (tarefa recorrente deve ter dias configurados)
    console.log(`❌ Sem dias específicos configurados`);
    return false;
  }

  gerarTarefasRecorrentesParaDia(data: Date): Tarefa[] {
    const tarefasRecorrentes: Tarefa[] = [];
    
    // Filtrar apenas tarefas recorrentes
    const tarefasRecorrentesOriginais = this.tarefas.filter(tarefa => tarefa.isRecorrente);
    
    for (const tarefaRecorrente of tarefasRecorrentesOriginais) {
      if (this.deveMostrarTarefaRecorrente(tarefaRecorrente, data)) {
        // Criar uma cópia da tarefa para este dia específico
        const tarefaInstancia = this.criarInstanciaTarefaRecorrente(tarefaRecorrente, data);
        tarefasRecorrentes.push(tarefaInstancia);
      }
    }
    
    return tarefasRecorrentes;
  }

  deveMostrarTarefaRecorrente(tarefa: Tarefa, data: Date): boolean {
    if (!tarefa.isRecorrente) {
      return false;
    }

    // Se não tem data de vencimento, usar a data atual como início
    const dataInicio = tarefa.dataVencimento ? new Date(tarefa.dataVencimento) : new Date();
    
    // Se a data for anterior ao início da recorrência, não mostrar
    if (data < dataInicio) {
      return false;
    }

    // Verificar se há data de fim e se já passou
    if (tarefa.dataFimRecorrencia) {
      const dataFim = new Date(tarefa.dataFimRecorrencia);
      if (data > dataFim) {
        return false;
      }
    }

    // Calcular se esta data deve ter a tarefa baseado no tipo de recorrência
    switch (tarefa.tipoRecorrencia) {
      case TipoRecorrencia.DIARIA:
        return this.verificarRecorrenciaDiaria(tarefa, dataInicio, data);
      
      case TipoRecorrencia.SEMANAL:
        return this.verificarRecorrenciaSemanal(tarefa, dataInicio, data);
      
      case TipoRecorrencia.MENSAL:
        return this.verificarRecorrenciaMensal(tarefa, dataInicio, data);
      
      case TipoRecorrencia.ANUAL:
        return this.verificarRecorrenciaAnual(tarefa, dataInicio, data);
      
      default:
        return false;
    }
  }

  verificarRecorrenciaDiaria(tarefa: Tarefa, dataInicio: Date, data: Date): boolean {
    // Se a data for anterior ao início da recorrência, não mostrar
    if (data < dataInicio) {
      return false;
    }

    const diasDiferenca = Math.floor((data.getTime() - dataInicio.getTime()) / (1000 * 60 * 60 * 24));
    return diasDiferenca % (tarefa.intervaloRecorrencia || 1) === 0;
  }

  verificarRecorrenciaSemanal(tarefa: Tarefa, dataInicio: Date, data: Date): boolean {
    // Se a data for anterior ao início da recorrência, não mostrar
    if (data < dataInicio) {
      return false;
    }

    const diasDiferenca = Math.floor((data.getTime() - dataInicio.getTime()) / (1000 * 60 * 60 * 24));
    const semanasDiferenca = Math.floor(diasDiferenca / 7);
    
    // Verificar se está no intervalo correto de semanas
    if (semanasDiferenca % (tarefa.intervaloRecorrencia || 1) !== 0) {
      return false;
    }

    // Verificar se o dia da semana está nos dias selecionados
    const diaSemana = data.getDay(); // 0 = Domingo, 1 = Segunda, etc.
    const diasSemanaMap: { [key: number]: DiaSemana } = {
      0: DiaSemana.DOMINGO,
      1: DiaSemana.SEGUNDA,
      2: DiaSemana.TERCA,
      3: DiaSemana.QUARTA,
      4: DiaSemana.QUINTA,
      5: DiaSemana.SEXTA,
      6: DiaSemana.SABADO
    };

    return tarefa.diasDaSemana?.includes(diasSemanaMap[diaSemana]) || false;
  }

  verificarRecorrenciaMensal(tarefa: Tarefa, dataInicio: Date, data: Date): boolean {
    // Se a data for anterior ao início da recorrência, não mostrar
    if (data < dataInicio) {
      return false;
    }

    const mesesDiferenca = (data.getFullYear() - dataInicio.getFullYear()) * 12 + 
                          (data.getMonth() - dataInicio.getMonth());
    
    if (mesesDiferenca % (tarefa.intervaloRecorrencia || 1) !== 0) {
      return false;
    }

    // Verificar se é o mesmo dia do mês
    return data.getDate() === dataInicio.getDate();
  }

  verificarRecorrenciaAnual(tarefa: Tarefa, dataInicio: Date, data: Date): boolean {
    // Se a data for anterior ao início da recorrência, não mostrar
    if (data < dataInicio) {
      return false;
    }

    const anosDiferenca = data.getFullYear() - dataInicio.getFullYear();
    
    if (anosDiferenca % (tarefa.intervaloRecorrencia || 1) !== 0) {
      return false;
    }

    // Verificar se é o mesmo dia e mês
    return data.getMonth() === dataInicio.getMonth() && 
           data.getDate() === dataInicio.getDate();
  }

  criarInstanciaTarefaRecorrente(tarefaOriginal: Tarefa, data: Date): Tarefa {
    // Criar uma cópia da tarefa original para este dia específico
    const instancia = { ...tarefaOriginal };
    
    // Atualizar a data de vencimento para o dia específico
    instancia.dataVencimento = data.toISOString();
    
    // Adicionar um sufixo ao título para indicar que é uma instância
    instancia.titulo = `${tarefaOriginal.titulo} (${data.toLocaleDateString('pt-BR')})`;
    
    // Marcar como instância de tarefa recorrente
    instancia.id = tarefaOriginal.id * 10000 + data.getTime(); // ID único para a instância
    
    return instancia;
  }

  selecionarDia(dia: DiaCalendario): void {
    this.dataSelecionada = dia.data;
    this.diaSelecionado.emit(dia.data);
    this.gerarCalendario(); // Regenerar para atualizar seleção
  }

  criarTarefaNoDia(dia: DiaCalendario, event: Event): void {
    event.stopPropagation();
    this.dataSelecionada = dia.data;
    this.mostrarModalCriacao = true;
    this.novaTarefa = {
      titulo: '',
      descricao: '',
      prioridade: PrioridadeTarefa.MEDIA,
      categoria: null,
      dataVencimento: '', // Não pré-preenchir
      isRecorrente: false,
      tipoRecorrencia: TipoRecorrencia.SEMANAL,
      intervaloRecorrencia: 1,
      diasDaSemana: []
    };
  }

  fecharModalCriacao(): void {
    this.mostrarModalCriacao = false;
    this.novaTarefa = {
      titulo: '',
      descricao: '',
      prioridade: PrioridadeTarefa.MEDIA,
      categoria: null,
      dataVencimento: '',
      isRecorrente: false,
      tipoRecorrencia: TipoRecorrencia.SEMANAL,
      intervaloRecorrencia: 1,
      diasDaSemana: []
    };
  }

  criarTarefaRapida(): void {
    console.log('🚀 criarTarefaRapida chamado');
    
    if (this.novaTarefa.titulo.trim()) {
      console.log('📝 Criando tarefa:', this.novaTarefa.titulo);
      
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
        tarefaData.dataVencimento = this.novaTarefa.dataVencimento + 'T00:00:00';
      }

      console.log('📋 Dados da tarefa:', tarefaData);

      if (this.novaTarefa.isRecorrente) {
        console.log('🔄 Criando tarefa RECORRENTE');
        this.criarTarefaRecorrente(tarefaData);
      } else {
        console.log('📝 Criando tarefa SIMPLES');
        this.criarTarefaSimples(tarefaData);
      }
    } else {
      console.log('❌ Título vazio, não criando tarefa');
    }
  }

  private criarTarefaSimples(tarefaData: any): void {
    this.tarefaService.criarTarefa(tarefaData).subscribe({
      next: () => {
        Swal.fire({
          title: 'Criada!',
          text: 'Tarefa criada com sucesso.',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        });
        this.tarefaCriada.emit();
        this.fecharModalCriacao();
      },
      error: (error) => {
        Swal.fire({
          title: 'Erro!',
          text: error.error?.message || 'Erro ao criar tarefa.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  private criarTarefaRecorrente(tarefaData: any): void {
    console.log('🚀 Enviando para backend:', tarefaData);
    
    this.tarefaService.criarTarefaRecorrente(tarefaData).subscribe({
      next: (tarefas) => {
        console.log('✅ Resposta do backend:', tarefas);
        Swal.fire({
          title: 'Criada!',
          text: 'Tarefa recorrente criada com sucesso.',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        });
        this.tarefaCriada.emit();
        this.fecharModalCriacao();
      },
      error: (error) => {
        console.error('❌ Erro do backend:', error);
        Swal.fire({
          title: 'Erro!',
          text: error.error?.message || 'Erro ao criar tarefa recorrente.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  alternarDiaSemana(dia: DiaSemana): void {
    const index = this.novaTarefa.diasDaSemana.indexOf(dia);
    if (index > -1) {
      this.novaTarefa.diasDaSemana.splice(index, 1);
    } else {
      this.novaTarefa.diasDaSemana.push(dia);
    }
  }

  private carregarInstanciasRecorrentes(): void {
    const dataInicio = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth(), 1);
    const dataFim = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() + 1, 0);
    
    const dataInicioStr = dataInicio.toISOString().split('T')[0];
    const dataFimStr = dataFim.toISOString().split('T')[0];
    
    this.tarefaService.buscarInstanciasRecorrentes(dataInicioStr, dataFimStr).subscribe({
      next: (instancias) => {
        this.instanciasRecorrentes = instancias;
        this.gerarCalendario();
      },
      error: (error) => {
        console.error('Erro ao carregar instâncias recorrentes:', error);
      }
    });
  }

  marcarComoConcluidaNoDia(tarefa: Tarefa, data: Date, event: Event): void {
    event.stopPropagation(); // Evitar abrir o modal de detalhes
    
    // Para tarefas recorrentes, marcar esta instância específica
    if (tarefa.isRecorrente) {
      const dataStr = data.toISOString().split('T')[0];
      
      this.tarefaService.marcarInstanciaComoConcluida(tarefa.id, dataStr).subscribe({
        next: (instancia) => {
          // Atualizar a instância na lista local
          const index = this.instanciasRecorrentes.findIndex(i => i.id === instancia.id);
          if (index >= 0) {
            this.instanciasRecorrentes[index] = instancia;
          }
          
          Swal.fire({
            title: 'Concluída!',
            text: 'Tarefa recorrente marcada como concluída para este dia.',
            icon: 'success',
            timer: 1500,
            showConfirmButton: false
          });
          
          // Atualizar visualização
          this.gerarCalendario();
        },
        error: (error) => {
          Swal.fire({
            title: 'Erro!',
            text: 'Erro ao marcar tarefa como concluída.',
            icon: 'error',
            confirmButtonText: 'OK'
          });
        }
      });
    } else {
      // Para tarefas normais, usar o método existente
      this.marcarComoConcluida(tarefa);
    }
  }

  tarefaConcluidaHoje(tarefa: Tarefa, data: Date): boolean {
    if (tarefa.isRecorrente) {
      const dataStr = data.toISOString().split('T')[0];
      const instancia = this.instanciasRecorrentes.find(i => 
        i.tarefaRecorrenteId === tarefa.id && i.dataInstancia === dataStr
      );
      return instancia ? instancia.status === 'CONCLUIDA' : false;
    }
    return tarefa.status === StatusTarefa.CONCLUIDA;
  }

  isDiaSelecionado(dia: DiaSemana): boolean {
    return this.novaTarefa.diasDaSemana.includes(dia);
  }

  onTipoRecorrenciaChange(): void {
    // Limpar dias da semana quando não for semanal
    if (this.novaTarefa.tipoRecorrencia !== TipoRecorrencia.SEMANAL) {
      this.novaTarefa.diasDaSemana = [];
    }
  }

  carregarCategorias(): void {
    this.tarefaService.obterCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      },
      error: () => {
        // Em caso de erro, manter lista vazia
        this.categorias = [];
      }
    });
  }


  mesAnterior(): void {
    this.mesAtual = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() - 1, 1);
    this.carregarInstanciasRecorrentes();
  }

  proximoMes(): void {
    this.mesAtual = new Date(this.mesAtual.getFullYear(), this.mesAtual.getMonth() + 1, 1);
    this.carregarInstanciasRecorrentes();
  }

  irParaHoje(): void {
    this.mesAtual = new Date();
    this.dataSelecionada = new Date();
    this.carregarInstanciasRecorrentes();
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

  obterNomeMes(): string {
    return this.nomesMeses[this.mesAtual.getMonth()];
  }

  obterAno(): number {
    return this.mesAtual.getFullYear();
  }

  obterClassesDia(dia: DiaCalendario): string {
    let classes = 'calendario-dia group';
    
    if (!dia.isCurrentMonth) {
      classes += ' dia-outro-mes';
    }
    
    if (dia.isToday) {
      classes += ' dia-atual';
    }
    
    if (dia.isSelected) {
      classes += ' dia-selecionado';
    }
    
    return classes;
  }

  obterClassesNumeroDia(dia: DiaCalendario): string {
    let classes = '';
    
    if (!dia.isCurrentMonth) {
      classes = 'text-gray-400';
    } else if (dia.isToday) {
      classes = 'text-blue-600 font-bold';
    } else if (dia.isSelected) {
      classes = 'text-blue-800 font-semibold';
    } else {
      classes = 'text-gray-900';
    }
    
    return classes;
  }

  trackByDia(index: number, dia: DiaCalendario): number {
    return dia.data.getTime();
  }

  trackByTarefa(index: number, tarefa: Tarefa): number {
    return tarefa.id;
  }

  // Métodos para visualizar tarefas do dia
  visualizarTarefasDoDia(dia: DiaCalendario, event: Event): void {
    event.stopPropagation();
    
    if (dia.tarefas.length === 0) {
      return;
    }
    
    this.diaSelecionadoParaVisualizacao = dia.data;
    this.tarefasDoDia = dia.tarefas;
    this.mostrarModalTarefasDia = true;
  }

  fecharModalTarefasDia(): void {
    this.mostrarModalTarefasDia = false;
    this.diaSelecionadoParaVisualizacao = null;
    this.tarefasDoDia = [];
  }


  marcarComoConcluida(tarefa: Tarefa): void {
    // Para tarefas normais, marcar como concluída no backend
    this.tarefaService.marcarComoConcluida(tarefa.id).subscribe({
      next: () => {
        tarefa.status = StatusTarefa.CONCLUIDA;
        this.tarefaCriada.emit(); // Recarrega as tarefas
        Swal.fire({
          title: 'Concluída!',
          text: 'Tarefa marcada como concluída.',
          icon: 'success',
          timer: 1500,
          showConfirmButton: false
        });
      },
      error: (error) => {
        Swal.fire({
          title: 'Erro!',
          text: error.error?.message || 'Erro ao marcar tarefa como concluída.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  excluirTarefa(tarefa: Tarefa): void {
    Swal.fire({
      title: 'Excluir Tarefa',
      text: `Tem certeza que deseja excluir a tarefa "${tarefa.titulo}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sim, excluir',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#ef4444'
    }).then((result) => {
      if (result.isConfirmed) {
        this.tarefaService.excluirTarefa(tarefa.id).subscribe({
          next: () => {
            this.tarefasDoDia = this.tarefasDoDia.filter(t => t.id !== tarefa.id);
            this.tarefaCriada.emit(); // Recarrega as tarefas
            Swal.fire({
              title: 'Excluída!',
              text: 'Tarefa excluída com sucesso.',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false
            });
          },
          error: (error) => {
            Swal.fire({
              title: 'Erro!',
              text: error.error?.message || 'Erro ao excluir tarefa.',
              icon: 'error',
              confirmButtonText: 'OK'
            });
          }
        });
      }
    });
  }

  editarTarefa(tarefa: Tarefa): void {
    // Emitir evento para o componente pai navegar para edição
    this.router.navigate(['/tarefas/editar', tarefa.id]);
  }

  // Métodos para compartilhamento
  compartilharTarefa(tarefa: Tarefa): void {
    this.tarefaParaCompartilhar = tarefa;
    this.mostrarModalCompartilhamento = true;
  }

  fecharModalCompartilhamento(): void {
    this.mostrarModalCompartilhamento = false;
    this.tarefaParaCompartilhar = null;
  }

  onCompartilhamentoAdicionado(compartilhamento: CompartilhamentoTarefa): void {
    if (this.tarefaParaCompartilhar) {
      this.tarefaParaCompartilhar.compartilhamentos = [...(this.tarefaParaCompartilhar.compartilhamentos || []), compartilhamento];
    }
    this.fecharModalCompartilhamento();
  }

  onCompartilhamentoRemovido(compartilhamentoId: number): void {
    if (this.tarefaParaCompartilhar) {
      this.tarefaParaCompartilhar.compartilhamentos = (this.tarefaParaCompartilhar.compartilhamentos || []).filter(
        c => c.id !== compartilhamentoId
      );
    }
  }
}
