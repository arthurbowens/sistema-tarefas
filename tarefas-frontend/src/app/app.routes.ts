import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { CadastroComponent } from './features/auth/cadastro/cadastro.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { AuthGuard } from './core/guards/auth.guard';
import { DashboardEstatisticasComponent } from './features/estatisticas/components/dashboard/dashboard.component';
import { IntegracaoCalendarComponent } from './features/google-calendar/components/integracao-calendar/integracao-calendar.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/cadastro', component: CadastroComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'tarefas', 
    loadChildren: () => import('./features/tarefas/tarefas.routes').then(m => m.tarefasRoutes),
    canActivate: [AuthGuard]
  },
  { 
    path: 'perfil', 
    loadChildren: () => import('./features/perfil/perfil.routes').then(m => m.perfilRoutes),
    canActivate: [AuthGuard]
  },
  { 
    path: 'estatisticas', 
    component: DashboardEstatisticasComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'google-calendar', 
    component: IntegracaoCalendarComponent,
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: '/dashboard' }
];