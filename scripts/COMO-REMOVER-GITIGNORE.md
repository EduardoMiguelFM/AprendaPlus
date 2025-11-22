# 🔧 Como Remover Arquivos do Git que Estão no .gitignore

## ❓ Por que isso acontece?

O `.gitignore` **só funciona para arquivos que ainda não foram commitados**. Se você commitou um arquivo ANTES de adicioná-lo ao `.gitignore`, o Git continuará rastreando esse arquivo mesmo depois.

## ✅ Solução

### Opção 1: Usar o Script Automático

Execute o script PowerShell:

```powershell
.\scripts\remover-arquivos-gitignore.ps1
```

O script vai:

1. Listar os arquivos que estão no `.gitignore`
2. Remover eles do índice do Git (mas mantém no disco)
3. Você precisará fazer commit depois

### Opção 2: Fazer Manualmente

#### Passo 1: Remover do índice do Git

Execute no terminal (Git Bash, PowerShell ou CMD):

```bash
# Remover arquivos específicos do índice (mantém no disco)
git rm --cached README-JAVA.md
git rm --cached README-DEVOPS.md
git rm --cached README-QA.md
git rm --cached README-MOBILE.md
git rm --cached README-ENTREGAS-GS.md

# OU remover todos de uma vez
git rm --cached README-JAVA.md README-DEVOPS.md README-QA.md README-MOBILE.md README-ENTREGAS-GS.md
```

#### Passo 2: Verificar o status

```bash
git status
```

Você verá os arquivos listados como "deleted" (mas eles ainda estão no disco).

#### Passo 3: Fazer commit

```bash
git commit -m "chore: remover arquivos do .gitignore do índice do Git"
```

#### Passo 4: Fazer push

```bash
git push
```

## ⚠️ Importante

- `git rm --cached` **remove do índice**, mas **mantém o arquivo no disco**
- Os arquivos continuarão existindo localmente
- Eles não serão mais rastreados pelo Git
- O `.gitignore` passará a funcionar corretamente

## 🔍 Verificar se funcionou

Depois do commit e push, os arquivos não aparecerão mais no repositório remoto, mas continuarão no seu computador.

Para verificar:

```bash
git status
```

Os arquivos não devem mais aparecer como modificados.

## 📝 Nota

Se você quiser que os arquivos **não existam nem no repositório nem localmente**, use:

```bash
git rm README-JAVA.md  # Remove do índice E do disco
```

Mas isso **não é recomendado** se você quer manter os arquivos localmente.
