package br.edu.sga.dados;

import java.sql.SQLException;

/**
 * Traduz erros técnicos do banco em códigos que o usuário e os testes entendem.
 * REGISTRO_DUPLICADO  → violou UNIQUE
 * VINCULO_EXISTENTE   → violou FOREIGN KEY (registro referenciado ou referência inexistente)
 * VALOR_FORA_DO_DOMINIO → violou CHECK
 */
public class DadosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String codigo;

    public DadosException(String codigo, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static DadosException de(SQLException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (msg.contains("UNIQUE")) {
            return new DadosException("REGISTRO_DUPLICADO", "Já existe um registro com este valor único.", e);
        }
        if (msg.contains("FOREIGN KEY")) {
            return new DadosException("VINCULO_EXISTENTE",
                    "Operação viola um vínculo entre tabelas (registro em uso ou referência inexistente).", e);
        }
        if (msg.contains("CHECK")) {
            return new DadosException("VALOR_FORA_DO_DOMINIO", "Valor fora do domínio permitido pelo banco.", e);
        }
        return new DadosException("ERRO_BANCO", "Erro de acesso ao banco: " + msg, e);
    }
}
