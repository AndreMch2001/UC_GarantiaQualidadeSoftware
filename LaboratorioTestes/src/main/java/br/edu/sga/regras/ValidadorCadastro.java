package br.edu.sga.regras;

import java.util.regex.Pattern;

/** Regras de cadastro de departamento, curso, disciplina e sala (RF-01, RF-02, RF-03, RF-07). */
public final class ValidadorCadastro {

    private static final Pattern CODIGO_DEPARTAMENTO = Pattern.compile("^[A-Z]{3,6}$");
    private static final Pattern CODIGO_GERAL = Pattern.compile("^[A-Z0-9-]{2,12}$");

    private ValidadorCadastro() { }

    public static void validarCodigoDepartamento(String codigo) {
        if (codigo == null || !CODIGO_DEPARTAMENTO.matcher(codigo).matches()) {
            throw new RegraException("CODIGO_INVALIDO",
                    "O código do departamento deve ter de 3 a 6 letras maiúsculas (ex.: ENG).");
        }
    }

    public static void validarCodigo(String codigo) {
        if (codigo == null || !CODIGO_GERAL.matcher(codigo).matches()) {
            throw new RegraException("CODIGO_INVALIDO",
                    "Use de 2 a 12 caracteres: letras maiúsculas, números ou hífen (ex.: GQS-01).");
        }
    }

    public static void validarNomeObrigatorio(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraException("NOME_OBRIGATORIO", "Preencha o nome.");
        }
    }

    public static void validarOrcamentoAnual(double valor) {
        if (valor < 0) {
            throw new RegraException("ORCAMENTO_NEGATIVO", "O orçamento anual não pode ser negativo.");
        }
    }

    public static void validarDuracaoCurso(int semestres) {
        if (semestres < 1 || semestres > 12) {
            throw new RegraException("DURACAO_INVALIDA", "A duração do curso deve ser de 1 a 12 semestres.");
        }
    }

    /** Carga horária: de 20 a 120 horas, sempre múltipla de 20. */
    public static void validarCargaHoraria(int horas) {
        if (horas < 20 || horas > 120 || horas % 20 != 0) {
            throw new RegraException("CARGA_HORARIA_INVALIDA",
                    "A carga horária deve ser 20, 40, 60, 80, 100 ou 120 horas.");
        }
    }

    public static void validarVagas(int vagas) {
        if (vagas < 1 || vagas > 200) {
            throw new RegraException("VAGAS_INVALIDAS", "O número de vagas deve ser de 1 a 200.");
        }
    }

    public static void validarCapacidadeSala(int capacidade) {
        if (capacidade < 10 || capacidade > 200) {
            throw new RegraException("CAPACIDADE_INVALIDA", "A capacidade da sala deve ser de 10 a 200 lugares.");
        }
    }
}
