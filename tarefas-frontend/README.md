# Sistema de Gerenciamento de Tarefas - Frontend

Frontend Angular profissional para o sistema de gerenciamento de tarefas, desenvolvido com Angular 17, Tailwind CSS e TypeScript.

## 🚀 Funcionalidades

- **Autenticação JWT** - Login e cadastro seguros
- **Dashboard** - Visão geral das tarefas e estatísticas
- **CRUD de Tarefas** - Criar, listar, editar e excluir tarefas
- **Sistema de Prioridades** - Baixa, Média, Alta e Urgente
- **Filtros e Busca** - Filtrar por status, prioridade e buscar por termo
- **Interface Responsiva** - Design moderno com Tailwind CSS
- **Validação de Formulários** - Validação em tempo real
- **Interceptors HTTP** - Adição automática de tokens JWT

## 🛠️ Tecnologias

- **Angular 17** - Framework principal
- **TypeScript** - Linguagem de programação
- **Tailwind CSS** - Framework CSS utilitário
- **Reactive Forms** - Formulários reativos
- **RxJS** - Programação reativa
- **Angular Router** - Roteamento
- **Angular HTTP Client** - Comunicação com API

## 📁 Estrutura do Projeto

```
src/
├── app/
│   ├── core/                    # Serviços e funcionalidades core
│   │   ├── guards/              # Guards de autenticação
│   │   ├── interceptors/        # Interceptors HTTP
│   │   ├── models/              # Interfaces e tipos
│   │   └── services/            # Serviços principais
│   ├── features/                # Módulos de funcionalidades
│   │   ├── auth/                # Autenticação
│   │   │   ├── login/           # Componente de login
│   │   │   └── cadastro/        # Componente de cadastro
│   │   ├── dashboard/           # Dashboard principal
│   │   ├── tarefas/             # Gerenciamento de tarefas
│   │   │   ├── lista/           # Lista de tarefas
│   │   │   ├── criar/           # Criar tarefa
│   │   │   ├── editar/          # Editar tarefa
│   │   │   └── detalhes/        # Detalhes da tarefa
│   │   └── perfil/              # Perfil do usuário
│   ├── shared/                  # Componentes compartilhados
│   └── app.routes.ts            # Configuração de rotas
├── assets/                      # Recursos estáticos
└── styles.scss                  # Estilos globais
```

## 🚀 Como Executar

### Pré-requisitos

- Node.js 18+ 
- npm ou yarn
- Backend da API rodando em `http://localhost:8080`

### Instalação

1. **Clone o repositório:**
```bash
git clone <url-do-repositorio>
cd tarefas-frontend
```

2. **Instale as dependências:**
```bash
npm install
```

3. **Execute o projeto:**
```bash
npm start
```

4. **Acesse no navegador:**
```
http://localhost:4200
```

## 🔧 Configuração

### Variáveis de Ambiente

Crie um arquivo `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Configuração da API

O frontend está configurado para se comunicar com a API em `http://localhost:8080`. Para alterar, modifique o arquivo `src/app/core/services/auth.service.ts` e `src/app/core/services/tarefa.service.ts`.

## 📱 Funcionalidades Implementadas

### ✅ Autenticação
- [x] Login com email e senha
- [x] Cadastro de novos usuários
- [x] Logout automático
- [x] Guard de autenticação
- [x] Interceptor para tokens JWT

### ✅ Dashboard
- [x] Visão geral das tarefas
- [x] Estatísticas em tempo real
- [x] Ações rápidas
- [x] Tarefas recentes

### ✅ Gerenciamento de Tarefas
- [x] Listar todas as tarefas
- [x] Criar nova tarefa
- [x] Filtrar por status e prioridade
- [x] Buscar por termo
- [x] Marcar como concluída
- [x] Excluir tarefa

### ✅ Perfil do Usuário
- [x] Visualizar informações
- [x] Atualizar dados pessoais
- [x] Informações da conta

## 🎨 Design System

### Cores
- **Primary**: Azul (#3b82f6)
- **Secondary**: Cinza (#64748b)
- **Success**: Verde (#10b981)
- **Warning**: Amarelo (#f59e0b)
- **Error**: Vermelho (#ef4444)

### Componentes
- **Botões**: `btn-primary`, `btn-secondary`, `btn-danger`
- **Inputs**: `input-field`
- **Cards**: `card`, `card-header`

## 🔒 Segurança

- **JWT Tokens** - Autenticação stateless
- **Guards** - Proteção de rotas
- **Interceptors** - Adição automática de tokens
- **Validação** - Validação client-side e server-side

## 📊 Performance

- **Lazy Loading** - Carregamento sob demanda
- **OnPush Strategy** - Detecção de mudanças otimizada
- **Tree Shaking** - Bundle otimizado
- **AOT Compilation** - Compilação antecipada

## 🧪 Testes

```bash
# Testes unitários
npm run test

# Testes e2e
npm run e2e

# Coverage
npm run test:coverage
```

## 📦 Build

```bash
# Build de desenvolvimento
npm run build

# Build de produção
npm run build:prod
```

## 🚀 Deploy

### Vercel
```bash
npm install -g vercel
vercel --prod
```

### Netlify
```bash
npm run build:prod
# Upload da pasta dist/
```

### Docker
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build:prod
EXPOSE 4200
CMD ["npm", "start"]
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👥 Equipe

- **Desenvolvedor**: Arthur Bowens
- **Tecnologias**: Angular, TypeScript, Tailwind CSS
- **Backend**: Spring Boot, PostgreSQL, JWT

## 📞 Suporte

Para suporte, entre em contato através do email: arthur@exemplo.com

---

**Desenvolvido com ❤️ usando Angular e Tailwind CSS**