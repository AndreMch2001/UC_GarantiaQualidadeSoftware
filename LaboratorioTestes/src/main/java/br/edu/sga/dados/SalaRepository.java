package br.edu.sga.dados;

import br.edu.sga.modelo.Sala;
import br.edu.sga.modelo.TipoSala;

import java.util.List;
import java.util.Optional;

public class SalaRepository extends Repositorio<Sala> {

    private static final Mapeador<Sala> MAP = rs -> new Sala(
            rs.getInt("id"), rs.getString("codigo"), rs.getInt("capacidade"), TipoSala.valueOf(rs.getString("tipo")));

    public SalaRepository(Database db) {
        super(db);
    }

    public Sala salvar(Sala s) {
        int id = inserir("INSERT INTO sala (codigo, capacidade, tipo) VALUES (?, ?, ?)",
                s.codigo(), s.capacidade(), s.tipo());
        return new Sala(id, s.codigo(), s.capacidade(), s.tipo());
    }

    public Optional<Sala> buscarPorId(int id) {
        return buscarUm("SELECT * FROM sala WHERE id = ?", MAP, id);
    }

    public List<Sala> listarTodos() {
        return listar("SELECT * FROM sala ORDER BY codigo", MAP);
    }
}
