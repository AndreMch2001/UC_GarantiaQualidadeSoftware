package br.edu.sga.caixapreta;

import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.ValidadorAluno;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CAIXA-PRETA — Técnica 1: PARTIÇÃO DE EQUIVALÊNCIA (ISO/IEC/IEEE 29119-4).
 * Base de teste: RN-01 (idade 16..100) e RN-02 (formato do e-mail).
 * Um representante por classe; classes inválidas testadas uma por caso (evita mascaramento).
 */
@DisplayName("Caixa-preta | Partição de equivalência — RN-01 e RN-02")
class ParticaoEquivalenciaTest {

    /** Simula o que o formulário faz: converte o texto e depois valida a faixa. */
    private static void validarIdadeDigitada(String texto) {
        ValidadorAluno.validarIdade(ValidadorAluno.converterIdade(texto));
    }

    @Test
    @DisplayName("CT-02 | CE-2 idade válida (35) é aceita")
    void idadeValidaEhAceita() {
        // Arrange
        String idade = "35";
        // Act + Assert
        assertDoesNotThrow(() -> validarIdadeDigitada(idade));
    }

    @ParameterizedTest(name = "{0} | {1} idade=\"{2}\" rejeitada com {3}")
    @CsvSource({
            "CT-01, CE-1 abaixo de 16,   12,    IDADE_INVALIDA",
            "CT-03, CE-3 acima de 100,   130,   IDADE_INVALIDA",
            "CT-04, CE-4 campo vazio,    '',    IDADE_OBRIGATORIA",
            "CT-05, CE-5 não numérico,   vinte, IDADE_NAO_NUMERICA"
    })
    void idadeInvalidaEhRejeitada(String caso, String classe, String idade, String codigoEsperado) {
        RegraException erro = assertThrows(RegraException.class, () -> validarIdadeDigitada(idade));
        assertEquals(codigoEsperado, erro.getCodigo());
    }

    @Test
    @DisplayName("CT-06 | CE-6 e-mail válido é aceito")
    void emailValidoEhAceito() {
        assertDoesNotThrow(() -> ValidadorAluno.validarEmail("ana.souza@aluno.univale.edu.br"));
    }

    @ParameterizedTest(name = "{0} | {1} email=\"{2}\" rejeitado com {3}")
    @CsvSource({
            "CT-07, CE-7 sem arroba,          ana.univale.edu.br, EMAIL_INVALIDO",
            "CT-08, CE-8 domínio sem ponto,   ana@univale,        EMAIL_INVALIDO",
            "CT-09, CE-9 vazio,               '',                 EMAIL_OBRIGATORIO"
    })
    void emailInvalidoEhRejeitado(String caso, String classe, String email, String codigoEsperado) {
        RegraException erro = assertThrows(RegraException.class, () -> ValidadorAluno.validarEmail(email));
        assertEquals(codigoEsperado, erro.getCodigo());
    }
}
