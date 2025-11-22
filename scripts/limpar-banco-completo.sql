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

