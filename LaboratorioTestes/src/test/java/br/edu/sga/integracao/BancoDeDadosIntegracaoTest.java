package br.edu.sga.integracao;

import br.edu.sga.dados.AlocacaoRepository;
import br.edu.sga.dados.AlunoRepository;
import br.edu.sga.dados.CursoRepository;
import br.edu.sga.dados.DadosException;
import br.edu.sga.dados.Database;
import br.edu.sga.dados.DepartamentoRepository;
import br.edu.sga.dados.DespesaRepository;
import br.edu.sga.dados.DisciplinaRepository;
import br.edu.sga.dados.SalaRepository;
import br.edu.sga.modelo.Alocacao;
import br.edu.sga.modelo.Aluno;
import br.edu.sga.modelo.Curso;
import br.edu.sga.modelo.Departamento;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Disciplina;
import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.Sala;
import br.edu.sga.modelo.StatusMatricula;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.modelo.Turno;
import br.edu.sga.regras.RegraException;
import br.edu.sga.servico.AlocacaoService;
import br.edu.sga.servico.CadastroService;
import br.edu.sga.servico.MatriculaService;
import br.edu.sga.servico.OrcamentoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * NÍVEL DE INTEGRAÇÃO — regras + repositórios + SQLite real (em memória).
 * @BeforeEach é a FIXTURE: cada teste recebe um banco novo e povoado (setup);
 * @AfterEach fecha a conexão e o banco em memória desaparece (teardown).
 */
@DisplayName("Integração | Serviços + repositórios + SQLite")
class BancoDeDadosIntegracaoTest {

    private static final Clock RELOGIO_FIXO = Clock.fixed(Instant.parse("2026-09-17T19:00:00Z"), ZoneId.of("UTC"));

    private Database db;
    private DepartamentoRepository departamentos;
    private CursoRepository cursos;
    private AlunoRepository alunos;
    private AlocacaoRepository alocacoes;
    private DespesaRepository despesas;
    private CadastroService cadastro;

    private Departamento computacao;
    private Curso engSoftware;
    private Disciplina gqs;
    private Sala s101;

    @BeforeEach
    void prepararBanco() {
        db = Database.emMemoria();
        departamentos = new DepartamentoRepository(db);
        cursos = new CursoRepository(db);
        alunos = new AlunoRepository(db);
        alocacoes = new AlocacaoRepository(db);
        despesas = new DespesaRepository(db);
        cadastro = new CadastroService(departamentos, cursos, new DisciplinaRepository(db), new SalaRepository(db));

        computacao = cadastro.criarDepartamento("COMP", "Computação", 80_000.00);
        engSoftware = cadastro.criarCurso("ES", "Engenharia de Software", 8, computacao.id());
        gqs = cadastro.criarDisciplina("GQS", "Garantia da Qualidade de Software", 80, 40, false, engSoftware.id());
        s101 = cadastro.criarSala("S-101", 40, TipoSala.COMUM);
    }

    @AfterEach
    void fecharBanco() {
        db.close();
    }

    @Test
    @DisplayName("CI-01 | código de departamento duplicado é barrado pelo UNIQUE")
    void codigoDuplicado() {
        DadosException erro = assertThrows(DadosException.class,
                () -> cadastro.criarDepartamento("COMP", "Outro nome", 1_000.00));
        assertEquals("REGISTRO_DUPLICADO", erro.getCodigo());
    }

    @Test
    @DisplayName("CI-02 | excluir departamento com curso vinculado é barrado pela FOREIGN KEY")
    void exclusaoComVinculo() {
        DadosException erro = assertThrows(DadosException.class, () -> departamentos.excluir(computacao.id()));
        assertEquals("VINCULO_EXISTENTE", erro.getCodigo());
        assertEquals(1, departamentos.listarTodos().size());
    }

    @Test
    @DisplayName("CI-03 | defesa em profundidade: banco rejeita a mesma sala/dia/turno mesmo sem passar pela regra")
    void alocacaoDuplicadaDiretoNoBanco() {
        alocacoes.salvar(new Alocacao(null, gqs.id(), s101.id(), DiaSemana.SEG, Turno.NOITE));
        DadosException erro = assertThrows(DadosException.class,
                () -> alocacoes.salvar(new Alocacao(null, gqs.id(), s101.id(), DiaSemana.SEG, Turno.NOITE)));
        assertEquals("REGISTRO_DUPLICADO", erro.getCodigo());
    }

    @Test
    @DisplayName("CI-04 | texto com SQL injection é gravado literalmente (PreparedStatement)")
    void sqlInjectionNaoExecuta() {
        String nomeMalicioso = "Robert'); DROP TABLE aluno;--";
        alunos.salvar(new Aluno(null, "20269999", nomeMalicioso, "bobby@x.com", 20,
                engSoftware.id(), StatusMatricula.ATIVA));

        assertEquals(nomeMalicioso, alunos.buscarPorNome("Robert").get(0).nome());
        assertEquals(1, alunos.listarTodos().size()); // a tabela continua existindo
    }

    @Test
    @DisplayName("CI-05 | CHECK do banco barra idade fora da faixa gravada sem validação")
    void checkDoBanco() {
        DadosException erro = assertThrows(DadosException.class, () -> alunos.salvar(
                new Aluno(null, "20260500", "Menor de Idade", "menor@x.com", 12, engSoftware.id(),
                        StatusMatricula.ATIVA)));
        assertEquals("VALOR_FORA_DO_DOMINIO", erro.getCodigo());
    }

    @Test
    @DisplayName("CI-06 | segunda alocação na mesma sala/dia/turno é bloqueada pela regra e não grava")
    void alocacaoServicoComBanco() {
        AlocacaoService servico = new AlocacaoService(alocacoes);
        assertEquals(ResultadoAlocacao.ALOCADA, servico.alocar(gqs, s101, DiaSemana.QUA, Turno.NOITE));
        assertEquals(ResultadoAlocacao.BLOQUEIO_SALA_OCUPADA, servico.alocar(gqs, s101, DiaSemana.QUA, Turno.NOITE));
        assertEquals(1, alocacoes.contar());
    }

    @Test
    @DisplayName("CI-07 | matrícula gerada com ano do relógio fixo + sequencial (teste repetível)")
    void matriculaDeterministica() {
        MatriculaService servico = new MatriculaService(alunos, cursos, RELOGIO_FIXO);
        Aluno aluno = servico.cadastrar("Ana Souza", "ANA@Aluno.edu.br", "19", engSoftware.id());
        assertEquals("20260001", aluno.matricula());
        assertEquals("ana@aluno.edu.br", aluno.email());
        assertEquals(StatusMatricula.ATIVA, aluno.status());
    }

    @Test
    @DisplayName("CI-08 | RN-09: orçamento não pode ser reduzido abaixo do já executado")
    void reducaoDeOrcamento() {
        OrcamentoService servico = new OrcamentoService(departamentos, despesas, (d, a, m) -> { }, RELOGIO_FIXO);
        servico.registrarDespesa(computacao.id(), "Licenças", 50_000.00);
        RegraException erro = assertThrows(RegraException.class,
                () -> servico.alterarOrcamento(computacao.id(), 40_000.00));
        assertEquals("ORCAMENTO_ABAIXO_EXECUTADO", erro.getCodigo());
    }
}
