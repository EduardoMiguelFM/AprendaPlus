# 📋 Guia Completo - Entregas GS (QA & DevOps)

## 🎯 Qual Disciplina Fazer Primeiro?

### ✅ **Recomendação: DEVOPS primeiro, depois QA**

**Motivo:**

- DevOps configura a infraestrutura, repositório, pipelines e Boards que o QA vai usar
- QA precisa do projeto Azure DevOps já configurado para criar Test Plans e Cases
- DevOps é mais técnico (scripts, pipelines); QA é mais documental (testes manuais)

**Ordem sugerida:**

1. **DEVOPS TOOLS & CLOUD COMPUTING** (primeiro)
2. **COMPLIANCE, QUALITY ASSURANCE & TESTS** (depois)

---

## ✅ O Que Já Foi Feito

### DEVOPS TOOLS & CLOUD COMPUTING

- ✅ **Projeto Azure DevOps criado**
- ✅ **Professores adicionados** (Basic + Contributor)
- ✅ **Aplicação em nuvem** (Web App + PostgreSQL)
- ✅ **Script de deploy** (`scripts/deploy-aprendaplus-cloud.sh`)
- ✅ **Repositório** (código-fonte no Azure Repos ou GitHub)
- ✅ **Arquivos de infraestrutura**:
  - `scripts/deploy-aprendaplus-cloud.sh` (Azure CLI)
  - `scripts/script-bd.sql` (script SQL)
  - `dockerfiles/Dockerfile` (se usar Docker)
  - `azure-pipelines.yml` (pipeline YAML)

### COMPLIANCE, QUALITY ASSURANCE & TESTS

- ⚠️ **Ainda não iniciado** (depende do DevOps estar completo)

---

## ❌ O Que Falta Fazer

### DEVOPS TOOLS & CLOUD COMPUTING

#### 1. Azure Boards

- [ ] Criar **Work Item inicial** (Task/User Story)
- [ ] Vincular commits, branches e PRs ao Work Item
- [ ] Configurar **branch principal protegida**:
  - Revisor obrigatório
  - Vinculação de Work Item obrigatória
  - Revisor padrão (seu RM)

#### 2. Azure Repos

- [ ] Garantir código no **Azure Repos** (importar se estiver no GitHub)
- [ ] Criar **branch de feature** a partir da Task
- [ ] Fazer commits vinculados ao Work Item
- [ ] Criar **Pull Request** vinculado ao Work Item
- [ ] Fazer merge via PR (simular aprovação)

#### 3. Azure Pipelines - Build

- [ ] Criar pipeline de **Build** (YAML ou Classic)
- [ ] Configurar trigger automático (commit na branch principal)
- [ ] Adicionar etapas:
  - Build da aplicação
  - Executar testes (JUnit)
  - Publicar artefatos
  - Publicar resultados de testes

#### 4. Azure Pipelines - Release

- [ ] Criar pipeline de **Release**
- [ ] Configurar trigger automático (após Build gerar artefato)
- [ ] Adicionar etapas de deploy:
  - Deploy para Web App (PaaS)
  - Configurar variáveis de ambiente (protegidas)
  - Validar deploy

#### 5. Documentação

- [ ] Atualizar `README.md` com **CRUD em JSON** (exemplos de requests/responses)
- [ ] Criar **desenho macro da arquitetura** (Visual Paradigm ou draw.io)
- [ ] Documentar variáveis de ambiente no README

### COMPLIANCE, QUALITY ASSURANCE & TESTS

#### 1. Azure Boards - Features

- [ ] Criar **Features** no Azure Boards (Scrum)
- [ ] Documentar cada Feature:
  - Descrição completa
  - Critérios de aceite
- [ ] Tirar prints:
  - Lista de Features
  - Detalhes de cada Feature

#### 2. Test Plans e Test Cases

- [ ] Criar **Test Plan** no Azure Test Plans
- [ ] Criar **Test Cases** para cada Feature (mínimo 1 por Feature)
- [ ] Configurar cada Test Case:
  - **Sumário** com pré-requisitos
  - **Passos** detalhados
  - **Param Values** (entradas e saídas esperadas)
- [ ] Tirar prints:
  - Configuração do sumário
  - Passos dos testes

#### 3. Execução de Testes

- [ ] Executar todos os Test Cases no Azure
- [ ] Documentar execução passo a passo:
  - Painel de execução (More options > View Test Results)
  - Painel de resumo dos testes
- [ ] Gerar gráficos:
  - **Gráfico de Outcome** (por Test Plan)
  - **Gráfico de Run by** (por Test Plan)
- [ ] Tirar prints de todas as telas

#### 4. Documento de Resposta

- [ ] Criar PDF com:
  - Prints das Features
  - Prints dos Test Cases
  - Prints das execuções
  - Link do projeto Azure DevOps (Overview > Summary)
  - Gráficos gerados

---

## 📝 Passo a Passo Detalhado

### 🔧 DEVOPS TOOLS & CLOUD COMPUTING

#### **Passo 1: Configurar Azure Boards**

1. Acesse: `https://dev.azure.com/<sua-org>/<seu-projeto>`
2. Vá em **Boards** > **Work Items**
3. Clique em **New Work Item** > **Task**
4. Preencha:
   - **Title**: "Configurar pipeline de CI/CD"
   - **Description**: Detalhe o que será feito
   - **Assigned to**: Seu usuário
5. Salve e anote o **ID do Work Item** (ex: #123)

#### **Passo 2: Configurar Branch Protegida**

1. Vá em **Repos** > **Branches**
2. Clique nos **3 pontos** da branch `main` > **Branch policies**
3. Configure:
   - ✅ **Require a minimum number of reviewers**: 1
   - ✅ **Check for linked work items**: Required
   - ✅ **Check for comment resolution**: Required
   - **Default reviewers**: Adicione seu RM
4. Salve

#### **Passo 3: Criar Branch e Commits Vinculados**

1. No terminal local:
   ```bash
   git checkout -b feature/configurar-pipelines
   ```
2. Faça alterações (ex: atualizar README)
3. Commit vinculando ao Work Item:
   ```bash
   git commit -m "feat: atualizar README #123"
   ```
   (Substitua #123 pelo ID do seu Work Item)
4. Push:
   ```bash
   git push origin feature/configurar-pipelines
   ```

#### **Passo 4: Criar Pull Request**

1. No Azure DevOps, vá em **Repos** > **Pull requests**
2. Clique em **New Pull Request**
3. Configure:
   - **Source**: `feature/configurar-pipelines`
   - **Target**: `main`
   - **Title**: "Configurar pipelines CI/CD"
   - **Description**: "Vinculado ao Work Item #123"
   - **Work Items**: Selecione o Work Item #123
4. Crie o PR
5. **Aprove o próprio PR** (simulação)
6. Faça **Complete** (merge)

#### **Passo 5: Criar Pipeline de Build (YAML)**

1. Vá em **Pipelines** > **Pipelines**
2. Clique em **New Pipeline**
3. Selecione **Azure Repos Git** (ou GitHub se usar)
4. Selecione o repositório
5. Escolha **Starter pipeline** (ou configure manualmente)
6. Substitua o YAML por:

```yaml
trigger:
  branches:
    include:
      - main
  paths:
    exclude:
      - README.md

pool:
  vmImage: "ubuntu-latest"

variables:
  javaVersion: "21"
  mavenVersion: "3.9.0"

stages:
  - stage: Build
    displayName: "Build e Testes"
    jobs:
      - job: Build
        displayName: "Compilar e Testar"
        steps:
          - task: JavaToolInstaller@0
            inputs:
              versionSpec: "21"
              jdkArchitecture: "x64"

          - task: Gradle@2
            displayName: "Build com Gradle"
            inputs:
              workingDirectory: "."
              gradleWrapperFile: "gradlew"
              options: "-PskipTests=false"
              tasks: "clean build"
              publishJUnitResults: true
              testResultsFiles: "**/TEST-*.xml"
              javaHomeOption: "JDKVersion"
              jdkVersionOption: "default"
              jdkArchitectureOption: "x64"

          - task: PublishTestResults@2
            displayName: "Publicar Resultados de Testes"
            inputs:
              testResultsFormat: "JUnit"
              testResultsFiles: "**/TEST-*.xml"
              failTaskOnFailedTests: true

          - task: PublishBuildArtifacts@1
            displayName: "Publicar Artefatos"
            inputs:
              PathtoPublish: "$(System.DefaultWorkingDirectory)/build/libs"
              ArtifactName: "drop"
              publishLocation: "Container"
```

7. Salve e commite o arquivo `azure-pipelines.yml` na raiz
8. Execute o pipeline manualmente para testar

#### **Passo 6: Criar Pipeline de Release**

1. Vá em **Pipelines** > **Releases**
2. Clique em **New Pipeline**
3. Escolha **Empty job**
4. Configure:
   - **Stage name**: "Deploy to Azure"
   - **Artifact**: Selecione o Build pipeline
   - **Trigger**: **After stage** (após Build)
5. Adicione tarefas:
   - **Azure App Service deploy**
     - **Azure subscription**: Selecione sua subscription
     - **App Service type**: Web App on Linux
     - **App Service name**: Nome do seu Web App
     - **Package or folder**: `$(System.DefaultWorkingDirectory)/drop/*.jar`
6. Configure **Variables**:
   - `SPRING_DATASOURCE_URL` (marcar como secret)
   - `SPRING_DATASOURCE_USERNAME` (secret)
   - `SPRING_DATASOURCE_PASSWORD` (secret)
   - `SPRING_AI_OPENAI_API_KEY` (secret)
7. Salve

#### **Passo 7: Atualizar README com CRUD JSON**

Adicione no `README.md` uma seção como:

````markdown
## 📡 Exemplos de CRUD (JSON)

### Criar Curso

```json
POST /api/cursos
{
  "titulo": "Java Avançado",
  "descricao": "Curso completo de Java",
  "area": "programacao",
  "nivel": "Avançado",
  "pontos": 500
}
```
````

### Listar Cursos

```json
GET /api/cursos
Response: [
  {
    "id": 1,
    "titulo": "Java Avançado",
    "area": "programacao",
    "nivel": "Avançado"
  }
]
```

### Atualizar Curso

```json
PUT /api/cursos/1
{
  "titulo": "Java Avançado - Atualizado",
  "pontos": 600
}
```

### Deletar Curso

```json
DELETE /api/cursos/1
Response: 204 No Content
```

```

Repita para **Trilhas** e **Desafios**.

#### **Passo 8: Criar Desenho da Arquitetura**

1. Use **Visual Paradigm** ou **draw.io**
2. Desenhe:
   - Azure DevOps (Boards, Repos, Pipelines)
   - Azure Cloud (Resource Group, Web App, PostgreSQL)
   - Fluxo: Developer → Commit → PR → Build → Release → Deploy
3. Salve como imagem e adicione no README

---

### 🧪 COMPLIANCE, QUALITY ASSURANCE & TESTS

#### **Passo 1: Criar Features no Azure Boards**

1. Vá em **Boards** > **Backlogs**
2. Mude a visualização para **Features**
3. Clique em **New Work Item** > **Feature**
4. Crie Features para cada funcionalidade principal:
   - **Feature 1**: "Sistema de Autenticação"
     - **Description**: "Permitir cadastro, login e gerenciamento de perfil"
     - **Acceptance Criteria**:
       - ✅ Usuário pode se cadastrar com email e senha
       - ✅ Usuário pode fazer login
       - ✅ Usuário pode atualizar perfil
   - **Feature 2**: "Gerenciamento de Cursos"
     - **Description**: "Visualizar, inscrever e acompanhar cursos"
     - **Acceptance Criteria**:
       - ✅ Listar cursos disponíveis
       - ✅ Ver detalhes do curso
       - ✅ Inscrever-se em curso
       - ✅ Acompanhar progresso
   - **Feature 3**: "Sistema de Desafios"
     - **Description**: "Realizar desafios e ganhar pontos"
     - **Acceptance Criteria**:
       - ✅ Listar desafios disponíveis
       - ✅ Iniciar quiz de desafio
       - ✅ Submeter respostas
       - ✅ Receber pontuação
   - **Feature 4**: "Assistente IA"
     - **Description**: "Interagir com assistente inteligente"
     - **Acceptance Criteria**:
       - ✅ Enviar pergunta ao assistente
       - ✅ Receber resposta contextualizada
5. **Tire prints** de:
   - Lista de Features
   - Detalhes de cada Feature (com descrição e critérios)

#### **Passo 2: Criar Test Plan**

1. Vá em **Test Plans** (menu lateral)
2. Clique em **+ New Test Plan**
3. Configure:
   - **Name**: "Test Plan - Aprenda+ v1.0"
   - **Area Path**: Selecione o projeto
   - **Iteration**: Selecione a sprint/iteração
4. Salve

#### **Passo 3: Criar Test Suites e Test Cases**

1. No Test Plan, clique em **+ Add** > **New Test Suite** > **Requirement-based**
2. Selecione uma **Feature** (ex: "Sistema de Autenticação")
3. Isso cria uma Test Suite vinculada à Feature
4. Clique em **+ New** > **New Test Case**
5. Configure o Test Case:

**Exemplo: Test Case - Cadastro de Usuário**

- **Title**: "TC001 - Cadastrar novo usuário com sucesso"
- **Sumário** (pré-requisitos):
```

Pré-requisitos:

- Aplicação rodando em https://aprendaplus-web-xxxxx.azurewebsites.net
- Banco de dados conectado e migrado
- Nenhum usuário com email "teste@fiap.com.br" cadastrado

```
- **Passos** (usando Param Values):
| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Acessar `/cadastro` | Página de cadastro carrega |
| 2 | Preencher nome: `{{nome}}` | Campo preenchido |
| 3 | Preencher email: `{{email}}` | Campo preenchido |
| 4 | Preencher senha: `{{senha}}` | Campo preenchido |
| 5 | Clicar em "Cadastrar" | Usuário criado, redireciona para login |
| 6 | Verificar no banco | Registro existe na tabela `usuario` |

- **Param Values** (aba separada):
| nome | email | senha |
|------|-------|-------|
| João Silva | joao@teste.com | senha123 |
| Maria Santos | maria@teste.com | senha456 |

6. Repita para cada Feature (mínimo 1 Test Case por Feature)

#### **Passo 4: Executar Testes**

1. No Test Plan, clique em **Run** (ou **Execute**)
2. Para cada Test Case:
 - Clique em **Run**
 - Execute os passos manualmente na aplicação
 - Marque cada passo como **Passed** ou **Failed**
 - Adicione comentários se necessário
 - Salve o resultado
3. **Tire prints** de:
 - Painel de execução (More options > View Test Results)
 - Cada passo executado
 - Resultado final

#### **Passo 5: Gerar Gráficos**

1. No Test Plan, vá em **Charts**
2. Adicione gráficos:
 - **Outcome** (Passed/Failed/Blocked)
 - **Run by** (quem executou)
3. **Tire prints** dos gráficos

#### **Passo 6: Criar Documento de Resposta**

1. Crie um documento Word/Google Docs
2. Inclua:
 - **Capa**: Nome do grupo, RMs, nomes completos
 - **Link do projeto**: `https://dev.azure.com/<org>/<projeto>/_overview`
 - **Seção 1**: Prints das Features (lista + detalhes)
 - **Seção 2**: Prints dos Test Cases (sumário + passos)
 - **Seção 3**: Prints das execuções (passo a passo + resumo)
 - **Seção 4**: Gráficos (Outcome + Run by)
3. Exporte como **PDF**
4. Nomeie: `GS_<nomeGrupo>.pdf`

---

## 📦 Checklist Final

### DEVOPS
- [ ] Projeto Azure DevOps criado
- [ ] Professores adicionados (Basic + Contributor)
- [ ] Work Item criado no Boards
- [ ] Branch protegida configurada
- [ ] Commits vinculados ao Work Item
- [ ] Pull Request criado e aprovado
- [ ] Pipeline de Build funcionando
- [ ] Pipeline de Release funcionando
- [ ] Artefatos publicados
- [ ] Testes publicados
- [ ] README com CRUD JSON
- [ ] Desenho da arquitetura
- [ ] Scripts Azure CLI no repositório
- [ ] Variáveis de ambiente protegidas
- [ ] Vídeo gravado (720p, narrado)

### QA
- [ ] Features criadas com descrição e critérios
- [ ] Test Plan criado
- [ ] Test Cases criados (mínimo 1 por Feature)
- [ ] Test Cases com Param Values
- [ ] Testes executados
- [ ] Gráficos gerados
- [ ] Documento PDF criado
- [ ] Prints incluídos no PDF

---

## 🎥 Gravação do Vídeo (DevOps)

### Requisitos Técnicos
- Resolução mínima: **720p**
- Áudio claro
- Narração por voz (sem legendas)

### Roteiro do Vídeo

1. **Apresentar README** (2 min)
 - Explicar conceito e arquitetura
 - Mostrar desenho da arquitetura

2. **Portal Azure** (3 min)
 - Mostrar recursos criados pelos scripts
 - Resource Group, Web App, PostgreSQL

3. **Azure DevOps - Boards** (2 min)
 - Criar nova Task no Boards
 - Mostrar Work Item

4. **Azure DevOps - Repos** (2 min)
 - Mostrar branch e commits
 - Mostrar Pull Request vinculado

5. **Azure DevOps - Pipelines** (5 min)
 - Executar Build pipeline
 - Mostrar etapas, artefatos, testes
 - Executar Release pipeline
 - Mostrar deploy

6. **Demonstrar Aplicação** (3 min)
 - Acessar aplicação em nuvem
 - Mostrar funcionalidades

7. **Testes CRUD** (5 min)
 - Executar CRUD em **Cursos** (Create, Read, Update, Delete)
 - Executar CRUD em **Trilhas** (Create, Read, Update, Delete)
 - Mostrar resultados no banco (pgAdmin ou Azure Portal)

8. **Finalização** (1 min)
 - Mostrar Task concluída
 - Mostrar links (Commits, PR, etc.)

**Duração total estimada: ~23 minutos**

---

## 🔗 Links Úteis

- **Azure DevOps**: `https://dev.azure.com/<sua-org>/<seu-projeto>`
- **Portal Azure**: `https://portal.azure.com`
- **Aplicação**: `https://aprendaplus-web-xxxxx.azurewebsites.net`
- **Swagger**: `https://aprendaplus-web-xxxxx.azurewebsites.net/swagger-ui.html`

---

## ⚠️ Atenção - Penalidades

### Penalidades que resultam em nota zero:
- ❌ Ausência do código fonte no repositório
- ❌ Ausência do `azure-pipelines.yml`
- ❌ Vídeo sem narração ou com legendas
- ❌ Aplicativo em localhost (não em nuvem)
- ❌ Projeto sem acesso ao Azure DevOps
- ❌ Plágio ou cópia

### Penalidades parciais:
- ❌ Ausência de `script-bd.sql`: -5 pontos
- ❌ Ausência de scripts Azure CLI: -10 pontos por script
- ❌ Ausência de CRUD JSON no README: -10 pontos
- ❌ Variáveis não protegidas: -20 pontos
- ❌ Testes não publicados: -15 pontos
- ❌ Vídeo sem qualidade 720p: -20 pontos
- ❌ CRUD não demonstrado: -30 pontos

---

## 📞 Suporte

Em caso de dúvidas, consulte:
- Documentação Azure DevOps: https://docs.microsoft.com/azure/devops
- Documentação Azure CLI: https://docs.microsoft.com/cli/azure
- Documentação Test Plans: https://docs.microsoft.com/azure/devops/test/

---

**Boa sorte com as entregas! 🚀**

```
