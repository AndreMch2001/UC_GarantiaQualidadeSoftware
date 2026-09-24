package br.edu.sga.servico;

import br.edu.sga.dados.AlunoRepository;
import br.edu.sga.dados.CursoRepository;
import br.edu.sga.modelo.Aluno;
import br.edu.sga.modelo.EventoMatricula;
import br.edu.sga.modelo.StatusMatricula;
import br.edu.sga.regras.MaquinaEstadosMatricula;
import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.ValidadorAluno;

import java.time.Clock;
import java.time.LocalDate;

/** RF-04 (cadastrar aluno) e RF-10 (alterar situação da matrícula). */
public class MatriculaService {

    private final AlunoRepository alunos;
    private final CursoRepository cursos;
    private final Clock relogio;

    /**
     * O relógio é injetado: em produção Clock.systemDefaultZone(); no teste Clock.fixed(...).
     * Sem isso a matrícula dependeria de LocalDate.now() e o teste deixaria de ser repetível.
     */
    public MatriculaService(AlunoRepository alunos, CursoRepository cursos, Clock relogio) {
        this.alunos = alunos;
        this.cursos = cursos;
        this.relogio = relogio;
    }

    public Aluno cadastrar(String nome, String email, String idadeDigitada, int cursoId) {
        ValidadorAluno.validarNome(nome);
        ValidadorAluno.validarEmail(email);
        int idade = ValidadorAluno.converterIdade(idadeDigitada);
        ValidadorAluno.validarIdade(idade);
        cursos.buscarPorId(cursoId).orElseThrow(() ->
                new RegraException("CURSO_INEXISTENTE", "Selecione um curso cadastrado."));

        String matricula = String.format("%d%04d", LocalDate.now(relogio).getYear(), alunos.proximoSequencial());
        return alunos.salvar(new Aluno(null, matricula, nome.trim(), email.trim().toLowerCase(), idade,
                cursoId, StatusMatricula.ATIVA));
    }

    public Aluno alterarStatus(int alunoId, EventoMatricula evento) {
        Aluno aluno = alunos.buscarPorId(alunoId).orElseThrow(() ->
                new RegraException("ALUNO_INEXISTENTE", "Aluno não encontrado."));
        StatusMatricula novo = MaquinaEstadosMatricula.aplicar(aluno.status(), evento);
        alunos.atualizarStatus(alunoId, novo);
        return aluno.comStatus(novo);
    }
}
