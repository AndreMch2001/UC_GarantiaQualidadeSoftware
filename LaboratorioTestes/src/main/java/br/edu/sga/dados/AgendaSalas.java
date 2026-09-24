package br.edu.sga.dados;

import br.edu.sga.modelo.Alocacao;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Turno;

/**
 * Porta da agenda de salas. É uma interface para que o teste de unidade do
 * AlocacaoService possa trocar o banco por um FAKE em memória (dublê de teste)
 * — injeção de dependência, como no slide "Dublês de teste".
 */
public interface AgendaSalas {

    boolean salaOcupada(int salaId, DiaSemana dia, Turno turno);

    Alocacao salvar(Alocacao alocacao);
}
