package br.edu.sga.caixabranca;

import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.SituacaoOrcamento;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.regras.RegraAlocacao;
import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.RegraOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CAIXA-BRANCA — complexidade ciclomática e caminhos básicos (McCabe).
 *
 * RegraAlocacao.avaliar: 3 decisões (if) → V(G) = 3 + 1 = 4 caminhos independentes.
 *   (Se cada condição atômica for contada — como fazem Checkstyle e SonarQube — o "&&"
 *    da 3ª decisão soma mais 1 e V(G) = 5. Discutir em aula por que as ferramentas divergem.)
 *
 * RegraOrcamento.situacao: 2 decisões → V(G) = 3, mais o caminho de exceção
 * herdado de percentualExecutado (orçado <= 0).
 */
@DisplayName("Caixa-branca | Caminhos básicos — avaliar() e situacao()")
class CaminhosBasicosTest {

    @Test
    @DisplayName("CB-05 | caminho 1: decisão 1 verdadeira (sala ocupada)")
    void caminho1SalaOcupada() {
        assertEquals(ResultadoAlocacao.BLOQUEIO_SALA_OCUPADA,
                RegraAlocacao.avaliar(false, 50, 30, false, TipoSala.COMUM));
    }

    @Test
    @DisplayName("CB-06 | caminho 2: decisão 2 verdadeira (capacidade)")
    void caminho2Capacidade() {
        assertEquals(ResultadoAlocacao.BLOQUEIO_CAPACIDADE,
                RegraAlocacao.avaliar(true, 20, 30, false, TipoSala.COMUM));
    }

    @Test
    @DisplayName("CB-07 | caminho 3: decisão 3 verdadeira (tipo de sala)")
    void caminho3Tipo() {
        assertEquals(ResultadoAlocacao.BLOQUEIO_TIPO_SALA,
                RegraAlocacao.avaliar(true, 50, 30, true, TipoSala.COMUM));
    }

    @Test
    @DisplayName("CB-08 | caminho 4: todas as decisões falsas (aloca)")
    void caminho4Aloca() {
        assertEquals(ResultadoAlocacao.ALOCADA,
                RegraAlocacao.avaliar(true, 50, 30, false, TipoSala.COMUM));
    }

    @Test
    @DisplayName("CB-09 | situacao(): 1º if verdadeiro → ESGOTADO")
    void situacaoEsgotado() {
        assertEquals(SituacaoOrcamento.ESGOTADO, RegraOrcamento.situacao(1_000.00, 1_000.00));
    }

    @Test
    @DisplayName("CB-10 | situacao(): 2º if verdadeiro → ALERTA")
    void situacaoAlerta() {
        assertEquals(SituacaoOrcamento.ALERTA, RegraOrcamento.situacao(1_000.00, 850.00));
    }

    @Test
    @DisplayName("CB-11 | situacao(): ambos falsos → NORMAL")
    void situacaoNormal() {
        assertEquals(SituacaoOrcamento.NORMAL, RegraOrcamento.situacao(1_000.00, 100.00));
    }

    @Test
    @DisplayName("CB-12 | percentualExecutado(): caminho de exceção (orçado = 0)")
    void caminhoExcecao() {
        RegraException erro = assertThrows(RegraException.class, () -> RegraOrcamento.situacao(0.0, 10.0));
        assertEquals("ORCAMENTO_ZERADO", erro.getCodigo());
    }
}
