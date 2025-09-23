export enum StatusTarefa {
  PENDENTE = 'PENDENTE',
  EM_ANDAMENTO = 'EM_ANDAMENTO',
  CONCLUIDA = 'CONCLUIDA',
  CANCELADA = 'CANCELADA'
}

export enum PrioridadeTarefa {
  BAIXA = 'BAIXA',
  MEDIA = 'MEDIA',
  ALTA = 'ALTA',
  URGENTE = 'URGENTE'
}

export enum CategoriaTarefa {
  TRABALHO = 'TRABALHO',
  PESSOAL = 'PESSOAL',
  ESTUDO = 'ESTUDO',
  SAUDE = 'SAUDE',
  FINANCAS = 'FINANCAS',
  CASA = 'CASA',
  VIAGEM = 'VIAGEM',
  HOBBIES = 'HOBBIES',
  FAMILIA = 'FAMILIA',
  OUTROS = 'OUTROS'
}

export interface Tarefa {
  id: number;
  titulo: string;
  descricao: string;
  status: StatusTarefa;
  prioridade: PrioridadeTarefa;
  categoria?: CategoriaTarefa;
  dataCriacao: string;
  dataInicio?: string; // Nova: quando a tarefa deve começar a aparecer
  dataVencimento?: string;
  dataConclusao?: string;
  dataAtualizacao: string;
  cor: string;
  tags?: string;
  tarefaPaiId?: number;
  checklist: ChecklistItem[];
  compartilhamentos: CompartilhamentoTarefa[];
  subtarefas: Tarefa[];
  usuario: Usuario;
  // Campos de recorrência
  isRecorrente?: boolean;
  tipoRecorrencia?: TipoRecorrencia;
  intervaloRecorrencia?: number;
  dataFimRecorrencia?: string;
  diasDaSemana?: DiaSemana[];
}

export enum TipoRecorrencia {
  DIARIA = 'DIARIA',
  SEMANAL = 'SEMANAL',
  MENSAL = 'MENSAL',
  ANUAL = 'ANUAL'
}

export enum DiaSemana {
  DOMINGO = 'DOMINGO',
  SEGUNDA = 'SEGUNDA',
  TERCA = 'TERCA',
  QUARTA = 'QUARTA',
  QUINTA = 'QUINTA',
  SEXTA = 'SEXTA',
  SABADO = 'SABADO'
}

export interface ChecklistItem {
  id: number;
  titulo: string;
  descricao?: string;
  concluido: boolean;
  dataCriacao: string;
  dataConclusao?: string;
  tarefa: Tarefa;
}

export interface CompartilhamentoTarefa {
  id: number;
  tarefa: Tarefa;
  usuarioCompartilhado: Usuario;
  tipoCompartilhamento: TipoCompartilhamento;
  dataCompartilhamento: string;
  conviteAceito: boolean;
}

export enum TipoCompartilhamento {
  LEITURA = 'LEITURA',
  ESCRITA = 'ESCRITA',
  ADMIN = 'ADMIN'
}

export interface Estatisticas {
  totalTarefas: number;
  tarefasConcluidas: number;
  tarefasPendentes: number;
  tarefasAtrasadas: number;
  tarefasPorPrioridade: Record<PrioridadeTarefa, number>;
  tarefasPorCategoria: Record<string, number>;
  producaoDiaria: Record<string, number>;
  tempoMedioConclusao?: number;
  tarefasRecorrentes: number;
  tarefasCompartilhadas: number;
}

import { Usuario } from './usuario.model';
