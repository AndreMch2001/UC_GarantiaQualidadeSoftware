package br.edu.sga.caixapreta;

import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.ValidadorAluno;
import br.edu.sga.regras.ValidadorCadastro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CAIXA-PRETA — partição + valor limite combinados nos cadastros (RF-01 a RF-04, RF-07).
 * Observe como a MESMA regra pede as duas técnicas: a partição acha as classes,
 * o valor limite escolhe o representante mais "perigoso" de cada borda.
 */
@DisplayName("Caixa-preta | Validações de cadastro — partição + valor limite")
class ValidacoesCadastroTest {

    @ParameterizedTest(name = "{0} | código de departamento \"{1}\" → aceita? {2}")
    @CsvSource({
            "CT-50, ENG,     true",
            "CT-51, EN,      false",
            "CT-52, ENGENH,  true",
            "CT-53, ENGENHA, false",
            "CT-54, eng,     false",
            "CT-55, EN1,     false"
    })
    void codigoDepartamento(String caso, String codigo, boolean aceita) {
        verificar(aceita, "CODIGO_INVALIDO", () -> ValidadorCadastro.validarCodigoDepartamento(codigo));
    }

    @ParameterizedTest(name = "{0} | carga horária {1} h → aceita? {2}")
    @CsvSource({
            "CT-56, 0,   false",
            "CT-57, 20,  true",
            "CT-58, 50,  false",
            "CT-59, 120, true",
            "CT-60, 140, false"
    })
    void cargaHoraria(String caso, int horas, boolean aceita) {
        verificar(aceita, "CARGA_HORARIA_INVALIDA", () -> ValidadorCadastro.validarCargaHoraria(horas));
    }

    @ParameterizedTest(name = "{0} | capacidade da sala {1} → aceita? {2}")
    @CsvSource({
            "CT-61, 9,   false",
            "CT-62, 10,  true",
            "CT-63, 200, true",
            "CT-64, 201, false"
    })
    void capacidadeSala(String caso, int capacidade, boolean aceita) {
        verificar(aceita, "CAPACIDADE_INVALIDA", () -> ValidadorCadastro.validarCapacidadeSala(capacidade));
    }

    @ParameterizedTest(name = "{0} | vagas {1} → aceita? {2}")
    @CsvSource({
            "CT-65, 0,   false",
            "CT-66, 1,   true",
            "CT-67, 200, true",
            "CT-68, 201, false"
    })
    void vagasDisciplina(String caso, int vagas, boolean aceita) {
        verificar(aceita, "VAGAS_INVALIDAS", () -> ValidadorCadastro.validarVagas(vagas));
    }

    @ParameterizedTest(name = "{0} | duração do curso {1} semestres → aceita? {2}")
    @CsvSource({
            "CT-69, 0,  false",
            "CT-70, 1,  true",
            "CT-71, 12, true",
            "CT-72, 13, false"
    })
    void duracaoCurso(String caso, int semestres, boolean aceita) {
        verificar(aceita, "DURACAO_INVALIDA", () -> ValidadorCadastro.validarDuracaoCurso(semestres));
    }

    @ParameterizedTest(name = "{0} | orçamento anual {1} → aceita? {2}")
    @CsvSource({
            "CT-73, -0.01, false",
            "CT-74, 0.00,  true"
    })
    void orcamentoAnual(String caso, double valor, boolean aceita) {
        verificar(aceita, "ORCAMENTO_NEGATIVO", () -> ValidadorCadastro.validarOrcamentoAnual(valor));
    }

    @ParameterizedTest(name = "{0} | código geral \"{1}\" → aceita? {2}")
    @CsvSource({
            "CT-75, GQS-01, true",
            "CT-76, g,      false",
            "CT-77, '',     false"
    })
    void codigoGeral(String caso, String codigo, boolean aceita) {
        verificar(aceita, "CODIGO_INVALIDO", () -> ValidadorCadastro.validarCodigo(codigo));
    }

    @ParameterizedTest(name = "{0} | nome do aluno com {1} caracteres → aceita? {2}")
    @CsvSource({
            "CT-78, 2,   false",
            "CT-79, 3,   true",
            "CT-80, 100, true",
            "CT-81, 101, false"
    })
    void tamanhoDoNome(String caso, int tamanho, boolean aceita) {
        String nome = "A".repeat(tamanho);
        verificar(aceita, "NOME_TAMANHO", () -> ValidadorAluno.validarNome(nome));
    }

    @Test
    @DisplayName("CT-82 | campos obrigatórios nulos ou em branco são rejeitados")
    void obrigatoriosNulos() {
        assertEquals("NOME_OBRIGATORIO", assertThrows(RegraException.class,
                () -> ValidadorAluno.validarNome("   ")).getCodigo());
        assertEquals("NOME_OBRIGATORIO", assertThrows(RegraException.class,
                () -> ValidadorAluno.validarNome(null)).getCodigo());
        assertEquals("NOME_OBRIGATORIO", assertThrows(RegraException.class,
                () -> ValidadorCadastro.validarNomeObrigatorio(null)).getCodigo());
        assertEquals("NOME_OBRIGATORIO", assertThrows(RegraException.class,
                () -> ValidadorCadastro.validarNomeObrigatorio(" ")).getCodigo());
        assertEquals("CODIGO_INVALIDO", assertThrows(RegraException.class,
                () -> ValidadorCadastro.validarCodigoDepartamento(null)).getCodigo());
        assertEquals("CODIGO_INVALIDO", assertThrows(RegraException.class,
                () -> ValidadorCadastro.validarCodigo(null)).getCodigo());
        assertEquals("EMAIL_OBRIGATORIO", assertThrows(RegraException.class,
                () -> ValidadorAluno.validarEmail(null)).getCodigo());
        assertEquals("IDADE_OBRIGATORIA", assertThrows(RegraException.class,
                () -> ValidadorAluno.converterIdade(null)).getCodigo());
    }

    private static void verificar(boolean aceita, String codigoEsperado,
                                  org.junit.jupiter.api.function.Executable acao) {
        if (aceita) {
            assertDoesNotThrow(acao);
        } else {
            assertEquals(codigoEsperado, assertThrows(RegraException.class, acao).getCodigo());
        }
    }
}
