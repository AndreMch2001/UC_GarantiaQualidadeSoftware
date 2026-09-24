package br.edu.sga.regras;

import br.edu.sga.modelo.SituacaoOrcamento;

/**
 * Regras do orçamento departamental.
 * RN-03: situação NORMAL (&lt; 80%), ALERTA (de 80% até menos de 100%), ESGOTADO (100%).
 * RN-04: uma despesa não pode fazer o executado ultrapassar o orçado.
 * RN-08: exige aprovação da diretoria se valor &gt; R$ 10.000,00 OU se o percentual após a despesa for &gt;= 90%.
 */
public final class RegraOrcamento {

    public static final double LIMITE_ALERTA_PERCENTUAL = 80.0;
    public static final double LIMITE_DIRETORIA_VALOR = 10_000.00;
    public static final double LIMITE_DIRETORIA_PERCENTUAL = 90.0;

    private RegraOrcamento() { }

    /** Percentual executado com duas casas decimais. */
    public static double percentualExecutado(double orcado, double executado) {
        if (orcado <= 0) {
            throw new RegraException("ORCAMENTO_ZERADO", "Defina um orçamento anual maior que zero.");
        }
        return Math.round(executado / orcado * 10_000.0) / 100.0;
    }

    public static SituacaoOrcamento situacao(double orcado, double executado) {
        double percentual = percentualExecutado(orcado, executado);
        if (percentual >= 100.0) {
            return SituacaoOrcamento.ESGOTADO;
        }
        if (percentual >= LIMITE_ALERTA_PERCENTUAL) {
            return SituacaoOrcamento.ALERTA;
        }
        return SituacaoOrcamento.NORMAL;
    }

    /** Comparação em centavos: evita o erro clássico de somar números decimais (0,1 + 0,2 != 0,3). */
    public static void validarDespesa(double orcado, double executado, double valor) {
        if (valor <= 0) {
            throw new RegraException("VALOR_INVALIDO", "O valor da despesa deve ser maior que zero.");
        }
        if (centavos(executado) + centavos(valor) > centavos(orcado)) {
            throw new RegraException("SALDO_INSUFICIENTE",
                    "A despesa ultrapassa o saldo do departamento. Reduza o valor ou solicite suplementação.");
        }
    }

    public static boolean exigeAprovacaoDiretoria(double valor, double percentualApos) {
        if (valor > LIMITE_DIRETORIA_VALOR || percentualApos >= LIMITE_DIRETORIA_PERCENTUAL) {
            return true;
        }
        return false;
    }

    static long centavos(double valor) {
        return Math.round(valor * 100);
    }
}
