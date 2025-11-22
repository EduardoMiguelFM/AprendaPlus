# ✅ Compliance, Quality Assurance & Tests

## 📋 Requisitos Atendidos

### ✅ Features no Azure Boards (30%)

- Features criadas com descrição completa
- Critérios de aceite documentados
- Screenshots das Features

### ✅ Casos de Teste Manuais (40%)

- Casos de teste para cada Feature
- Dados de input e output esperados
- Uso de "Param Values" para entradas/saídas
- Pré-requisitos documentados no sumário
- Screenshots da configuração dos testes

### ✅ Execução de Testes (30%)

- Execução dos casos de teste no Azure
- Painéis de execução passo a passo
- Gráficos de Outcome por Test Plan
- Gráficos de Run by por Test Plan
- Screenshots dos painéis

## 🚀 Passo a Passo

### 1. Configurar Projeto no Azure DevOps

**IMPORTANTE**: O mesmo projeto usado para DevOps pode ser usado para QA!

1. Acesse o projeto no Azure DevOps
2. Vá em **Project Settings > Teams**
3. Adicione o professor como **Administrador** do projeto
4. Link: `https://dev.azure.com/[org]/[projeto]/_settings/teams`

### 2. Criar Features (30% da nota)

#### 2.1. Criar Feature 1: Autenticação de Usuário

1. Vá em **Boards > Work Items**
2. Clique em **New Work Item > Feature**
3. Preencha:

**Título**: `Feature: Autenticação de Usuário`

**Descrição**:

```
Permitir que usuários façam login e logout no sistema Aprenda+.

Funcionalidades:
- Login com email e senha
- Logout
- Recuperação de senha
- Controle de sessão
```

**Critérios de Aceite**:

```
✅ Usuário consegue fazer login com credenciais válidas
✅ Usuário não consegue fazer login com credenciais inválidas
✅ Usuário consegue fazer logout
✅ Sessão expira após período de inatividade
✅ Mensagens de erro são exibidas adequadamente
```

4. Salve e tire screenshot
5. Repita para outras Features

#### 2.2. Exemplos de Features Adicionais

- **Feature: Gestão de Cursos**
- **Feature: Inscrição em Cursos**
- **Feature: Dashboard do Aluno**
- **Feature: API REST de Cursos**
- **Feature: Notificações por Email**

### 3. Criar Casos de Teste (40% da nota)

#### 3.1. Criar Test Plan

1. Vá em **Test Plans**
2. Clique em **New Test Plan**
3. Nome: `Test Plan - Aprenda+ v1.0`
4. Adicione as Features criadas ao Test Plan

#### 3.2. Criar Caso de Teste para Feature de Autenticação

1. No Test Plan, clique em **New Test Case**
2. Preencha:

**Título**: `TC-001: Login com credenciais válidas`

**Sumário (Pré-requisitos)**:

```
Pré-requisitos:
1. Usuário deve estar cadastrado no sistema
2. Email: teste@aprenda.com
3. Senha: Senha123!
4. Aplicação deve estar rodando
5. Banco de dados deve estar populado
```

**Passos do Teste**:

| Ação                                        | Resultado Esperado                       |
| ------------------------------------------- | ---------------------------------------- |
| 1. Acessar URL: http://localhost:8080/login | Página de login é exibida                |
| 2. Preencher campo Email com: {{email}}     | Campo é preenchido                       |
| 3. Preencher campo Senha com: {{senha}}     | Campo é preenchido (mascarado)           |
| 4. Clicar no botão "Entrar"                 | Sistema autentica o usuário              |
| 5. Verificar redirecionamento               | Usuário é redirecionado para /dashboard  |
| 6. Verificar mensagem de boas-vindas        | Mensagem "Bem-vindo, {{nome}}" é exibida |

**Param Values** (Configurar):

- `email`: `teste@aprenda.com`
- `senha`: `Senha123!`
- `nome`: `Usuário Teste`

3. Tire screenshot da configuração
4. Salve o caso de teste

#### 3.3. Criar Mais Casos de Teste

Para cada Feature, crie pelo menos 1 caso de teste:

**Feature: Autenticação**

- TC-001: Login com credenciais válidas
- TC-002: Login com credenciais inválidas
- TC-003: Logout

**Feature: Gestão de Cursos**

- TC-004: Criar novo curso
- TC-005: Listar cursos
- TC-006: Atualizar curso
- TC-007: Excluir curso

**Feature: API REST**

- TC-008: GET /api/cursos (listar)
- TC-009: POST /api/cursos (criar)
- TC-010: PUT /api/cursos/{id} (atualizar)
- TC-011: DELETE /api/cursos/{id} (excluir)

### 4. Executar Testes (30% da nota)

#### 4.1. Executar Test Plan

1. No Test Plan, clique em **Run**
2. Para cada caso de teste:
   - Execute passo a passo
   - Marque cada passo como Passed/Failed
   - Adicione comentários se necessário
   - Anexe screenshots se houver falhas

#### 4.2. Visualizar Resultados Passo a Passo

1. Após executar um teste, clique em **More options > View Test Results**
2. Tire screenshot do painel de execução passo a passo
3. Repita para todos os testes

#### 4.3. Gerar Gráficos

1. No Test Plan, vá em **Charts**
2. Adicione gráfico **Outcome**:
   - Mostra quantos testes passaram/falharam
   - Tire screenshot
3. Adicione gráfico **Run by**:
   - Mostra quem executou os testes
   - Tire screenshot

### 5. Documentar Entrega

Crie um documento PDF com:

1. **Link do Projeto**: `https://dev.azure.com/[org]/[projeto]`
2. **Screenshots das Features**:
   - Lista de Features
   - Detalhes de cada Feature (descrição + critérios de aceite)
3. **Screenshots dos Casos de Teste**:
   - Configuração do sumário (pré-requisitos)
   - Configuração dos passos
   - Param Values
4. **Screenshots da Execução**:
   - Painel de execução passo a passo (View Test Results)
   - Gráfico de Outcome
   - Gráfico de Run by
   - Painel de resumo dos testes

## 📝 Template de Documento de Resposta

```markdown
# QA Compliance - Aprenda+

## 1. Link do Projeto

https://dev.azure.com/[org]/[projeto]/_settings/overview

## 2. Features (30%)

### 2.1. Lista de Features

[Screenshot da lista de Features]

### 2.2. Feature: Autenticação de Usuário

[Screenshot com descrição e critérios de aceite]

### 2.3. Feature: Gestão de Cursos

[Screenshot com descrição e critérios de aceite]

[... outras features ...]

## 3. Casos de Teste (40%)

### 3.1. TC-001: Login com credenciais válidas

[Screenshot do sumário com pré-requisitos]
[Screenshot dos passos do teste]
[Screenshot dos Param Values]

### 3.2. TC-002: Login com credenciais inválidas

[...]

## 4. Execução de Testes (30%)

### 4.1. Painel de Execução - TC-001

[Screenshot do View Test Results - passo a passo]

### 4.2. Gráfico de Outcome

[Screenshot do gráfico de Outcome]

### 4.3. Gráfico de Run by

[Screenshot do gráfico de Run by]

### 4.4. Resumo dos Testes

[Screenshot do painel de resumo]
```

## ✅ Checklist de Entrega

- [ ] Professor adicionado como Administrador do projeto
- [ ] Features criadas com descrição e critérios de aceite
- [ ] Screenshots das Features tirados
- [ ] Test Plan criado
- [ ] Casos de teste criados (pelo menos 1 por Feature)
- [ ] Pré-requisitos documentados no sumário
- [ ] Param Values configurados
- [ ] Screenshots dos casos de teste tirados
- [ ] Testes executados
- [ ] Screenshots dos painéis de execução tirados
- [ ] Gráficos de Outcome e Run by gerados
- [ ] Documento PDF criado com todos os screenshots
- [ ] Link do projeto incluído no documento
- [ ] Arquivo ZIP gerado com o PDF

## 🎯 Dicas

1. **Use Param Values**: Facilita a manutenção e reutilização dos testes
2. **Documente bem os pré-requisitos**: Facilita a execução dos testes
3. **Tire screenshots claros**: Garanta que textos estejam legíveis
4. **Organize o documento**: Use seções claras e numeração
5. **Teste antes de documentar**: Execute os testes para garantir que funcionam

## 📚 Recursos Adicionais

- [Azure Test Plans Documentation](https://docs.microsoft.com/azure/devops/test/)
- [Create Test Plans and Test Suites](https://docs.microsoft.com/azure/devops/test/create-a-test-plan)
- [Run Manual Tests](https://docs.microsoft.com/azure/devops/test/run-manual-tests)








