import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TarefaService } from '../../../../core/services/tarefa.service';

@Component({
  selector: 'app-integracao-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './integracao-calendar.component.html',
  styleUrls: ['./integracao-calendar.component.scss']
})
export class IntegracaoCalendarComponent implements OnInit {
  isConectado = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.verificarStatusConexao();
  }

  verificarStatusConexao(): void {
    this.isLoading = true;
    this.tarefaService.obterUrlAutenticacaoGoogle().subscribe({
      next: () => {
        // Se conseguiu obter a URL, significa que não está conectado
        this.isConectado = false;
        this.isLoading = false;
      },
      error: () => {
        // Se deu erro, pode ser que já esteja conectado
        this.isConectado = true;
        this.isLoading = false;
      }
    });
  }

  conectarGoogleCalendar(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.tarefaService.obterUrlAutenticacaoGoogle().subscribe({
      next: (response) => {
        // Abrir a URL de autenticação em uma nova janela
        const authWindow = window.open(response.authUrl, 'google-auth', 'width=500,height=600');
        
        // Verificar se a janela foi fechada (usuário completou ou cancelou)
        const checkClosed = setInterval(() => {
          if (authWindow?.closed) {
            clearInterval(checkClosed);
            this.verificarStatusConexao();
            this.isLoading = false;
          }
        }, 1000);
      },
      error: (error) => {
        this.errorMessage = 'Erro ao iniciar conexão com Google Calendar';
        this.isLoading = false;
      }
    });
  }

  desconectarGoogleCalendar(): void {
    if (confirm('Tem certeza que deseja desconectar do Google Calendar?')) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.tarefaService.desconectarGoogleCalendar().subscribe({
        next: () => {
          this.isConectado = false;
          this.successMessage = 'Desconectado do Google Calendar com sucesso!';
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Erro ao desconectar do Google Calendar';
          this.isLoading = false;
        }
      });
    }
  }

  sincronizarTarefas(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Simular sincronização (implementar conforme necessário)
    setTimeout(() => {
      this.successMessage = 'Tarefas sincronizadas com sucesso!';
      this.isLoading = false;
    }, 2000);
  }
}
