package br.edu.sga.modelo;

/** Retorno do registro de uma despesa: o que foi gravado e como ficou o orçamento. */
public record ResultadoDespesa(Despesa despesa, SituacaoOrcamento situacao,
                               double percentualExecutado, boolean exigeAprovacaoDiretoria) { }
