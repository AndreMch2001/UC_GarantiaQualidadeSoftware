# Roteiro de aula — laboratório SGA

Duração sugerida: 2 aulas de 120 min. Tudo pode ser feito só com o **laboratório HTML** (projetado em sala, sem instalar nada) ou, em laboratório com Java, também com o projeto Maven.

## Parte 1 — Validação de requisitos (20 min, Aula 02)
1. Distribua a seção 7 de `REQUISITOS.md` (requisitos da versão 0.9). Cada grupo aponta qual característica de bom requisito cada frase viola.
2. Compare com a versão 1.0. Pergunta-chave: *“Sala que comporta a turma” aceita capacidade igual às vagas?* A turma se divide — prova de que o requisito era ambíguo.

## Parte 2 — Caixa-preta (40 min, Aulas 04/05)
1. Na aba **Sistema** do laboratório, os grupos cadastram dados livremente e tentam “quebrar” as regras (exploratório de 10 min, com charter escrito).
2. Na aba **Caixa-preta**, para cada técnica, o grupo tenta prever o resultado esperado de cada caso **antes** de clicar em *Executar*.
3. Em laboratório Java: `mvn test`. Ler a saída do Surefire e localizar os casos pelo ID (CT-xx) no nome.

## Parte 3 — Caixa-branca (30 min, Aula 06)
1. Aba **Caixa-branca**: marque só CB-01 e observe as linhas pintadas. Acrescente CB-02: comandos e decisões vão a 100%.
2. Ligue o defeito **D-05** (`||` → `&&`). Pergunta: a suíte CB-01 + CB-02 detecta? (Não.) Acrescente CB-03: nenhuma métrica sobe e o defeito é pego. Discuta por quê.
3. Calcule V(G) de `avaliar()` no quadro (4 ou 5, conforme o critério) e confira no laboratório.
4. Em laboratório Java: `mvn verify` e abrir `target/site/jacoco/index.html`.

## Parte 4 — Defeitos injetados, incidente e reteste (30 min)

Cada grupo recebe um defeito. No laboratório HTML basta ligar a chave; no Java, faça a alteração indicada, rode `mvn test`, registre o incidente (Anexo E do plano), desfaça a alteração (correção) e rode de novo (reteste + regressão).

| Defeito | Onde alterar no Java | Alteração | Tipo de erro |
|---|---|---|---|
| D-01 | `RegraAlocacao.avaliar` | `capacidadeSala < vagasDisciplina` → `<=` | fronteira |
| D-02 | `RegraOrcamento.situacao` | `percentual >= LIMITE_ALERTA_PERCENTUAL` → `>` | fronteira |
| D-03 | `MaquinaEstadosMatricula` (bloco static) | acrescentar `TRANSICOES.put(StatusMatricula.FORMADA, Map.of(EventoMatricula.CANCELAR, StatusMatricula.CANCELADA));` | transição proibida aceita |
| D-04 | `ValidadorAluno.validarIdade` | `idade > IDADE_MAXIMA` → `>=` | fronteira |
| D-05 | `RegraOrcamento.exigeAprovacaoDiretoria` | `\|\|` → `&&` | operador lógico |

Resultado **verificado** com a suíte completa (102 testes):

| Defeito | Testes que falham | Quantos | Observação para discussão |
|---|---|---|---|
| D-01 | CT-17, CT-30, CT-31, CT-34, CI-06, UD-05 | 6 | Um único defeito derruba casos de 3 níveis. CB-13 (sem assert) continua **verde**. |
| D-02 | CT-20 | 1 | Só o valor exato da borda detecta; 79,99% e 80,01% passam. |
| D-03 | CT-45 | 1 | Só o teste da transição **proibida** detecta; os 5 válidos passam. |
| D-04 | CT-14 | 1 | Idade 100 é rejeitada; 99 e 101 continuam corretos. |
| D-05 | CB-03, CB-04 | 2 | CB-01 e CB-02 passam — com 100% de comandos e decisões. |

## Perguntas para o relatório do grupo
1. Por que o defeito D-02 escapa de um teste com 79% e 81%?
2. O antiexemplo CB-13 dá cobertura. Que número do relatório poderia enganar a equipe?
3. Qual é a diferença entre severidade e prioridade no incidente que vocês registraram?
4. Depois de corrigir, que testes vocês rodaram de novo? Isso é reteste ou regressão?
5. Se a regra RN-05 falhasse, o banco ainda impediria a alocação duplicada? Qual teste prova isso?
