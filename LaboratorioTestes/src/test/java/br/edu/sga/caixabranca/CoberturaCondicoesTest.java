package br.edu.sga.caixabranca;

import br.edu.sga.regras.RegraOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CAIXA-BRANCA — cobertura de comandos, decisões e condições (MC/DC).
 * Código sob análise (RegraOrcamento.exigeAprovacaoDiretoria):
 *
 *   L1  public static boolean exigeAprovacaoDiretoria(double valor, double percentualApos) {
 *   L2      if (valor > LIMITE_DIRETORIA_VALOR || percentualApos >= LIMITE_DIRETORIA_PERCENTUAL) {
 *   L3          return true;
 *   L4      }
 *   L5      return false;
 *   L6  }
 *
 * Só CB-01            → comandos 3/4 (L5 nunca roda), decisão só "verdadeira".
 *                        Curto-circuito: com A verdadeira, B nem é avaliada.
 * CB-01 + CB-02       → 100% comandos e 100% decisões, mas JaCoCo mostra "1 of 4 branches missed"
 *                        (B nunca foi verdadeira) … e o defeito "|| trocado por &&" SOBREVIVE.
 * + CB-03             → nenhum percentual muda, mas o defeito é detectado: quem verifica é o assert.
 * + CB-04             → B verdadeira: 4/4 desvios.
 * CB-02, CB-03, CB-04 → conjunto MC/DC mínimo (n + 1 = 3 casos para 2 condições).
 */
@DisplayName("Caixa-branca | Cobertura de condições — exigeAprovacaoDiretoria")
class CoberturaCondicoesTest {

    @Test
    @DisplayName("CB-01 | T1 valor 15.000 e 95% → exige (A=V, B=V)")
    void t1AmbasVerdadeiras() {
        assertTrue(RegraOrcamento.exigeAprovacaoDiretoria(15_000.00, 95.0));
    }

    @Test
    @DisplayName("CB-02 | T2 valor 500 e 10% → não exige (A=F, B=F)")
    void t2AmbasFalsas() {
        assertFalse(RegraOrcamento.exigeAprovacaoDiretoria(500.00, 10.0));
    }

    @Test
    @DisplayName("CB-03 | T3 valor 15.000 e 10% → exige (A=V, B=F) — só A decide")
    void t3SomenteValorAlto() {
        assertTrue(RegraOrcamento.exigeAprovacaoDiretoria(15_000.00, 10.0));
    }

    @Test
    @DisplayName("CB-04 | T4 valor 500 e 95% → exige (A=F, B=V) — só B decide")
    void t4SomentePercentualAlto() {
        assertTrue(RegraOrcamento.exigeAprovacaoDiretoria(500.00, 95.0));
    }
}
