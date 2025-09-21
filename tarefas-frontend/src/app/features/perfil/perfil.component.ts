import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Usuario } from '../../core/models/usuario.model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss']
})
export class PerfilComponent implements OnInit {
  perfilForm: FormGroup;
  usuario: Usuario | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.perfilForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2)]],
      sobrenome: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.usuario = this.authService.getCurrentUser();
    if (this.usuario) {
      this.perfilForm.patchValue({
        nome: this.usuario.nome,
        sobrenome: this.usuario.sobrenome,
        email: this.usuario.email
      });
    }
  }

  onSubmit(): void {
    if (this.perfilForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.authService.atualizarPerfil(this.perfilForm.value).subscribe({
        next: () => {
          this.successMessage = 'Perfil atualizado com sucesso!';
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Erro ao atualizar perfil. Tente novamente.';
          this.isLoading = false;
        }
      });
    }
  }

  voltar(): void {
    this.router.navigate(['/dashboard']);
  }
}
