-- SGA — Sistema de Gestão Acadêmica
-- Banco relacional SQLite. A integridade é garantida em DUAS camadas:
--   1) regras de negócio em Java (pacote br.edu.sga.regras)
--   2) restrições do próprio banco (NOT NULL, UNIQUE, CHECK, FOREIGN KEY)
-- Se a camada 1 tiver defeito, a camada 2 ainda impede dado inconsistente.

CREATE TABLE IF NOT EXISTS departamento (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo           TEXT    NOT NULL UNIQUE CHECK (length(codigo) BETWEEN 3 AND 6),
    nome             TEXT    NOT NULL,
    orcamento_anual  REAL    NOT NULL CHECK (orcamento_anual >= 0)
);

CREATE TABLE IF NOT EXISTS curso (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo             TEXT    NOT NULL UNIQUE,
    nome               TEXT    NOT NULL,
    duracao_semestres  INTEGER NOT NULL CHECK (duracao_semestres BETWEEN 1 AND 12),
    departamento_id    INTEGER NOT NULL REFERENCES departamento(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS disciplina (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo             TEXT    NOT NULL UNIQUE,
    nome               TEXT    NOT NULL,
    carga_horaria      INTEGER NOT NULL CHECK (carga_horaria BETWEEN 20 AND 120),
    vagas              INTEGER NOT NULL CHECK (vagas BETWEEN 1 AND 200),
    exige_laboratorio  INTEGER NOT NULL DEFAULT 0 CHECK (exige_laboratorio IN (0, 1)),
    curso_id           INTEGER NOT NULL REFERENCES curso(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS aluno (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    matricula  TEXT    NOT NULL UNIQUE,
    nome       TEXT    NOT NULL,
    email      TEXT    NOT NULL UNIQUE,
    idade      INTEGER NOT NULL CHECK (idade BETWEEN 16 AND 100),
    curso_id   INTEGER NOT NULL REFERENCES curso(id) ON DELETE RESTRICT,
    status     TEXT    NOT NULL DEFAULT 'ATIVA'
               CHECK (status IN ('ATIVA', 'TRANCADA', 'FORMADA', 'CANCELADA'))
);

CREATE TABLE IF NOT EXISTS sala (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo      TEXT    NOT NULL UNIQUE,
    capacidade  INTEGER NOT NULL CHECK (capacidade BETWEEN 10 AND 200),
    tipo        TEXT    NOT NULL CHECK (tipo IN ('COMUM', 'LABORATORIO'))
);

CREATE TABLE IF NOT EXISTS alocacao (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    disciplina_id  INTEGER NOT NULL REFERENCES disciplina(id) ON DELETE CASCADE,
    sala_id        INTEGER NOT NULL REFERENCES sala(id) ON DELETE RESTRICT,
    dia            TEXT    NOT NULL CHECK (dia IN ('SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SAB')),
    turno          TEXT    NOT NULL CHECK (turno IN ('MANHA', 'TARDE', 'NOITE')),
    UNIQUE (sala_id, dia, turno)
);

CREATE TABLE IF NOT EXISTS despesa (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    departamento_id  INTEGER NOT NULL REFERENCES departamento(id) ON DELETE RESTRICT,
    descricao        TEXT    NOT NULL,
    valor            REAL    NOT NULL CHECK (valor > 0),
    data             TEXT    NOT NULL
);
