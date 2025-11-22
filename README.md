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
- **RabbitMQ** (mensageria)
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
git clone https://github.com/seu-usuario/aprenda-plus.git
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

### 🐳 Execução com Docker

```bash
docker build -t aprenda-plus .
docker run -p 8080:8080 -e SPRING_AI_OPENAI_API_KEY=sk-proj-sua-chave aprenda-plus
```

### ☁️ Deploy no Azure (resumo)

1. `./gradlew clean bootJar`
2. Crie App Service + PostgreSQL conforme [README-DEVOPS.md](README-DEVOPS.md)
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

> Documentação completa disponível no Swagger e em `README-JAVA.md`.

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

- [README-JAVA.md](README-JAVA.md) – Detalhes técnicos Java Advanced
- [README-DEVOPS.md](README-DEVOPS.md) – Deploy completo no Azure
- [README-QA.md](README-QA.md) – Qualidade e testes
- [README-MOBILE.md](README-MOBILE.md) – Integração com app mobile
- Vídeos de apresentação (links fornecidos na banca – atualize aqui se necessário)

---

## 👥 Equipe

- Eduardo Miguel Forato Monteiro – RM 555871
- Cícero Gabriel Oliveira Serafim – RM 556996
- Murillo Ari Ferreira Sant'Anna – RM 557183

---

## 📄 Licença

Projeto acadêmico desenvolvido para o Challenge FIAP. Uso restrito para fins educacionais.
