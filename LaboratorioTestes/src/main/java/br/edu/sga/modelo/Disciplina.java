package br.edu.sga.modelo;

public record Disciplina(Integer id, String codigo, String nome, int cargaHoraria,
                         int vagas, boolean exigeLaboratorio, int cursoId) { }
