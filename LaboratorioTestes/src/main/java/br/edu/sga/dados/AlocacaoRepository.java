package br.edu.sga.dados;

import br.edu.sga.modelo.Alocacao;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Turno;

import java.util.List;

public class AlocacaoRepository extends Repositorio<Alocacao> implements AgendaSalas {

    private static final Mapeador<Alocacao> MAP = rs -> new Alocacao(
            rs.getInt("id"), rs.getInt("disciplina_id"), rs.getInt("sala_id"),
            DiaSemana.valueOf(rs.getString("dia")), Turno.valueOf(rs.getString("turno")));

    public AlocacaoRepository(Database db) {
        super(db);
    }

    @Override
    public Alocacao salvar(Alocacao a) {
        int id = inserir("INSERT INTO alocacao (disciplina_id, sala_id, dia, turno) VALUES (?, ?, ?, ?)",
                a.disciplinaId(), a.salaId(), a.dia(), a.turno());
        return new Alocacao(id, a.disciplinaId(), a.salaId(), a.dia(), a.turno());
    }

    @Override
    public boolean salaOcupada(int salaId, DiaSemana dia, Turno turno) {
        return escalar("SELECT COUNT(*) FROM alocacao WHERE sala_id = ? AND dia = ? AND turno = ?",
                salaId, dia, turno) > 0;
    }

    public List<Alocacao> listarTodas() {
        return listar("SELECT * FROM alocacao ORDER BY dia, turno", MAP);
    }

    public int contar() {
        return (int) escalar("SELECT COUNT(*) FROM alocacao");
    }
}
