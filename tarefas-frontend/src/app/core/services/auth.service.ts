import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { Usuario, LoginRequest, CadastroRequest, AuthResponse } from '../models/usuario.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/api/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'current_user';

  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  login(credentials: LoginRequest): Observable<string> {
    return this.http.post<{token: string}>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => {
          this.setToken(response.token);
          this.loadUserProfile();
        }),
        map(response => response.token)
      );
  }

  cadastro(userData: CadastroRequest): Observable<string> {
    return this.http.post<{token: string}>(`${this.API_URL}/registro`, userData)
      .pipe(
        tap(response => {
          this.setToken(response.token);
          this.loadUserProfile();
        }),
        map(response => response.token)
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  private loadStoredUser(): void {
    const storedUser = localStorage.getItem(this.USER_KEY);
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  private loadUserProfile(): void {
    this.http.get<Usuario>(`${this.API_URL}/perfil`).subscribe({
      next: (user) => {
        this.currentUserSubject.next(user);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      },
      error: () => {
        this.logout();
      }
    });
  }

  buscarUsuarioPorUuid(uuid: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.API_URL}/usuario/${uuid}`);
  }

  atualizarPerfil(userData: Partial<Usuario>): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.API_URL}/perfil`, userData)
      .pipe(
        tap(user => {
          this.currentUserSubject.next(user);
          localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        })
      );
  }
}
