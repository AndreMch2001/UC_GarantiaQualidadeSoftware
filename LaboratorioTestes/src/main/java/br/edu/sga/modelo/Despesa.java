package br.edu.sga.modelo;

public record Despesa(Integer id, int departamentoId, String descricao, double valor, String data) { }
