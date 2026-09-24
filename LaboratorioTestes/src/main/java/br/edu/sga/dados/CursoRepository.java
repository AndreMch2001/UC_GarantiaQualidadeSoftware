package br.edu.sga.dados;

import br.edu.sga.modelo.Curso;

import java.util.List;
import java.util.Optional;

public class CursoRepository extends Repositorio<Curso> {

    private static final Mapeador<Curso> MAP = rs -> new Curso(
            rs.getInt("id"), rs.getString("codigo"), rs.getString("nome"),
            rs.getInt("duracao_semestres"), rs.getInt("departamento_id"));

    public CursoRepository(Database db) {
        super(db);
    }

    public Curso salvar(Curso c) {
        int id = inserir("INSERT INTO curso (codigo, nome, duracao_semestres, departamento_id) VALUES (?, ?, ?, ?)",
                c.codigo(), c.nome(), c.duracaoSemestres(), c.departamentoId());
        return new Curso(id, c.codigo(), c.nome(), c.duracaoSemestres(), c.departamentoId());
    }

    public Optional<Curso> buscarPorId(int id) {
        return buscarUm("SELECT * FROM curso WHERE id = ?", MAP, id);
    }

    public List<Curso> listarTodos() {
        return listar("SELECT * FROM curso ORDER BY codigo", MAP);
    }

    public List<Curso> listarPorDepartamento(int departamentoId) {
        return listar("SELECT * FROM curso WHERE departamento_id = ? ORDER BY codigo", MAP, departamentoId);
    }
}
