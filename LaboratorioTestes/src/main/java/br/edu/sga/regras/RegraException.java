package br.edu.sga.regras;

/**
 * Violação de regra de negócio. O {@code codigo} é estável e é o que os testes verificam;
 * a mensagem é para o usuário e diz o que fazer (RNF-04 — usabilidade).
 */
public class RegraException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String codigo;

    public RegraException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
