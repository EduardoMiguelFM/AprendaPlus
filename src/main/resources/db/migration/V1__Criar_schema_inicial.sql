-- Flyway Migration V1: Criar schema inicial do banco de dados
-- Esta migração cria todas as tabelas do sistema Aprenda+

-- 1. Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    pontos_totais INTEGER DEFAULT 0,
    onboarding_concluido BOOLEAN DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

-- 2. Preferências do Usuário
CREATE TABLE IF NOT EXISTS preferencias_usuario (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    onboarding_concluido BOOLEAN DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    CONSTRAINT fk_preferencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS areas_interesse (
    preferencias_id BIGINT NOT NULL,
    area VARCHAR(255),
    CONSTRAINT fk_areas_interesse FOREIGN KEY (preferencias_id) REFERENCES preferencias_usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS niveis_interesse (
    preferencias_id BIGINT NOT NULL,
    nivel VARCHAR(255),
    CONSTRAINT fk_niveis_interesse FOREIGN KEY (preferencias_id) REFERENCES preferencias_usuario(id) ON DELETE CASCADE
);

-- 3. Cursos
CREATE TABLE IF NOT EXISTS cursos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT NOT NULL,
    area VARCHAR(50) NOT NULL,
    duracao VARCHAR(50) NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    icone VARCHAR(10),
    conteudo TEXT,
    instrutor VARCHAR(100),
    avaliacao DOUBLE PRECISION DEFAULT 0.0,
    total_aulas INTEGER DEFAULT 1,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

-- 4. UsuarioCurso (Inscrições)
CREATE TABLE IF NOT EXISTS usuario_curso (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    progresso INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'em_andamento',
    inscrito_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    concluido_em TIMESTAMP,
    atualizado_em TIMESTAMP,
    CONSTRAINT fk_usuario_curso_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_curso_curso FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_curso UNIQUE (usuario_id, curso_id)
);

-- 5. Desafios
CREATE TABLE IF NOT EXISTS desafios (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    area VARCHAR(50) NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    pontos INTEGER NOT NULL,
    icone VARCHAR(10),
    dificuldade VARCHAR(50),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

-- 6. Perguntas dos Desafios
CREATE TABLE IF NOT EXISTS perguntas_desafio (
    id BIGSERIAL PRIMARY KEY,
    desafio_id BIGINT NOT NULL,
    pergunta TEXT NOT NULL,
    indice_correto INTEGER NOT NULL,
    explicacao TEXT,
    CONSTRAINT fk_pergunta_desafio FOREIGN KEY (desafio_id) REFERENCES desafios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS opcoes_pergunta (
    pergunta_id BIGINT NOT NULL,
    opcao VARCHAR(500),
    CONSTRAINT fk_opcoes_pergunta FOREIGN KEY (pergunta_id) REFERENCES perguntas_desafio(id) ON DELETE CASCADE
);

-- 7. UsuarioDesafio (Histórico de Desafios)
CREATE TABLE IF NOT EXISTS usuario_desafio (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    desafio_id BIGINT NOT NULL,
    pontuacao INTEGER NOT NULL DEFAULT 0,
    total_perguntas INTEGER NOT NULL DEFAULT 0,
    tempo_gasto INTEGER,
    pontos_ganhos INTEGER DEFAULT 0,
    concluido_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_desafio_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_desafio_desafio FOREIGN KEY (desafio_id) REFERENCES desafios(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_desafio UNIQUE (usuario_id, desafio_id)
);

-- 8. Trilhas de Aprendizado
CREATE TABLE IF NOT EXISTS trilhas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    icone VARCHAR(10),
    cor VARCHAR(7),
    area VARCHAR(50) NOT NULL,
    nivel_minimo VARCHAR(20) NOT NULL,
    duracao_total VARCHAR(50),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

-- 9. TrilhaCurso (Many-to-Many)
CREATE TABLE IF NOT EXISTS trilha_curso (
    trilha_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    CONSTRAINT fk_trilha_curso_trilha FOREIGN KEY (trilha_id) REFERENCES trilhas(id) ON DELETE CASCADE,
    CONSTRAINT fk_trilha_curso_curso FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE,
    CONSTRAINT pk_trilha_curso PRIMARY KEY (trilha_id, curso_id)
);

-- 9a. TrilhaDesafio (Many-to-Many)
CREATE TABLE IF NOT EXISTS trilha_desafio (
    trilha_id BIGINT NOT NULL,
    desafio_id BIGINT NOT NULL,
    CONSTRAINT fk_trilha_desafio_trilha FOREIGN KEY (trilha_id) REFERENCES trilhas(id) ON DELETE CASCADE,
    CONSTRAINT fk_trilha_desafio_desafio FOREIGN KEY (desafio_id) REFERENCES desafios(id) ON DELETE CASCADE,
    CONSTRAINT pk_trilha_desafio PRIMARY KEY (trilha_id, desafio_id)
);

-- 10. UsuarioTrilha (Inscrições em Trilhas)
CREATE TABLE IF NOT EXISTS usuario_trilha (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    trilha_id BIGINT NOT NULL,
    inscrito_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_trilha_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_trilha_trilha FOREIGN KEY (trilha_id) REFERENCES trilhas(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_trilha UNIQUE (usuario_id, trilha_id)
);

-- 11. Troféus
CREATE TABLE IF NOT EXISTS trofeus (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    icone VARCHAR(10),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 12. UsuarioTrofeu (Conquistas)
CREATE TABLE IF NOT EXISTS usuario_trofeu (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    trofeu_id BIGINT NOT NULL,
    conquistado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_trofeu_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_trofeu_trofeu FOREIGN KEY (trofeu_id) REFERENCES trofeus(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_trofeu UNIQUE (usuario_id, trofeu_id)
);

-- 13. Transações de Pontos
CREATE TABLE IF NOT EXISTS transacoes_pontos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    pontos INTEGER NOT NULL,
    motivo VARCHAR(200),
    tipo_transacao VARCHAR(20) DEFAULT 'ganho',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transacoes_pontos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Criar índices para melhorar performance
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_cursos_area ON cursos(area);
CREATE INDEX IF NOT EXISTS idx_cursos_nivel ON cursos(nivel);
CREATE INDEX IF NOT EXISTS idx_desafios_area ON desafios(area);
CREATE INDEX IF NOT EXISTS idx_desafios_nivel ON desafios(nivel);
CREATE INDEX IF NOT EXISTS idx_desafios_tipo ON desafios(tipo);
CREATE INDEX IF NOT EXISTS idx_trilhas_area ON trilhas(area);
CREATE INDEX IF NOT EXISTS idx_transacoes_pontos_usuario ON transacoes_pontos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_transacoes_pontos_criado_em ON transacoes_pontos(criado_em DESC);


