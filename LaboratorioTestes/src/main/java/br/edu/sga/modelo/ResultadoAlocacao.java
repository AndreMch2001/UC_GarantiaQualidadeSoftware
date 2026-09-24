package br.edu.sga.modelo;

/** Ações possíveis da tabela de decisão de alocação de salas (RN-05). */
public enum ResultadoAlocacao {
    ALOCADA,
    BLOQUEIO_SALA_OCUPADA,
    BLOQUEIO_CAPACIDADE,
    BLOQUEIO_TIPO_SALA
}
