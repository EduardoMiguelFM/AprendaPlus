# ☁️ DevOps Tools & Cloud Computing - Guia Completo

## 📋 Requisitos Atendidos

### ✅ Provisionamento em Nuvem (Azure CLI)

- Scripts para criar recursos Azure
- Resource Group, PostgreSQL, Web App
- Configuração automática de variáveis de ambiente

### ✅ Azure DevOps

- Azure Boards (gestão de tarefas)
- Azure Repos (controle de versão)
- Azure Pipelines (CI/CD)

### ✅ Pipeline de Build

- Execução automática a cada commit
- Publicação de artefatos
- Execução e publicação de testes (JUnit)

### ✅ Pipeline de Release

- Deploy automático após build
- Deploy para Azure Web App (PaaS)
- Banco de dados PostgreSQL (PaaS)

## 🚀 Passo a Passo

### 1. Criar Projeto no Azure DevOps

1. Acesse [Azure DevOps](https://dev.azure.com)
2. Crie uma nova organização (se necessário)
3. Crie um novo projeto
4. Convide o professor com permissões:
   - Organização: **Basic**
   - Projeto: **Contributor**

### 2. Importar Código para Azure Repos

```bash
# Adicionar remote do Azure DevOps
git remote add azure https://dev.azure.com/[org]/[projeto]/_git/[repo]

# Push do código
git push azure main
```

### 3. Configurar Branch Protection

1. Vá em **Repos > Branches**
2. Selecione a branch `main`
3. Configure:
   - ✅ Require a minimum number of reviewers: 1
   - ✅ Check for linked work items: Required
   - ✅ Check for comment resolution: Required
   - ✅ Set default reviewer: [Seu RM]

### 4. Criar Work Item no Azure Boards

1. Vá em **Boards > Work Items**
2. Crie uma nova **Task** ou **User Story**
3. Vincule commits e PRs a este work item

### 5. Provisionar Infraestrutura

#### Windows (PowerShell)

```powershell
# Executar script de provisionamento
.\scripts\script-infra-azure.ps1
```

#### Linux/Mac (Bash)

```bash
# Dar permissão de execução
chmod +x scripts/script-infra-azure.sh

# Executar script
./scripts/script-infra-azure.sh
```

**Recursos criados:**

- Resource Group: `rg-aprenda-plus`
- PostgreSQL Server: `aprenda-db-server`
- Database: `aprenda_db`
- App Service Plan: `aprenda-plus-plan`
- Web App: `aprenda-plus`

### 6. Configurar Pipeline de Build

1. Vá em **Pipelines > Pipelines**
2. Clique em **New Pipeline**
3. Selecione **Azure Repos Git**
4. Selecione o repositório
5. Escolha **Existing Azure Pipelines YAML file**
6. Selecione o arquivo `azure-pipelines.yml` na raiz

**O pipeline irá:**

- ✅ Compilar o projeto com Maven
- ✅ Executar testes JUnit
- ✅ Publicar resultados dos testes
- ✅ Publicar artefatos (JAR)

### 7. Configurar Service Connection

1. Vá em **Project Settings > Service connections**
2. Crie uma nova conexão do tipo **Azure Resource Manager**
3. Configure com suas credenciais Azure
4. Nomeie como: `Azure-Service-Connection`

### 8. Configurar Pipeline de Release

O pipeline de release está configurado no mesmo arquivo `azure-pipelines.yml` e executa automaticamente após o build.

**O pipeline de release irá:**

- ✅ Fazer deploy do JAR para Azure Web App
- ✅ Configurar variáveis de ambiente
- ✅ Verificar status do deploy

### 9. Executar Script SQL no Banco

```bash
# Conectar ao PostgreSQL no Azure
psql -h aprenda-db-server.postgres.database.azure.com \
     -U aprenda_admin@aprenda-db-server \
     -d aprenda_db \
     -f scripts/script-bd.sql
```

Ou via Azure Portal:

1. Vá em **Azure Portal > PostgreSQL Server**
2. Abra **Query editor**
3. Execute o conteúdo de `scripts/script-bd.sql`

## 📁 Estrutura de Arquivos DevOps

```
Aprenda+/
├── azure-pipelines.yml          # Pipeline CI/CD
├── scripts/
│   ├── script-infra-azure.sh    # Script Azure CLI (Linux/Mac)
│   ├── script-infra-azure.ps1   # Script Azure CLI (Windows)
│   └── script-bd.sql            # Script de criação do banco
└── dockerfiles/
    ├── Dockerfile               # Dockerfile da aplicação
    └── .dockerignore           # Arquivos ignorados no build
```

## 🔐 Variáveis de Ambiente

Configure no Azure Web App:

```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://[server]:5432/[database]?sslmode=require
DB_USERNAME=[username]@[server]
DB_PASSWORD=[password]
RABBITMQ_HOST=[rabbitmq-host]
RABBITMQ_USERNAME=[username]
RABBITMQ_PASSWORD=[password]
OPENAI_API_KEY=[api-key]
```

## 📊 Monitoramento

- **Azure Portal**: Métricas da Web App
- **Application Insights**: Logs e telemetria
- **Azure Monitor**: Alertas e dashboards

## 🔄 Fluxo CI/CD

```
Commit → Push → Pull Request →
Review → Merge → Build Pipeline →
Testes → Artefatos → Release Pipeline →
Deploy → Azure Web App
```

## 📝 Checklist de Entrega

- [ ] Projeto criado no Azure DevOps
- [ ] Professor convidado com permissões corretas
- [ ] Código importado para Azure Repos
- [ ] Branch `main` protegida
- [ ] Work Item criado no Azure Boards
- [ ] Infraestrutura provisionada via script
- [ ] Pipeline de Build configurado e funcionando
- [ ] Pipeline de Release configurado e funcionando
- [ ] Script SQL executado no banco
- [ ] Deploy funcionando na Azure Web App
- [ ] Variáveis de ambiente configuradas
- [ ] Documentação atualizada

## 🆘 Troubleshooting

### Pipeline falha no build

- Verificar versão do Java no pipeline
- Verificar dependências no `build.gradle`
- Verificar logs do pipeline

### Deploy falha

- Verificar Service Connection
- Verificar variáveis de ambiente
- Verificar logs da Web App no Azure Portal

### Banco de dados não conecta

- Verificar firewall do PostgreSQL
- Verificar credenciais
- Verificar string de conexão

## 📚 Recursos Adicionais

- [Azure DevOps Documentation](https://docs.microsoft.com/azure/devops)
- [Azure CLI Reference](https://docs.microsoft.com/cli/azure)
- [Azure Pipelines YAML](https://docs.microsoft.com/azure/devops/pipelines/yaml-schema)








