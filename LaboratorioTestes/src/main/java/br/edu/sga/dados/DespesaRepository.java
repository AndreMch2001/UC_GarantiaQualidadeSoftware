package br.edu.sga.dados;

import br.edu.sga.modelo.Despesa;

import java.util.List;

public class DespesaRepository extends Repositorio<Despesa> {

    private static final Mapeador<Despesa> MAP = rs -> new Despesa(
            rs.getInt("id"), rs.getInt("departamento_id"), rs.getString("descricao"),
            rs.getDouble("valor"), rs.getString("data"));

    public DespesaRepository(Database db) {
        super(db);
    }

    public Despesa salvar(Despesa d) {
        int id = inserir("INSERT INTO despesa (departamento_id, descricao, valor, data) VALUES (?, ?, ?, ?)",
                d.departamentoId(), d.descricao(), d.valor(), d.data());
        return new Despesa(id, d.departamentoId(), d.descricao(), d.valor(), d.data());
    }

    public double totalExecutado(int departamentoId) {
        return escalar("SELECT COALESCE(SUM(valor), 0) FROM despesa WHERE departamento_id = ?", departamentoId);
    }

    public List<Despesa> listarPorDepartamento(int departamentoId) {
        return listar("SELECT * FROM despesa WHERE departamento_id = ? ORDER BY data, id", MAP, departamentoId);
    }
}
