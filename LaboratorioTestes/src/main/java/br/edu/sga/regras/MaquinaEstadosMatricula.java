package br.edu.sga.regras;

import br.edu.sga.modelo.EventoMatricula;
import br.edu.sga.modelo.StatusMatricula;

import java.util.EnumMap;
import java.util.Map;

/**
 * RN-06 — Ciclo de vida da matrícula.
 * <pre>
 *   ATIVA    --TRANCAR-->  TRANCADA
 *   TRANCADA --REATIVAR--> ATIVA
 *   ATIVA    --CONCLUIR--> FORMADA    (estado final)
 *   ATIVA    --CANCELAR--> CANCELADA  (estado final)
 *   TRANCADA --CANCELAR--> CANCELADA
 * </pre>
 * Qualquer par (estado, evento) fora da tabela é transição proibida.
 */
public final class MaquinaEstadosMatricula {

    private static final Map<StatusMatricula, Map<EventoMatricula, StatusMatricula>> TRANSICOES =
            new EnumMap<>(StatusMatricula.class);

    static {
        TRANSICOES.put(StatusMatricula.ATIVA, Map.of(
                EventoMatricula.TRANCAR, StatusMatricula.TRANCADA,
                EventoMatricula.CONCLUIR, StatusMatricula.FORMADA,
                EventoMatricula.CANCELAR, StatusMatricula.CANCELADA));
        TRANSICOES.put(StatusMatricula.TRANCADA, Map.of(
                EventoMatricula.REATIVAR, StatusMatricula.ATIVA,
                EventoMatricula.CANCELAR, StatusMatricula.CANCELADA));
    }

    private MaquinaEstadosMatricula() { }

    public static StatusMatricula aplicar(StatusMatricula atual, EventoMatricula evento) {
        StatusMatricula proximo = TRANSICOES.getOrDefault(atual, Map.of()).get(evento);
        if (proximo == null) {
            throw new RegraException("TRANSICAO_INVALIDA",
                    "Não é possível aplicar " + evento + " a uma matrícula " + atual + ".");
        }
        return proximo;
    }
}
