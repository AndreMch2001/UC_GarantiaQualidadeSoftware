package br.edu.sga.unidade;

import br.edu.sga.dados.AgendaSalas;
import br.edu.sga.modelo.Alocacao;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Turno;

import java.util.ArrayList;
import java.util.List;

/** DUBLÊ do tipo FAKE: implementação simplificada e funcional da agenda, em memória. */
class AgendaSalasFake implements AgendaSalas {

    final List<Alocacao> gravadas = new ArrayList<>();

    @Override
    public boolean salaOcupada(int salaId, DiaSemana dia, Turno turno) {
        return gravadas.stream().anyMatch(a -> a.salaId() == salaId && a.dia() == dia && a.turno() == turno);
    }

    @Override
    public Alocacao salvar(Alocacao alocacao) {
        Alocacao comId = new Alocacao(gravadas.size() + 1, alocacao.disciplinaId(), alocacao.salaId(),
                alocacao.dia(), alocacao.turno());
        gravadas.add(comId);
        return comId;
    }
}
