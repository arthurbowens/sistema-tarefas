import { Routes } from '@angular/router';

export const perfilRoutes: Routes = [
  { path: '', loadComponent: () => import('./perfil.component').then(m => m.PerfilComponent) }
];
