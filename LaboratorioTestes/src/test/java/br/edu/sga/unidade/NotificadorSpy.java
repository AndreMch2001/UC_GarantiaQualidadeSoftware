package br.edu.sga.unidade;

import br.edu.sga.servico.Notificador;

import java.util.ArrayList;
import java.util.List;

/**
 * DUBLÊ do tipo SPY escrito à mão (sem framework): não envia e-mail nenhum,
 * apenas registra cada chamada para o teste conferir depois.
 * Com Mockito seria: Notificador spy = mock(Notificador.class); verify(spy).enviar(...)
 */
class NotificadorSpy implements Notificador {

    record Chamada(String destinatario, String assunto, String mensagem) { }

    final List<Chamada> chamadas = new ArrayList<>();

    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        chamadas.add(new Chamada(destinatario, assunto, mensagem));
    }
}
