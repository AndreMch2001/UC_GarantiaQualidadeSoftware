package br.edu.sga.caixapreta;

import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.regras.RegraAlocacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CAIXA-PRETA — Técnica 3: TABELA DE DECISÃO. Base: RN-05.
 *
 *   Condição                         R1  R2  R3  R4  R5  R6  R7  R8
 *   C1 Sala livre?                   S   S   S   S   N   N   N   N
 *   C2 Capacidade >= vagas?          S   S   N   N   S   S   N   N
 *   C3 Tipo compatível?              S   N   S   N   S   N   S   N
 *   → Aloca                          X
 *   → Bloqueia: tipo de sala             X
 *   → Bloqueia: capacidade                   X   X
 *   → Bloqueia: sala ocupada                         X   X   X   X
 *
 * Colapsando as colunas com condição indiferente ("—"): 8 regras → 4 casos.
 */
@DisplayName("Caixa-preta | Tabela de decisão — RN-05 alocação de sala")
class TabelaDecisaoAlocacaoTest {

    @ParameterizedTest(name = "{0} | regra {1}: livre={2}, cap={3}, vagas={4}, exigeLab={5}, sala={6} → {7}")
    @CsvSource({
            "CT-30, R1,    true,  30, 30, true,  LABORATORIO, ALOCADA",
            "CT-31, R2,    true,  30, 30, true,  COMUM,       BLOQUEIO_TIPO_SALA",
            "CT-32, R3-R4, true,  20, 30, false, COMUM,       BLOQUEIO_CAPACIDADE",
            "CT-33, R5-R8, false, 30, 30, false, COMUM,       BLOQUEIO_SALA_OCUPADA",
            "CT-34, R1*,   true,  30, 30, false, LABORATORIO, ALOCADA",
            "CT-35, R8,    false, 20, 30, true,  COMUM,       BLOQUEIO_SALA_OCUPADA"
    })
    void combinacoesDeCondicoes(String caso, String regra, boolean livre, int capacidade, int vagas,
                                boolean exigeLab, TipoSala tipo, ResultadoAlocacao esperado) {
        assertEquals(esperado, RegraAlocacao.avaliar(livre, capacidade, vagas, exigeLab, tipo));
    }
}
