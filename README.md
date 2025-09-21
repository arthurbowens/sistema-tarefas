# Sistema de Tarefas - API Backend

Sistema completo de gerenciamento de tarefas com integração ao Google Calendar, desenvolvido em Spring Boot.

## 🚀 Funcionalidades

### ✅ Gestão de Tarefas
- Criar, editar, excluir e visualizar tarefas
- Sistema de prioridades (Baixa, Média, Alta, Urgente)
- Status das tarefas (Pendente, Em Andamento, Concluída, Cancelada)
- Data de vencimento e conclusão
- Sistema de tags e cores personalizáveis
- Subtarefas (tarefas filhas)

### ✅ Checklist
- Adicionar itens de checklist às tarefas
- Marcar itens como concluídos
- Reordenar itens do checklist
- Controle de progresso automático

### ✅ Compartilhamento
- Compartilhar tarefas com outros usuários
- Diferentes níveis de permissão (Leitura, Escrita, Administrador)
- Convites com data de expiração
- Sistema de aceitação/rejeição de convites

### ✅ Integração Google Calendar
- Sincronização automática de tarefas com Google Calendar
- Criação de eventos baseados nas tarefas
- Atualização e exclusão de eventos
- Autenticação OAuth2 com Google

### ✅ Autenticação e Segurança
- Autenticação JWT (JSON Web Token)
- Criptografia de senhas com BCrypt
- Controle de acesso baseado em roles
- CORS configurado para desenvolvimento e produção

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (JSON Web Token)**
- **Google Calendar API**
- **Swagger/OpenAPI 3**
- **Lombok**
- **Maven**

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+
- Conta Google (para integração com Calendar)

## 🚀 Como Executar

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/sistema-tarefas.git
cd sistema-tarefas
```

### 2. Configure o banco de dados
```sql
CREATE DATABASE tarefas_db;
CREATE USER tarefas_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE tarefas_db TO tarefas_user;
```

### 3. Configure as variáveis de ambiente
Crie um arquivo `.env` na raiz do projeto:
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/tarefas_db
DATABASE_USERNAME=tarefas_user
DATABASE_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_jwt_aqui
JWT_EXPIRATION=86400000
```

### 4. Execute a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📚 Documentação da API

### Swagger UI
Acesse a documentação interativa da API em:
- **Desenvolvimento**: http://localhost:8080/swagger-ui.html
- **Produção**: https://tarefas-backend.onrender.com/swagger-ui.html

### Principais Endpoints

#### Autenticação
- `POST /api/auth/registro` - Registrar novo usuário
- `POST /api/auth/login` - Fazer login
- `GET /api/auth/perfil` - Obter perfil do usuário
- `PUT /api/auth/perfil` - Atualizar perfil

#### Tarefas
- `GET /api/tarefas` - Listar tarefas do usuário
- `POST /api/tarefas` - Criar nova tarefa
- `GET /api/tarefas/{id}` - Obter tarefa por ID
- `PUT /api/tarefas/{id}` - Atualizar tarefa
- `DELETE /api/tarefas/{id}` - Excluir tarefa
- `PUT /api/tarefas/{id}/concluir` - Marcar como concluída

#### Checklist
- `GET /api/tarefas/{tarefaId}/checklist` - Listar itens do checklist
- `POST /api/tarefas/{tarefaId}/checklist` - Adicionar item ao checklist
- `PUT /api/tarefas/{tarefaId}/checklist/{id}` - Atualizar item
- `DELETE /api/tarefas/{tarefaId}/checklist/{id}` - Excluir item

#### Compartilhamento
- `POST /api/tarefas/{tarefaId}/compartilhamento` - Compartilhar tarefa
- `GET /api/tarefas/{tarefaId}/compartilhamento` - Listar compartilhamentos
- `PUT /api/tarefas/{tarefaId}/compartilhamento/{id}/aceitar` - Aceitar convite

#### Google Calendar
- `GET /api/google-calendar/auth-url` - Obter URL de autorização
- `GET /api/google-calendar/callback` - Callback de autorização
- `GET /api/google-calendar/status` - Status da conexão

## 🐳 Deploy no Render

### 1. Configuração do Banco de Dados
1. Crie um banco PostgreSQL no Render
2. Anote a string de conexão fornecida

### 2. Deploy da Aplicação
1. Conecte seu repositório GitHub ao Render
2. Configure as variáveis de ambiente:
   - `DATABASE_URL`: String de conexão do banco
   - `JWT_SECRET`: Chave secreta para JWT
   - `JWT_EXPIRATION`: Tempo de expiração do token
   - `SPRING_PROFILES_ACTIVE`: production

### 3. Configuração do Google Calendar
1. Crie um projeto no Google Cloud Console
2. Ative a Google Calendar API
3. Crie credenciais OAuth2
4. Baixe o arquivo `credentials.json`
5. Faça upload para o Render

## 🔧 Configuração do Google Calendar

### 1. Criar Projeto no Google Cloud Console
1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione existente
3. Ative a Google Calendar API

### 2. Configurar OAuth2
1. Vá para "Credenciais" > "Criar Credenciais" > "ID do cliente OAuth"
2. Configure a tela de consentimento
3. Adicione URIs de redirecionamento:
   - `http://localhost:8080/api/google-calendar/callback` (desenvolvimento)
   - `https://tarefas-backend.onrender.com/api/google-calendar/callback` (produção)

### 3. Baixar Credenciais
1. Baixe o arquivo JSON das credenciais
2. Renomeie para `credentials.json`
3. Coloque na pasta `src/main/resources/` para desenvolvimento
4. Faça upload para o Render para produção

## 📊 Estrutura do Projeto

```
src/main/java/com/arthur/tarefas/
├── config/                 # Configurações
├── controller/             # Controllers REST
├── dto/                   # Data Transfer Objects
├── enums/                 # Enumeradores
├── exception/             # Exceções customizadas
├── integration/           # Integrações externas
├── model/                 # Entidades JPA
├── repository/            # Repositories JPA
├── security/              # Configurações de segurança
├── service/               # Services de negócio
└── util/                  # Utilitários
```

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

**Arthur Bowens**
- GitHub: [@arthurbowens](https://github.com/arthurbowens)
- Email: arthur@exemplo.com

## 🙏 Agradecimentos

- Spring Boot Team
- Google Calendar API
- Comunidade Java
- Todos os contribuidores do projeto
# sistema-tarefas
