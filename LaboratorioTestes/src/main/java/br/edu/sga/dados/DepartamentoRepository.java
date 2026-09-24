package br.edu.sga.dados;

import br.edu.sga.modelo.Departamento;

import java.util.List;
import java.util.Optional;

public class DepartamentoRepository extends Repositorio<Departamento> {

    private static final Mapeador<Departamento> MAP = rs -> new Departamento(
            rs.getInt("id"), rs.getString("codigo"), rs.getString("nome"), rs.getDouble("orcamento_anual"));

    public DepartamentoRepository(Database db) {
        super(db);
    }

    public Departamento salvar(Departamento d) {
        int id = inserir("INSERT INTO departamento (codigo, nome, orcamento_anual) VALUES (?, ?, ?)",
                d.codigo(), d.nome(), d.orcamentoAnual());
        return new Departamento(id, d.codigo(), d.nome(), d.orcamentoAnual());
    }

    public Optional<Departamento> buscarPorId(int id) {
        return buscarUm("SELECT * FROM departamento WHERE id = ?", MAP, id);
    }

    public Optional<Departamento> buscarPorCodigo(String codigo) {
        return buscarUm("SELECT * FROM departamento WHERE codigo = ?", MAP, codigo);
    }

    public List<Departamento> listarTodos() {
        return listar("SELECT * FROM departamento ORDER BY codigo", MAP);
    }

    public void atualizarOrcamento(int id, double novoOrcamento) {
        executar("UPDATE departamento SET orcamento_anual = ? WHERE id = ?", novoOrcamento, id);
    }

    public void excluir(int id) {
        executar("DELETE FROM departamento WHERE id = ?", id);
    }
}
