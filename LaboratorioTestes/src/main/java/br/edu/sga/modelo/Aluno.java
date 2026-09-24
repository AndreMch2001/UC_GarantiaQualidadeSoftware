package br.edu.sga.modelo;

public record Aluno(Integer id, String matricula, String nome, String email, int idade,
                    int cursoId, StatusMatricula status) {

    public Aluno comStatus(StatusMatricula novo) {
        return new Aluno(id, matricula, nome, email, idade, cursoId, novo);
    }
}
