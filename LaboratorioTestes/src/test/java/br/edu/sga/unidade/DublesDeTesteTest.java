package br.edu.sga.unidade;

import br.edu.sga.dados.AgendaSalas;
import br.edu.sga.dados.Database;
import br.edu.sga.dados.DepartamentoRepository;
import br.edu.sga.dados.DespesaRepository;
import br.edu.sga.modelo.Alocacao;
import br.edu.sga.modelo.Departamento;
import br.edu.sga.modelo.DiaSemana;
import br.edu.sga.modelo.Disciplina;
import br.edu.sga.modelo.ResultadoAlocacao;
import br.edu.sga.modelo.ResultadoDespesa;
import br.edu.sga.modelo.Sala;
import br.edu.sga.modelo.SituacaoOrcamento;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.modelo.Turno;
import br.edu.sga.regras.RegraException;
import br.edu.sga.servico.AlocacaoService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TESTE DE UNIDADE COM DUBLÊS — isolar a unidade das suas dependências.
 * STUB  → Clock.fixed(...) devolve sempre a mesma data; a AgendaSalas "sempre ocupada".
 * SPY   → NotificadorSpy registra as chamadas de e-mail.
 * FAKE  → AgendaSalasFake guarda alocações numa lista; SQLite em memória para o orçamento.
 */
@DisplayName("Unidade | Dublês de teste — stub, spy e fake")
class DublesDeTesteTest {

    private static final Clock RELOGIO_FIXO = Clock.fixed(Instant.parse("2026-09-17T10:00:00Z"), ZoneId.of("UTC"));

    private Database db;
    private DespesaRepository despesas;
    private NotificadorSpy email;
    private OrcamentoService servico;
    private int departamentoId;

    @BeforeEach
    void preparar() {
        db = Database.emMemoria();
        DepartamentoRepository departamentos = new DepartamentoRepository(db);
        despesas = new DespesaRepository(db);
        departamentoId = departamentos.salvar(new Departamento(null, "ENG", "Engenharias", 10_000.00)).id();
        email = new NotificadorSpy();
        servico = new OrcamentoService(departamentos, despesas, email, RELOGIO_FIXO);
    }

    @AfterEach
    void encerrar() {
        db.close();
    }

    @Test
    @DisplayName("UD-01 | despesa que muda NORMAL → ALERTA notifica a diretoria uma vez")
    void notificaNaMudancaDeFaixa() {
        // Act
        ResultadoDespesa r = servico.registrarDespesa(departamentoId, "Bancadas", 8_500.00);
        // Assert
        assertEquals(SituacaoOrcamento.ALERTA, r.situacao());
        assertEquals(1, email.chamadas.size());
        assertEquals(OrcamentoService.EMAIL_DIRETORIA, email.chamadas.get(0).destinatario());
        assertTrue(email.chamadas.get(0).assunto().contains("ALERTA"));
    }

    @Test
    @DisplayName("UD-02 | despesa que mantém NORMAL não envia e-mail")
    void naoNotificaSemMudanca() {
        servico.registrarDespesa(departamentoId, "Material de escritório", 500.00);
        assertEquals(0, email.chamadas.size());
    }

    @Test
    @DisplayName("UD-03 | despesa sem saldo: nada gravado e nenhum e-mail")
    void semSaldoNadaAcontece() {
        RegraException erro = assertThrows(RegraException.class,
                () -> servico.registrarDespesa(departamentoId, "Equipamento", 10_000.01));
        assertEquals("SALDO_INSUFICIENTE", erro.getCodigo());
        assertEquals(0.0, despesas.totalExecutado(departamentoId), 0.001);
        assertEquals(0, email.chamadas.size());
    }

    @Test
    @DisplayName("UD-04 | data da despesa vem do relógio (stub), não do relógio do sistema")
    void dataVemDoRelogioFixo() {
        ResultadoDespesa r = servico.registrarDespesa(departamentoId, "Cabos", 100.00);
        assertEquals("2026-09-17", r.despesa().data());
    }

    @Test
    @DisplayName("UD-05 | fake: alocação em sala livre é gravada uma única vez")
    void fakeGravaAlocacao() {
        AgendaSalasFake agenda = new AgendaSalasFake();
        AlocacaoService alocacao = new AlocacaoService(agenda);
        Disciplina bd = new Disciplina(7, "BD1", "Banco de Dados I", 80, 30, true, 1);
        Sala lab = new Sala(3, "LAB-1", 30, TipoSala.LABORATORIO);

        assertEquals(ResultadoAlocacao.ALOCADA, alocacao.alocar(bd, lab, DiaSemana.TER, Turno.NOITE));
        assertEquals(1, agenda.gravadas.size());
    }

    @Test
    @DisplayName("UD-06 | stub 'sempre ocupada': o serviço bloqueia e nunca chama salvar()")
    void stubSempreOcupada() {
        int[] chamadasSalvar = {0};
        AgendaSalas sempreOcupada = new AgendaSalas() {
            @Override
            public boolean salaOcupada(int salaId, DiaSemana dia, Turno turno) {
                return true; // resposta fixa combinada = STUB
            }

            @Override
            public Alocacao salvar(Alocacao a) {
                chamadasSalvar[0]++;
                return a;
            }
        };
        AlocacaoService alocacao = new AlocacaoService(sempreOcupada);
        Disciplina gqs = new Disciplina(1, "GQS", "GQS", 80, 40, false, 1);
        Sala s101 = new Sala(1, "S-101", 40, TipoSala.COMUM);

        assertEquals(ResultadoAlocacao.BLOQUEIO_SALA_OCUPADA, alocacao.alocar(gqs, s101, DiaSemana.SEG, Turno.NOITE));
        assertEquals(0, chamadasSalvar[0]);
    }
}
