-- Script para limpar o histórico da migração V2 falhada do Flyway
-- Execute este script se a migração V2 falhou e você quer tentar novamente

-- Remover o registro da migração V2 falhada
DELETE FROM flyway_schema_history 
WHERE version = '2' AND success = false;

-- Verificar o histórico atual
SELECT version, description, type, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;


