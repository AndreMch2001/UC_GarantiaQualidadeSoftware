package br.edu.sga.dados;

import br.edu.sga.modelo.Aluno;
import br.edu.sga.modelo.StatusMatricula;

import java.util.List;
import java.util.Optional;

public class AlunoRepository extends Repositorio<Aluno> {

    private static final Mapeador<Aluno> MAP = rs -> new Aluno(
            rs.getInt("id"), rs.getString("matricula"), rs.getString("nome"), rs.getString("email"),
            rs.getInt("idade"), rs.getInt("curso_id"), StatusMatricula.valueOf(rs.getString("status")));

    public AlunoRepository(Database db) {
        super(db);
    }

    public Aluno salvar(Aluno a) {
        int id = inserir("INSERT INTO aluno (matricula, nome, email, idade, curso_id, status) VALUES (?, ?, ?, ?, ?, ?)",
                a.matricula(), a.nome(), a.email(), a.idade(), a.cursoId(), a.status());
        return new Aluno(id, a.matricula(), a.nome(), a.email(), a.idade(), a.cursoId(), a.status());
    }

    /** Próximo número sequencial para compor a matrícula (determinístico, sem random()). */
    public int proximoSequencial() {
        return (int) escalar("SELECT COALESCE(MAX(id), 0) + 1 FROM aluno");
    }

    public Optional<Aluno> buscarPorId(int id) {
        return buscarUm("SELECT * FROM aluno WHERE id = ?", MAP, id);
    }

    public List<Aluno> listarTodos() {
        return listar("SELECT * FROM aluno ORDER BY nome", MAP);
    }

    public List<Aluno> buscarPorNome(String trecho) {
        return listar("SELECT * FROM aluno WHERE nome LIKE ? ORDER BY nome", MAP, "%" + trecho + "%");
    }

    public void atualizarStatus(int id, StatusMatricula status) {
        executar("UPDATE aluno SET status = ? WHERE id = ?", status, id);
    }
}
