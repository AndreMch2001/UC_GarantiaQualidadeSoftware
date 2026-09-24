package br.edu.sga.caixapreta;

import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.SituacaoOrcamento;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.regras.RegraAlocacao;
import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.RegraOrcamento;
import br.edu.sga.regras.ValidadorAluno;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CAIXA-PRETA — Técnica 2: ANÁLISE DE VALOR LIMITE, variante de 3 valores
 * (último valor antes da borda, a borda, primeiro valor depois).
 * Os defeitos se concentram nas bordas: "<" trocado por "<=", ">=" trocado por ">".
 */
@DisplayName("Caixa-preta | Análise de valor limite — RN-01, RN-03, RN-04 e RN-05")
class ValorLimiteTest {

    @ParameterizedTest(name = "{0} | idade {1} → aceita? {2}")
    @CsvSource({
            "CT-10, 15,  false",
            "CT-11, 16,  true",
            "CT-12, 17,  true",
            "CT-13, 99,  true",
            "CT-14, 100, true",
            "CT-15, 101, false"
    })
    void limitesDaIdade(String caso, int idade, boolean deveAceitar) {
        if (deveAceitar) {
            assertDoesNotThrow(() -> ValidadorAluno.validarIdade(idade));
        } else {
            RegraException erro = assertThrows(RegraException.class, () -> ValidadorAluno.validarIdade(idade));
            assertEquals("IDADE_INVALIDA", erro.getCodigo());
        }
    }

    @ParameterizedTest(name = "{0} | sala com {1} lugares, disciplina com 40 vagas → {2}")
    @CsvSource({
            "CT-16, 39, BLOQUEIO_CAPACIDADE",
            "CT-17, 40, ALOCADA",
            "CT-18, 41, ALOCADA"
    })
    void limiteDaCapacidadeDaSala(String caso, int capacidade, ResultadoAlocacao esperado) {
        // Arrange: sala livre, disciplina sem laboratório — isola a condição de capacidade
        int vagas = 40;
        // Act
        ResultadoAlocacao obtido = RegraAlocacao.avaliar(true, capacidade, vagas, false, TipoSala.COMUM);
        // Assert
        assertEquals(esperado, obtido);
    }

    @ParameterizedTest(name = "{0} | orçado 100.000, executado {1} → {2}")
    @CsvSource({
            "CT-19, 79990.00,  NORMAL",
            "CT-20, 80000.00,  ALERTA",
            "CT-21, 80010.00,  ALERTA",
            "CT-22, 99990.00,  ALERTA",
            "CT-23, 100000.00, ESGOTADO"
    })
    void limitesDaSituacaoOrcamentaria(String caso, double executado, SituacaoOrcamento esperada) {
        assertEquals(esperada, RegraOrcamento.situacao(100_000.00, executado));
    }

    @ParameterizedTest(name = "{0} | orçado 1.000, executado 900, despesa {1} → aceita? {2}")
    @CsvSource({
            "CT-24, 100.00, true,  ''",
            "CT-25, 100.01, false, SALDO_INSUFICIENTE",
            "CT-26, 0.00,   false, VALOR_INVALIDO",
            "CT-27, 0.01,   true,  ''"
    })
    void limitesDoSaldo(String caso, double valor, boolean deveAceitar, String codigoEsperado) {
        if (deveAceitar) {
            assertDoesNotThrow(() -> RegraOrcamento.validarDespesa(1_000.00, 900.00, valor));
        } else {
            RegraException erro = assertThrows(RegraException.class,
                    () -> RegraOrcamento.validarDespesa(1_000.00, 900.00, valor));
            assertEquals(codigoEsperado, erro.getCodigo());
        }
    }
}
