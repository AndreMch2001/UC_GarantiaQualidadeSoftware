package br.edu.sga.servico;

/** Implementação de produção simplificada: imprime o aviso no console. */
public class NotificadorConsole implements Notificador {
    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        System.out.printf("[e-mail para %s] %s — %s%n", destinatario, assunto, mensagem);
    }
}
