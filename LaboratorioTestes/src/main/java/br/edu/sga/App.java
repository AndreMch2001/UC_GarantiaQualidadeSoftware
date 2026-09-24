package br.edu.sga;

import br.edu.sga.dados.AlocacaoRepository;
import br.edu.sga.dados.AlunoRepository;
import br.edu.sga.dados.CursoRepository;
import br.edu.sga.dados.Database;
import br.edu.sga.dados.DepartamentoRepository;
import br.edu.sga.dados.DespesaRepository;
import br.edu.sga.dados.DisciplinaRepository;
import br.edu.sga.dados.SalaRepository;
import br.edu.sga.modelo.Aluno;
import br.edu.sga.modelo.Curso;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Departamento;
import br.edu.sga.modelo.Disciplina;
import br.edu.sga.modelo.EventoMatricula;
import br.edu.sga.modelo.ResultadoDespesa;
import br.edu.sga.modelo.Sala;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.modelo.Turno;
import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.RegraOrcamento;
import br.edu.sga.servico.AlocacaoService;
import br.edu.sga.servico.CadastroService;
import br.edu.sga.servico.MatriculaService;
import br.edu.sga.servico.NotificadorConsole;
import br.edu.sga.servico.OrcamentoService;

import java.io.File;
import java.time.Clock;

/**
 * Demonstração de ponta a ponta: cria (ou reabre) o arquivo sga.db, cadastra dados de exemplo,
 * provoca alguns bloqueios de regra de negócio e imprime os relatórios.
 * Execução: mvn -q compile exec:java
 */
public class App {

    public static void main(String[] args) {
        boolean novo = !new File("sga.db").exists();
        try (Database db = Database.arquivo("sga.db")) {
            var departamentos = new DepartamentoRepository(db);
            var cursos = new CursoRepository(db);
            var disciplinas = new DisciplinaRepository(db);
            var alunos = new AlunoRepository(db);
            var salas = new SalaRepository(db);
            var alocacoes = new AlocacaoRepository(db);
            var despesas = new DespesaRepository(db);

            Clock relogio = Clock.systemDefaultZone();
            var cadastro = new CadastroService(departamentos, cursos, disciplinas, salas);
            var matricula = new MatriculaService(alunos, cursos, relogio);
            var orcamento = new OrcamentoService(departamentos, despesas, new NotificadorConsole(), relogio);
            var alocacao = new AlocacaoService(alocacoes);

            if (novo) {
                System.out.println("== Criando dados de exemplo em sga.db ==");
                Departamento eng = cadastro.criarDepartamento("ENG", "Engenharias", 100_000.00);
                Departamento cmp = cadastro.criarDepartamento("COMP", "Computação", 80_000.00);
                Curso es = cadastro.criarCurso("ES", "Engenharia de Software", 8, cmp.id());
                Curso em = cadastro.criarCurso("EM", "Engenharia Mecânica", 10, eng.id());
                Disciplina gqs = cadastro.criarDisciplina("GQS", "Garantia da Qualidade de Software", 80, 40, false, es.id());
                Disciplina bd = cadastro.criarDisciplina("BD1", "Banco de Dados I", 80, 30, true, es.id());
                cadastro.criarDisciplina("TERMO", "Termodinâmica", 60, 50, false, em.id());
                Sala s101 = cadastro.criarSala("S-101", 40, TipoSala.COMUM);
                Sala lab1 = cadastro.criarSala("LAB-1", 30, TipoSala.LABORATORIO);
                cadastro.criarSala("AUD-1", 120, TipoSala.COMUM);

                Aluno ana = matricula.cadastrar("Ana Souza", "ana@aluno.univale.edu.br", "19", es.id());
                matricula.cadastrar("Bruno Lima", "bruno@aluno.univale.edu.br", "23", es.id());
                matricula.cadastrar("Carla Dias", "carla@aluno.univale.edu.br", "31", em.id());
                matricula.alterarStatus(ana.id(), EventoMatricula.TRANCAR);

                System.out.println("GQS em S-101 (SEG/NOITE): " + alocacao.alocar(gqs, s101, DiaSemana.SEG, Turno.NOITE));
                System.out.println("BD1 em S-101 (SEG/NOITE): " + alocacao.alocar(bd, s101, DiaSemana.SEG, Turno.NOITE));
                System.out.println("BD1 em S-101 (TER/NOITE): " + alocacao.alocar(bd, s101, DiaSemana.TER, Turno.NOITE));
                System.out.println("BD1 em LAB-1 (TER/NOITE): " + alocacao.alocar(bd, lab1, DiaSemana.TER, Turno.NOITE));

                ResultadoDespesa r = orcamento.registrarDespesa(cmp.id(), "Licenças de software", 65_000.00);
                System.out.println("Despesa COMP: " + r.situacao() + " (" + r.percentualExecutado() + "%)");
                try {
                    orcamento.registrarDespesa(cmp.id(), "Servidores", 20_000.00);
                } catch (RegraException e) {
                    System.out.println("Bloqueado [" + e.getCodigo() + "]: " + e.getMessage());
                }
            }

            System.out.println();
            System.out.println("== Departamentos e orçamento ==");
            for (Departamento d : departamentos.listarTodos()) {
                double executado = despesas.totalExecutado(d.id());
                System.out.printf("%-5s %-20s orçado %12.2f  executado %12.2f  %6.2f%%  %s%n",
                        d.codigo(), d.nome(), d.orcamentoAnual(), executado,
                        RegraOrcamento.percentualExecutado(d.orcamentoAnual(), executado),
                        RegraOrcamento.situacao(d.orcamentoAnual(), executado));
            }
            System.out.println();
            System.out.println("== Alunos ==");
            alunos.listarTodos().forEach(a ->
                    System.out.printf("%s  %-15s %-30s %s%n", a.matricula(), a.nome(), a.email(), a.status()));
            System.out.println();
            System.out.println("== Distribuição de salas ==");
            alocacoes.listarTodas().forEach(a -> System.out.printf("%s/%-5s sala %s  disciplina %s%n",
                    a.dia(), a.turno(),
                    salas.buscarPorId(a.salaId()).map(Sala::codigo).orElse("?"),
                    disciplinas.buscarPorId(a.disciplinaId()).map(Disciplina::codigo).orElse("?")));
        }
    }
}
