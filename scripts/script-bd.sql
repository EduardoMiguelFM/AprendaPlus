-- Script de criação do banco de dados
-- PostgreSQL Database Script para Aprenda+

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Exemplo de tabela (será substituída pelas entidades reais do projeto)
-- Esta é apenas uma estrutura base para referência

-- Tabela de usuários (exemplo)
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_ativo ON usuarios(ativo);

-- Comentários nas tabelas
COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema Aprenda+';

-- Inserir dados de exemplo (opcional)
-- INSERT INTO usuarios (nome, email, senha) VALUES 
-- ('Admin', 'admin@aprenda.com', '$2a$10$...');











