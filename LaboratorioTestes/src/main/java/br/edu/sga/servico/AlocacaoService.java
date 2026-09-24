package br.edu.sga.servico;

import br.edu.sga.dados.AgendaSalas;
import br.edu.sga.modelo.Alocacao;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Disciplina;
import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.Sala;
import br.edu.sga.modelo.Turno;
import br.edu.sga.regras.RegraAlocacao;

/** RF-08: alocar sala para disciplina em um dia e turno, aplicando a tabela de decisão RN-05. */
public class AlocacaoService {

    private final AgendaSalas agenda;

    public AlocacaoService(AgendaSalas agenda) {
        this.agenda = agenda;
    }

    public ResultadoAlocacao alocar(Disciplina disciplina, Sala sala, DiaSemana dia, Turno turno) {
        boolean livre = !agenda.salaOcupada(sala.id(), dia, turno);
        ResultadoAlocacao resultado = RegraAlocacao.avaliar(livre, sala.capacidade(), disciplina.vagas(),
                disciplina.exigeLaboratorio(), sala.tipo());
        if (resultado == ResultadoAlocacao.ALOCADA) {
            agenda.salvar(new Alocacao(null, disciplina.id(), sala.id(), dia, turno));
        }
        return resultado;
    }
}
