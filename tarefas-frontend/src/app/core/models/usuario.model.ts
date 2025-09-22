export interface Usuario {
  id: number;
  uuid: string;
  email: string;
  nome: string;
  sobrenome: string;
  dataCriacao: string;
  ultimoAcesso?: string;
  ativo: boolean;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface CadastroRequest {
  email: string;
  senha: string;
  nome: string;
  sobrenome: string;
}

export interface AuthResponse {
  token: string;
}
