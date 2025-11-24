# Aprenda+ – Plataforma Educacional Gamificada 🚀

### Projeto desenvolvido para o Challenge FIAP 2025 (2º ano – ADS)

WebApp completo construído com **Spring Boot** que oferece **experiências gamificadas de aprendizado**, conectando trilhas, cursos, desafios, pontuação e assistente inteligente. O objetivo é aumentar o engajamento dos alunos dentro do ecossistema Aprenda+.

---

## 🎯 Principais Funcionalidades

- ✅ **Onboarding inteligente** com seleção de áreas e níveis de conhecimento
- ✅ **Dashboard gamificado** com pontos, progresso e ranking
- ✅ **Catálogo de cursos e trilhas** com inscrição e acompanhamento
- ✅ **Sistema de desafios** com quiz, pontuação e troféus
- ✅ **Perfil completo do aluno** (dados pessoais, áreas de interesse, histórico)
- ✅ **Assistente IA** via Spring AI (OpenAI) para dúvidas sobre cursos/desafios
- ✅ **API REST** com endpoints para autenticação, cursos, desafios e trilhas
- ✅ **Interface web responsiva** com Thymeleaf + Bootstrap
- ♻️ **Mensageria interna** para processamento assíncrono
- 🔐 **Segurança com Spring Security** e perfis autenticados
- 🌍 **Internacionalização (pt-BR / en-US)**
- ⚡ **Caching com Caffeine** e paginação em listagens extensas

---

## 🧱 Entidades com CRUD Completo

| Entidade            | API (Swagger)                                                             | Interface Web                                        | Uso mobile/API                  |
| ------------------- | ------------------------------------------------------------------------- | ---------------------------------------------------- | ------------------------------- |
| **Cursos**          | `GET/POST/PUT/DELETE /api/cursos`                                         | `/cursos`, `/cursos/{id}` (listar, ver, inscrever)   | Consumido pelo app mobile e web |
| **Trilhas**         | `GET/POST/PUT/DELETE /api/trilhas`                                        | `/trilhas`, `/trilhas/{id}`                          | Dados recomendados para mobile  |
| **Desafios**        | `GET/POST/PUT/DELETE /api/desafios` + `POST /api/desafios/{id}/completar` | `/desafios`, `/desafios/{id}`, `/desafios/{id}/quiz` | Integrado ao front e à IA       |
| **Usuários/Perfil** | `POST /api/auth/*`, `GET/PUT /api/usuarios`                               | `/perfil`, `/login`, `/cadastro`                     | Mobile usa as mesmas APIs       |

Todos os módulos foram exercitados via UI e documentados no Swagger, garantindo cobertura REST e web.

---

## 🧪 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Data JPA** + **PostgreSQL**
- **Spring Security 6**
- **Spring Validation**
- **Spring AI + OpenAI**
- **Caffeine Cache**
- **Thymeleaf + Bootstrap**
- **Flyway** (migração de banco)
- **Swagger/OpenAPI**
- **Docker** (opcional)
- **Azure App Service + Azure Database for PostgreSQL**

---

## ▶️ Como Rodar

### 🏠 Execução Local

#### 1. Clonar o projeto

```bash
git clone https://github.com/EduardoMiguelFM/AprendaPlus.git
cd Aprenda+
```

#### 2. Configurar PostgreSQL local

- Porta: **5432**
- DB: **Aprenda**
- Usuário: **postgres**
- Senha: **dudu0602**

#### 3. Definir variável para o Spring AI

Windows (cmd ou PowerShell):

```bash
setx SPRING_AI_OPENAI_API_KEY "sk-proj-sua-chave-aqui"
```

> Abra um novo terminal após definir.

#### 4. Executar aplicação

```bash
./gradlew bootRun
```

#### 5. Acessar

- UI (login): `http://localhost:8080/login`
- Dashboard: `http://localhost:8080/dashboard`
- Swagger: `http://localhost:8080/swagger-ui.html`

> **💡 Aplicação em Produção**: Acesse [https://aprendaplus-web-0703.azurewebsites.net/](https://aprendaplus-web-0703.azurewebsites.net/) para testar a versão deployada

### 🐳 Execução com Docker

```bash
docker build -t aprenda-plus .
docker run -p 8080:8080 -e SPRING_AI_OPENAI_API_KEY=sk-proj-sua-chave aprenda-plus
```

### ☁️ Deploy no Azure

#### Opção 1: Script Automatizado (Recomendado)

```bash
# Execute o script de deploy completo
./scripts/deploy-aprendaplus-cloud.sh
```

O script automatiza:

1. Criação do Resource Group
2. Provisionamento do PostgreSQL Flexible Server
3. Criação do App Service Plan e Web App
4. Configuração de variáveis de ambiente
5. Build e deploy do JAR

#### Opção 2: Manual

1. `./gradlew clean bootJar`
2. Crie App Service + PostgreSQL conforme Script 'deploy-aprendaplus-cloud.sh'
3. Configure variáveis no App Service:
   - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_PROFILES_ACTIVE=cloud`
   - `SPRING_AI_OPENAI_API_KEY`
4. Faça deploy do JAR via ZIP deploy ou pipeline Azure DevOps
5. Reinicie e monitore com `az webapp log tail`

---

## 🧠 Módulos & Fluxos

### 🔓 Autenticação e Perfil

- Login por e-mail/senha
- Atualização de dados pessoais
- Áreas de interesse e níveis

### 📚 Cursos e Trilhas

- Listagem paginada
- Inscrição e acompanhamento
- Progresso por curso/trilha

### 🕹️ Desafios e Gamificação

- Quiz com perguntas dinâmicas
- Pontos, troféus e ranking
- Histórico de desafios no perfil

### 🤖 Assistente IA Aprenda+

- Endpoint: `POST /api/ia/chat`
- Responde dúvidas sobre cursos, progresso e próximos passos
- Contextualiza com pontos e estatísticas do usuário
- indica cursos com base nos seus interesses

---

## 📦 Estrutura

```
Aprenda+/
├── src/main/java/br/com/fiap/Aprenda
│   ├── config/        # Security, Cache, OpenAPI, etc.
│   ├── controller/    # REST + Web MVC
│   ├── dto/           # Transfer Objects
│   ├── exception/     # Global handlers
│   ├── message/       # Integrações com RabbitMQ
│   ├── model/         # Entidades JPA
│   ├── repository/    # Spring Data
│   └── service/       # Regras de negócio
├── src/main/resources
│   ├── templates/     # Thymeleaf
│   ├── static/        # JS/CSS
│   ├── messages*.properties
│   └── application*.properties
├── scripts/           # SQL, utilidades e deploy
├── dockerfiles/       # Dockerfile base
└── README-*.md        # Documentações por disciplina
```

---

## 🔄 Endpoints Principais

| Recurso       | Endpoints (exemplos)                                          |
| ------------- | ------------------------------------------------------------- |
| Autenticação  | `POST /api/auth/login`, `POST /api/auth/registrar`            |
| Cursos        | `GET /api/cursos`, `GET /api/cursos/{id}`, `POST /api/cursos` |
| Trilhas       | `GET /api/trilhas`, `GET /api/trilhas/{id}`                   |
| Desafios      | `GET /api/desafios`, `POST /api/desafios/{id}/completar`      |
| IA Assistente | `POST /api/ia/chat`                                           |
| Perfil (Web)  | `/perfil`, `/perfil/preferencias`, `/perfil/atualizar`        |

## 📡 Exemplos de CRUD (JSON)

### 📚 Cursos

#### Listar Cursos (GET)

```http
GET /api/cursos?pagina=0&tamanho=10
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "dados": [
      {
        "id": 1,
        "titulo": "Java Avançado",
        "descricao": "Curso completo de Java com Spring Boot",
        "area": "programacao",
        "duracao": "40 horas",
        "nivel": "Avançado",
        "icone": "💻",
        "instrutor": "Prof. João Silva",
        "avaliacao": 4.8,
        "totalAulas": 20
      }
    ],
    "paginacao": {
      "pagina": 0,
      "tamanho": 10,
      "total": 50,
      "totalPaginas": 5
    }
  },
  "mensagem": "Cursos recuperados com sucesso"
}
```

#### Obter Curso por ID (GET)

```http
GET /api/cursos/1
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "titulo": "Java Avançado",
    "descricao": "Curso completo de Java com Spring Boot",
    "area": "programacao",
    "duracao": "40 horas",
    "nivel": "Avançado",
    "icone": "💻",
    "conteudo": "Módulos: 1. Spring Framework, 2. JPA/Hibernate, 3. REST APIs",
    "instrutor": "Prof. João Silva",
    "avaliacao": 4.8,
    "totalAulas": 20
  },
  "mensagem": "Curso recuperado com sucesso"
}
```

#### Inscrever-se em Curso (POST)

```http
POST /api/cursos/1/inscrever
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "usuario": {...},
    "curso": {...},
    "status": "em_andamento",
    "progresso": 0,
    "inscritoEm": "2025-01-15T10:30:00"
  },
  "mensagem": "Inscrição realizada com sucesso"
}
```

#### Atualizar Progresso do Curso (PUT)

```http
PUT /api/cursos/1/progresso?progresso=50
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "progresso": 50,
    "status": "em_andamento"
  },
  "mensagem": "Progresso atualizado com sucesso"
}
```

---

### 🎯 Trilhas

#### Listar Trilhas (GET)

```http
GET /api/trilhas?pagina=0&tamanho=10
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "dados": [
      {
        "id": 1,
        "titulo": "Trilha Full Stack Java",
        "descricao": "Aprenda desenvolvimento completo com Java",
        "area": "programacao",
        "nivelMinimo": "Iniciante",
        "icone": "🚀",
        "cor": "#007bff",
        "cursos": [...],
        "desafios": [...]
      }
    ],
    "paginacao": {
      "pagina": 0,
      "tamanho": 10,
      "total": 15,
      "totalPaginas": 2
    }
  },
  "mensagem": "Trilhas recuperadas com sucesso"
}
```

#### Obter Trilha por ID (GET)

```http
GET /api/trilhas/1
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "titulo": "Trilha Full Stack Java",
    "descricao": "Aprenda desenvolvimento completo com Java",
    "area": "programacao",
    "nivelMinimo": "Iniciante",
    "icone": "🚀",
    "cor": "#007bff",
    "cursos": [
      {
        "id": 1,
        "titulo": "Java Básico",
        "nivel": "Iniciante"
      },
      {
        "id": 2,
        "titulo": "Spring Boot",
        "nivel": "Intermediário"
      }
    ]
  },
  "mensagem": "Trilha recuperada com sucesso"
}
```

#### Inscrever-se em Trilha (POST)

```http
POST /api/trilhas/1/inscrever
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "usuario": {...},
    "trilha": {...},
    "inscritoEm": "2025-01-15T10:30:00"
  },
  "mensagem": "Inscrição na trilha realizada com sucesso"
}
```

#### Obter Progresso da Trilha (GET)

```http
GET /api/trilhas/1/progresso
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "progressoGeral": 45,
    "cursosCompletos": 2,
    "totalCursos": 5,
    "desafiosCompletos": 1,
    "totalDesafios": 3
  },
  "mensagem": "Progresso recuperado com sucesso"
}
```

---

### 🧠 Desafios

#### Listar Desafios (GET)

```http
GET /api/desafios?area=programacao&nivel=Intermediário&tipo=quiz
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "dados": [
      {
        "id": 1,
        "titulo": "Quiz de Java",
        "descricao": "Teste seus conhecimentos em Java",
        "tipo": "quiz",
        "area": "programacao",
        "nivel": "Intermediário",
        "pontos": 200,
        "icone": "🧠",
        "dificuldade": "Médio"
      }
    ],
    "paginacao": {
      "pagina": 0,
      "tamanho": 20,
      "total": 25,
      "totalPaginas": 2
    }
  },
  "mensagem": "Desafios recuperados com sucesso"
}
```

#### Obter Desafio por ID (GET)

```http
GET /api/desafios/1
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "titulo": "Quiz de Java",
    "descricao": "Teste seus conhecimentos em Java",
    "tipo": "quiz",
    "area": "programacao",
    "nivel": "Intermediário",
    "pontos": 200,
    "icone": "🧠",
    "dificuldade": "Médio"
  },
  "mensagem": "Desafio recuperado com sucesso"
}
```

#### Obter Perguntas do Desafio (GET)

```http
GET /api/desafios/1/perguntas
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": [
    {
      "id": 1,
      "pergunta": "O que é Spring Boot?",
      "opcoes": [
        "Framework Java",
        "Linguagem de programação",
        "Banco de dados",
        "Editor de código"
      ],
      "respostaCorreta": 0
    }
  ],
  "mensagem": "Perguntas recuperadas com sucesso"
}
```

#### Completar Desafio (POST)

```http
POST /api/desafios/1/completar
Authorization: Bearer {token}
Content-Type: application/json

{
  "respostas": [
    {"perguntaId": 1, "resposta": 0},
    {"perguntaId": 2, "resposta": 1}
  ]
}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "pontosGanhos": 200,
    "pontuacao": 85,
    "totalPerguntas": 10
  },
  "mensagem": "Desafio concluído com sucesso"
}
```

#### Verificar Status do Desafio (GET)

```http
GET /api/desafios/1/status
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "completo": true,
    "concluidoEm": "2025-01-15T11:00:00",
    "pontuacao": 85,
    "pontosGanhos": 200
  },
  "mensagem": "Status recuperado com sucesso"
}
```

---

### 👤 Usuários

#### Cadastrar Usuário (POST)

```http
POST /api/auth/cadastro
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao.silva@fiap.com.br",
  "senha": "SenhaSegura123!",
  "confirmarSenha": "SenhaSegura123!"
}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao.silva@fiap.com.br"
  },
  "mensagem": "Usuário cadastrado com sucesso"
}
```

#### Login (POST)

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao.silva@fiap.com.br",
  "senha": "SenhaSegura123!"
}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "usuario": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao.silva@fiap.com.br"
    }
  },
  "mensagem": "Login realizado com sucesso"
}
```

#### Obter Perfil (GET)

```http
GET /api/usuarios/perfil
Authorization: Bearer {token}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao.silva@fiap.com.br",
    "pontos": 1250,
    "nivel": "Intermediário"
  },
  "mensagem": "Perfil recuperado com sucesso"
}
```

#### Atualizar Perfil (PUT)

```http
PUT /api/usuarios/perfil
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "João Silva Santos",
  "telefone": "(11) 98765-4321"
}
```

**Response:**

```json
{
  "sucesso": true,
  "dados": {
    "id": 1,
    "nome": "João Silva Santos",
    "email": "joao.silva@fiap.com.br",
    "telefone": "(11) 98765-4321"
  },
  "mensagem": "Perfil atualizado com sucesso"
}
```

---

## ✅ Checklist de Requisitos Acadêmicos

- [x] Spring annotations / DI
- [x] Model + DTO
- [x] Spring Data JPA
- [x] Bean Validation
- [x] Caching (Caffeine)
- [x] Internacionalização (pt/en)
- [x] Paginação
- [x] Spring Security
- [x] Tratamento de erros
- [x] Mensageria interna (processos assíncronos)
- [x] IA Generativa (Spring AI)
- [x] Deploy em nuvem (Azure)
- [x] API REST com verbos e status corretos
- [x] Interface web integrada

---

## 📹 Vídeos & Documentação

### 🎥 Vídeos de Apresentação

- **Vídeo Pitch (JAVA)**: [Assistir no YouTube](https://youtu.be/vFQ52cdzKfk)
- **Vídeo Funcionalidades (JAVA)**: [Assistir no YouTube](https://youtu.be/y3EUrky8pig)
- **Vídeo DEVOPS**: [Assistir no YouTube](https://youtu.be/rJ5AEc8tutU)

### 🔗 Links Importantes

- **Repositório GitHub**: [https://github.com/EduardoMiguelFM/AprendaPlus.git](https://github.com/EduardoMiguelFM/AprendaPlus.git)
- **Aplicação em Nuvem**: [https://aprendaplus-web-0703.azurewebsites.net/](https://aprendaplus-web-0703.azurewebsites.net/)


---

## 👥 Equipe

- Eduardo Miguel Forato Monteiro – RM 555871
- Cícero Gabriel Oliveira Serafim – RM 556996
- Murillo Ari Ferreira Sant'Anna – RM 557183

---

## 📄 Licença

Projeto acadêmico desenvolvido para o Challenge FIAP. Uso restrito para fins educacionais.
