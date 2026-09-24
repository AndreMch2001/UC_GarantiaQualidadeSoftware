package br.edu.sga.dados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base dos repositórios. TODO comando usa PreparedStatement com parâmetros "?":
 * o valor digitado nunca é concatenado ao SQL (RNF-05 — prevenção de SQL injection).
 */
abstract class Repositorio<T> {

    @FunctionalInterface
    interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    protected final Connection conexao;

    protected Repositorio(Database db) {
        this.conexao = db.conexao();
    }

    protected int inserir(String sql, Object... parametros) {
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, parametros);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getInt(1);
            }
        } catch (SQLException e) {
            throw DadosException.de(e);
        }
    }

    protected int executar(String sql, Object... parametros) {
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw DadosException.de(e);
        }
    }

    protected List<T> listar(String sql, Mapeador<T> mapeador, Object... parametros) {
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> itens = new ArrayList<>();
                while (rs.next()) {
                    itens.add(mapeador.mapear(rs));
                }
                return itens;
            }
        } catch (SQLException e) {
            throw DadosException.de(e);
        }
    }

    protected Optional<T> buscarUm(String sql, Mapeador<T> mapeador, Object... parametros) {
        List<T> itens = listar(sql, mapeador, parametros);
        return itens.isEmpty() ? Optional.empty() : Optional.of(itens.get(0));
    }

    protected double escalar(String sql, Object... parametros) {
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            throw DadosException.de(e);
        }
    }

    private static void preencher(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object p = parametros[i];
            if (p instanceof Enum<?> e) {
                ps.setString(i + 1, e.name());
            } else if (p instanceof Boolean b) {
                ps.setInt(i + 1, b ? 1 : 0);
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }
}
