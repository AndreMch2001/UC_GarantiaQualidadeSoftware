package br.edu.sga.dados;

import br.edu.sga.modelo.Disciplina;

import java.util.List;
import java.util.Optional;

public class DisciplinaRepository extends Repositorio<Disciplina> {

    private static final Mapeador<Disciplina> MAP = rs -> new Disciplina(
            rs.getInt("id"), rs.getString("codigo"), rs.getString("nome"), rs.getInt("carga_horaria"),
            rs.getInt("vagas"), rs.getInt("exige_laboratorio") == 1, rs.getInt("curso_id"));

    public DisciplinaRepository(Database db) {
        super(db);
    }

    public Disciplina salvar(Disciplina d) {
        int id = inserir("INSERT INTO disciplina (codigo, nome, carga_horaria, vagas, exige_laboratorio, curso_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                d.codigo(), d.nome(), d.cargaHoraria(), d.vagas(), d.exigeLaboratorio(), d.cursoId());
        return new Disciplina(id, d.codigo(), d.nome(), d.cargaHoraria(), d.vagas(), d.exigeLaboratorio(), d.cursoId());
    }

    public Optional<Disciplina> buscarPorId(int id) {
        return buscarUm("SELECT * FROM disciplina WHERE id = ?", MAP, id);
    }

    public List<Disciplina> listarTodos() {
        return listar("SELECT * FROM disciplina ORDER BY codigo", MAP);
    }
}
