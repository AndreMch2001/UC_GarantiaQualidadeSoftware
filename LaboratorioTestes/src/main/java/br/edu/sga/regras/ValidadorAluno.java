package br.edu.sga.regras;

import java.util.regex.Pattern;

/**
 * Regras de cadastro do aluno.
 * RN-01: idade entre 16 e 100 anos (inteiro).
 * RN-02: e-mail com usuário, "@" e domínio contendo ponto.
 */
public final class ValidadorAluno {

    public static final int IDADE_MINIMA = 16;
    public static final int IDADE_MAXIMA = 100;

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private ValidadorAluno() { }

    /** Converte o texto digitado no formulário em idade (classe CE-4: vazio / não numérico). */
    public static int converterIdade(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RegraException("IDADE_OBRIGATORIA", "Preencha a idade do aluno.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new RegraException("IDADE_NAO_NUMERICA", "Digite a idade usando apenas números inteiros.");
        }
    }

    public static void validarIdade(int idade) {
        if (idade < IDADE_MINIMA || idade > IDADE_MAXIMA) {
            throw new RegraException("IDADE_INVALIDA",
                    "A idade deve estar entre " + IDADE_MINIMA + " e " + IDADE_MAXIMA + " anos.");
        }
    }

    public static void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraException("EMAIL_OBRIGATORIO", "Preencha o e-mail do aluno.");
        }
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new RegraException("EMAIL_INVALIDO", "Informe um e-mail no formato nome@dominio.com.");
        }
    }

    public static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraException("NOME_OBRIGATORIO", "Preencha o nome do aluno.");
        }
        int tamanho = nome.trim().length();
        if (tamanho < 3 || tamanho > 100) {
            throw new RegraException("NOME_TAMANHO", "O nome deve ter entre 3 e 100 caracteres.");
        }
    }
}
