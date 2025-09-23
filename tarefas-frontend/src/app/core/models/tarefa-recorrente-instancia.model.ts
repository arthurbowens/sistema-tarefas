export interface TarefaRecorrenteInstancia {
  id: number;
  tarefaRecorrenteId: number;
  dataInstancia: string; // YYYY-MM-DD
  status: 'PENDENTE' | 'CONCLUIDA' | 'CANCELADA';
  dataConclusao?: string;
  dataCriacao: string;
  
  // Dados da tarefa recorrente
  titulo: string;
  descricao: string;
  prioridade: string;
  categoria?: string;
  cor: string;
  tags?: string;
}
