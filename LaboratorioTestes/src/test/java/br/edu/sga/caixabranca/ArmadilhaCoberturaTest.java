package br.edu.sga.caixabranca;

import br.edu.sga.modelo.TipoSala;
import br.edu.sga.regras.RegraAlocacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ANTIEXEMPLO DIDÁTICO — "a armadilha dos 100%".
 * Este teste executa as 4 saídas de avaliar() e soma cobertura de comandos e de decisões,
 * mas NÃO TEM NENHUM ASSERT: passa mesmo que a regra devolva o resultado errado.
 * Exercício: injete o defeito D-01 (troque "<" por "<=" na capacidade) e observe que este
 * teste continua verde, enquanto CT-17 fica vermelho.
 */
@DisplayName("Caixa-branca | Antiexemplo: cobertura sem verificação")
class ArmadilhaCoberturaTest {

    @Test
    @DisplayName("CB-13 | executa todos os caminhos, não verifica nada (NÃO FAÇA ISSO)")
    void cobreMasNaoVerifica() {
        RegraAlocacao.avaliar(false, 40, 40, false, TipoSala.COMUM);
        RegraAlocacao.avaliar(true, 39, 40, false, TipoSala.COMUM);
        RegraAlocacao.avaliar(true, 40, 40, true, TipoSala.COMUM);
        RegraAlocacao.avaliar(true, 40, 40, false, TipoSala.COMUM);
        // sem nenhum assert
    }
}
