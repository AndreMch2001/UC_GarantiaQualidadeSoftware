package br.edu.sga.caixapreta;

import br.edu.sga.modelo.EventoMatricula;
import br.edu.sga.modelo.StatusMatricula;
import br.edu.sga.regras.MaquinaEstadosMatricula;
import br.edu.sga.regras.RegraException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CAIXA-PRETA — Técnica 4: TRANSIÇÃO DE ESTADOS, cobertura 0-switch
 * (um caso por transição válida) + transições proibidas. Base: RN-06.
 */
@DisplayName("Caixa-preta | Transição de estados — RN-06 matrícula")
class TransicaoEstadosMatriculaTest {

    @ParameterizedTest(name = "{0} | {1} --{2}--> {3}")
    @CsvSource({
            "CT-40, ATIVA,    TRANCAR,  TRANCADA",
            "CT-41, TRANCADA, REATIVAR, ATIVA",
            "CT-42, ATIVA,    CONCLUIR, FORMADA",
            "CT-43, ATIVA,    CANCELAR, CANCELADA",
            "CT-44, TRANCADA, CANCELAR, CANCELADA"
    })
    void transicoesValidas(String caso, StatusMatricula atual, EventoMatricula evento, StatusMatricula esperado) {
        assertEquals(esperado, MaquinaEstadosMatricula.aplicar(atual, evento));
    }

    @ParameterizedTest(name = "{0} | {1} --{2}--> proibida")
    @CsvSource({
            "CT-45, FORMADA,   CANCELAR",
            "CT-46, CANCELADA, REATIVAR",
            "CT-47, TRANCADA,  CONCLUIR",
            "CT-48, ATIVA,     REATIVAR"
    })
    void transicoesProibidas(String caso, StatusMatricula atual, EventoMatricula evento) {
        RegraException erro = assertThrows(RegraException.class,
                () -> MaquinaEstadosMatricula.aplicar(atual, evento));
        assertEquals("TRANSICAO_INVALIDA", erro.getCodigo());
    }
}
