import { Routes } from '@angular/router';

export const tarefasRoutes: Routes = [
  { path: '', redirectTo: 'lista', pathMatch: 'full' },
  { path: 'lista', loadComponent: () => import('./lista/lista.component').then(m => m.ListaComponent) },
  { path: 'criar', loadComponent: () => import('./criar/criar.component').then(m => m.CriarComponent) },
  { path: 'editar/:id', loadComponent: () => import('./editar/editar.component').then(m => m.EditarComponent) },
  { path: 'detalhes/:id', loadComponent: () => import('./detalhes/detalhes.component').then(m => m.DetalhesComponent) }
];
