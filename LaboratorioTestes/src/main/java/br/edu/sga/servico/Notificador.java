package br.edu.sga.servico;

/** Envio de avisos (e-mail). Em teste é substituído por um SPY que só registra as chamadas. */
public interface Notificador {
    void enviar(String destinatario, String assunto, String mensagem);
}
