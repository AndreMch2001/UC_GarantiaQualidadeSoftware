package br.edu.sga.dados;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexão com o SQLite e criação do esquema a partir de schema.sql.
 * Em testes usamos {@link #emMemoria()}: o banco nasce vazio e some ao fechar (princípio Isolated do FIRST).
 */
public final class Database implements AutoCloseable {

    private final Connection conexao;

    private Database(String url) {
        try {
            this.conexao = DriverManager.getConnection(url);
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON"); // no SQLite a FK vem desligada por padrão!
            }
            criarTabelas();
        } catch (SQLException e) {
            throw DadosException.de(e);
        }
    }

    public static Database emMemoria() {
        return new Database("jdbc:sqlite::memory:");
    }

    public static Database arquivo(String caminho) {
        return new Database("jdbc:sqlite:" + caminho);
    }

    public Connection conexao() {
        return conexao;
    }

    private void criarTabelas() throws SQLException {
        String sql = lerRecurso("/schema.sql");
        try (Statement st = conexao.createStatement()) {
            for (String comando : sql.split(";")) {
                String limpo = removerComentarios(comando).trim();
                if (!limpo.isEmpty()) {
                    st.execute(limpo);
                }
            }
        }
    }

    private static String removerComentarios(String bloco) {
        StringBuilder sb = new StringBuilder();
        for (String linha : bloco.split("\n")) {
            if (!linha.trim().startsWith("--")) {
                sb.append(linha).append('\n');
            }
        }
        return sb.toString();
    }

    private static String lerRecurso(String caminho) {
        try (InputStream in = Database.class.getResourceAsStream(caminho)) {
            if (in == null) {
                throw new IllegalStateException("Recurso não encontrado: " + caminho);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {
        try {
            conexao.close();
        } catch (SQLException e) {
            throw DadosException.de(e);
        }
    }
}
