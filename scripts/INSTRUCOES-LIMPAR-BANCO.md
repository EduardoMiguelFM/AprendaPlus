# Instruções para Limpar o Banco de Dados

## Opção 1: pgAdmin (Recomendado)

1. Abra o **pgAdmin**
2. Conecte ao servidor PostgreSQL
3. Expanda: **Servers** → **PostgreSQL** → **Databases** → **Aprenda**
4. Clique com botão direito em **Aprenda** → **Query Tool**
5. Copie e cole o conteúdo do arquivo `limpar-banco-completo.sql`
6. Clique em **Execute** (F5)

## Opção 2: DBeaver

1. Abra o **DBeaver**
2. Conecte ao banco **Aprenda**
3. Abra o arquivo `scripts\limpar-banco-completo.sql`
4. Execute o script (Ctrl+Enter)

## Opção 3: Via linha de comando (se tiver psql no PATH)

```powershell
# Adicionar psql ao PATH temporariamente (ajuste o caminho)
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"

# Executar o script
psql -h localhost -U postgres -d Aprenda -f scripts\limpar-banco-completo.sql
```

## SQL para Copiar e Colar

```sql
-- Script para limpar completamente o banco de dados
-- ATENÇÃO: Este script remove TODAS as tabelas e dados!

-- Desabilitar verificação de foreign keys temporariamente
SET session_replication_role = 'replica';

-- Remover todas as tabelas (em ordem para evitar problemas de foreign key)
-- Tabelas de relacionamento primeiro
DROP TABLE IF EXISTS usuario_trofeu CASCADE;
DROP TABLE IF EXISTS transacoes_pontos CASCADE;
DROP TABLE IF EXISTS usuario_trilha CASCADE;
DROP TABLE IF EXISTS trilha_curso CASCADE;
DROP TABLE IF EXISTS usuario_desafio CASCADE;
DROP TABLE IF EXISTS opcoes_pergunta CASCADE;
DROP TABLE IF EXISTS perguntas_desafio CASCADE;
DROP TABLE IF EXISTS usuario_curso CASCADE;
DROP TABLE IF EXISTS areas_interesse CASCADE;
DROP TABLE IF EXISTS niveis_interesse CASCADE;

-- Tabelas principais
DROP TABLE IF EXISTS preferencias_usuario CASCADE;
DROP TABLE IF EXISTS trofeus CASCADE;
DROP TABLE IF EXISTS trilhas CASCADE;
DROP TABLE IF EXISTS desafios CASCADE;
DROP TABLE IF EXISTS cursos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- Remover histórico do Flyway
DROP TABLE IF EXISTS flyway_schema_history CASCADE;

-- Reabilitar verificação de foreign keys
SET session_replication_role = 'origin';

-- Verificar se tudo foi removido
SELECT 'Banco de dados limpo com sucesso!' AS status;
```

## Depois de Limpar

1. Reinicie a aplicação: `./gradlew bootRun`
2. O Flyway vai executar a migração V1 e criar todas as tabelas do zero
3. Tudo deve funcionar corretamente!

