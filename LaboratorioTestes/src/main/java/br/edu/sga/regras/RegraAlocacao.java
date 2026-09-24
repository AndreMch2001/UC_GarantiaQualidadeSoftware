package br.edu.sga.regras;

import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.TipoSala;

/**
 * RN-05 — Alocação de sala (tabela de decisão).
 * C1: a sala está livre no dia/turno?
 * C2: a capacidade da sala é maior ou igual às vagas da disciplina?
 * C3: o tipo da sala é compatível (disciplina que exige laboratório só vai para LABORATORIO)?
 * A ordem das verificações define a prioridade da mensagem de bloqueio.
 */
public final class RegraAlocacao {

    private RegraAlocacao() { }

    public static ResultadoAlocacao avaliar(boolean salaLivre, int capacidadeSala, int vagasDisciplina,
                                            boolean exigeLaboratorio, TipoSala tipoSala) {
        if (!salaLivre) {
            return ResultadoAlocacao.BLOQUEIO_SALA_OCUPADA;
        }
        if (capacidadeSala < vagasDisciplina) {
            return ResultadoAlocacao.BLOQUEIO_CAPACIDADE;
        }
        if (exigeLaboratorio && tipoSala != TipoSala.LABORATORIO) {
            return ResultadoAlocacao.BLOQUEIO_TIPO_SALA;
        }
        return ResultadoAlocacao.ALOCADA;
    }
}
