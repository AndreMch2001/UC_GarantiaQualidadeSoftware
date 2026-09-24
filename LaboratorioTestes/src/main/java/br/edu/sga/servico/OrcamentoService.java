package br.edu.sga.servico;

import br.edu.sga.dados.DepartamentoRepository;
import br.edu.sga.dados.DespesaRepository;
import br.edu.sga.modelo.Departamento;
import br.edu.sga.modelo.Despesa;
import br.edu.sga.modelo.ResultadoDespesa;
import br.edu.sga.modelo.SituacaoOrcamento;
import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.RegraOrcamento;
import br.edu.sga.regras.ValidadorCadastro;

import java.time.Clock;
import java.time.LocalDate;

/** RF-05 (registrar despesa), RF-06 (consultar situação) e RN-09 (reduzir orçamento). */
public class OrcamentoService {

    public static final String EMAIL_DIRETORIA = "diretoria@univale.edu.br";

    private final DepartamentoRepository departamentos;
    private final DespesaRepository despesas;
    private final Notificador notificador;
    private final Clock relogio;

    public OrcamentoService(DepartamentoRepository departamentos, DespesaRepository despesas,
                            Notificador notificador, Clock relogio) {
        this.departamentos = departamentos;
        this.despesas = despesas;
        this.notificador = notificador;
        this.relogio = relogio;
    }

    public ResultadoDespesa registrarDespesa(int departamentoId, String descricao, double valor) {
        Departamento dep = buscar(departamentoId);
        if (descricao == null || descricao.isBlank()) {
            throw new RegraException("DESCRICAO_OBRIGATORIA", "Descreva a despesa.");
        }
        double executado = despesas.totalExecutado(departamentoId);
        RegraOrcamento.validarDespesa(dep.orcamentoAnual(), executado, valor);

        double executadoApos = executado + valor;
        double percentual = RegraOrcamento.percentualExecutado(dep.orcamentoAnual(), executadoApos);
        boolean exigeDiretoria = RegraOrcamento.exigeAprovacaoDiretoria(valor, percentual);
        Despesa salva = despesas.salvar(new Despesa(null, departamentoId, descricao.trim(), valor,
                LocalDate.now(relogio).toString()));

        SituacaoOrcamento antes = RegraOrcamento.situacao(dep.orcamentoAnual(), executado);
        SituacaoOrcamento depois = RegraOrcamento.situacao(dep.orcamentoAnual(), executadoApos);
        if (depois != antes) { // avisa só na MUDANÇA de faixa, para não enviar e-mail a cada despesa
            notificador.enviar(EMAIL_DIRETORIA,
                    "Orçamento " + dep.codigo() + " mudou para " + depois,
                    String.format("Executado: %.2f%% do orçamento anual.", percentual));
        }
        return new ResultadoDespesa(salva, depois, percentual, exigeDiretoria);
    }

    public SituacaoOrcamento consultarSituacao(int departamentoId) {
        Departamento dep = buscar(departamentoId);
        return RegraOrcamento.situacao(dep.orcamentoAnual(), despesas.totalExecutado(departamentoId));
    }

    /** RN-09: o novo orçamento não pode ser menor do que o valor já executado. */
    public void alterarOrcamento(int departamentoId, double novoOrcamento) {
        buscar(departamentoId);
        ValidadorCadastro.validarOrcamentoAnual(novoOrcamento);
        if (novoOrcamento < despesas.totalExecutado(departamentoId)) {
            throw new RegraException("ORCAMENTO_ABAIXO_EXECUTADO",
                    "O novo orçamento é menor que o valor já executado pelo departamento.");
        }
        departamentos.atualizarOrcamento(departamentoId, novoOrcamento);
    }

    private Departamento buscar(int id) {
        return departamentos.buscarPorId(id).orElseThrow(() ->
                new RegraException("DEPARTAMENTO_INEXISTENTE", "Departamento não encontrado."));
    }
}
