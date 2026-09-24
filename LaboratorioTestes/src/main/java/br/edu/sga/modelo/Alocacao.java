package br.edu.sga.modelo;

public record Alocacao(Integer id, int disciplinaId, int salaId, DiaSemana dia, Turno turno) { }
