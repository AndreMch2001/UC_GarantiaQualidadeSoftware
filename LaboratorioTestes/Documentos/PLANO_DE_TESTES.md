# Plano de teste — SGA 1.0
### Estrutura conforme ISO/IEC/IEEE 29119-3:2021 (substituta do IEEE 829)

| Campo | Valor |
|---|---|
| Produto / versão | SGA — Sistema de Gestão Acadêmica 1.0 (Java 17 + SQLite) e Laboratório HTML |
| Base de teste | `docs/REQUISITOS.md` versão 1.0 (RF-01 a RF-10, RN-01 a RN-11, RNF-01 a RNF-06) |
| Autor | Equipe de QA (turma de Garantia da Qualidade de Software) |
| Situação | Aprovado para execução |

---

## 1. Contexto do teste

**Itens de teste**

| Item | Onde está | Nível em que é testado |
|---|---|---|
| Regras de negócio | `br.edu.sga.regras` | Unidade (caixa-preta e caixa-branca) |
| Serviços | `br.edu.sga.servico` | Unidade com dublês e integração |
| Repositórios + banco | `br.edu.sga.dados` + `schema.sql` | Integração |
| Interface do laboratório | `web/sga-laboratorio.html` | Sistema (manual/exploratório) e aceitação |

**Fora do escopo desta release:** autenticação de usuários, geração de boletos, integração com o sistema financeiro real, acessibilidade (fica registrado como risco residual).

## 2. Comunicação

Resultado de cada execução do pipeline é visível no GitHub Actions; defeitos são registrados no modelo do Anexo E; o relatório de conclusão é entregue ao dono do produto ao final da sprint.

## 3. Riscos de produto (teste orientado a risco)

Risco = probabilidade de falha × impacto no negócio.

| ID | Risco | Prob. | Impacto | Intensidade | Resposta no plano |
|---|---|---|---|---|---|
| RS-01 | Duas disciplinas alocadas na mesma sala, dia e turno | Média | Alto | Intensivo | Tabela de decisão (RN-05) + UNIQUE no banco + teste de integração |
| RS-02 | Departamento gastar além do orçamento | Média | Alto | Intensivo + automação | Valor limite em centavos (RN-04), testes CT-24 a CT-27 no pipeline |
| RS-03 | Aviso de orçamento não chegar à diretoria (ou chegar em excesso) | Média | Médio | Sistemático | Dublê spy (UD-01 a UD-03) |
| RS-04 | Transição proibida de matrícula (ex.: FORMADA → CANCELADA) | Média | Alto | Intensivo | Transição de estados 0-switch + transições proibidas |
| RS-05 | Cadastro de aluno com dado inválido | Alta | Médio | Intensivo | Partição + valor limite + CHECK do banco |
| RS-06 | SQL injection pelos campos de texto | Baixa | Alto | Sistemático | CI-04 + revisão de código (PreparedStatement) |
| RS-07 | Lentidão na grade de salas | Baixa | Baixo | Exploratório | Medição simples no laboratório HTML |

## 4. Estratégia de teste

**Níveis (modelo V)**

| Nível | Base de teste | Ferramenta | Quem executa | Casos |
|---|---|---|---|---|
| Unidade | Regras (RN) e código | JUnit 5 | Desenvolvimento | CT-01 a CT-82, CB-01 a CB-13, UD-01 a UD-06 |
| Integração | Arquitetura: serviço ↔ repositório ↔ SQLite | JUnit 5 + SQLite em memória | Dev e QA | CI-01 a CI-08 |
| Sistema | Requisitos funcionais e não funcionais | Laboratório HTML (manual) | QA | CS-01 a CS-08 |
| Aceitação | Critérios de aceitação em Gherkin | Laboratório HTML (manual) | Dono do produto | CA-01 e CA-02 |

**Técnicas por requisito (ISO/IEC/IEEE 29119-4)**

| Requisito | Técnica | Justificativa |
|---|---|---|
| RN-01, RN-02 | Partição de equivalência | Domínio de entrada com faixas e formatos |
| RN-01, RN-03, RN-04, RN-11 | Análise de valor limite (3 valores) | Regras com fronteira numérica |
| RN-05 | Tabela de decisão | Três condições que se combinam |
| RN-06 | Transição de estados | Objeto com ciclo de vida |
| RN-08 | Cobertura de condições / MC/DC | Decisão composta com OU |
| RN-05, RN-03 | Caminhos básicos (McCabe) | Métodos com várias decisões |
| RNF-04, RNF-05 | Adivinhação de erro e checklist | Especificação não cobre tudo |

**Tipos:** funcional (todos os RF/RN), não funcional (RNF-01, 03, 04, 05), estrutural (caixa-branca com JaCoCo) e de mudança (reteste e regressão após cada correção de defeito).

**Ambiente:** Java 17+, Maven 3.9+, SQLite em memória para testes; navegador atual para o laboratório HTML. Dados de teste criados pelas fixtures `@BeforeEach` — nenhum teste depende de outro.

## 5. Critérios

| Critério | Definição |
|---|---|
| Entrada | Código compila; requisitos v1.0 revisados; ambiente com Java 17 e Maven |
| Saída (liberação) | 100% dos casos de severidade alta executados e aprovados; nenhum defeito crítico ou alto em aberto; cobertura de linhas e desvios ≥ 80% em `br.edu.sga.regras` (gate `mvn verify`) |
| Suspensão | Build quebrado ou mais de 30% dos casos falhando por um mesmo defeito bloqueante |
| Retomada | Defeito bloqueante corrigido e reteste aprovado |

## 6. Testware

Classes de teste em `src/test/java` (organizadas por técnica), relatório JaCoCo em `target/site/jacoco/index.html`, relatórios do Surefire em `target/surefire-reports`, laboratório HTML, registros de incidente (Anexo E).

## 7. Cronograma (alinhado às aulas)

| Aula | Atividade sobre o SGA |
|---|---|
| 01 | Diagnóstico ISO/IEC 25010 do SGA; escolher dois atributos críticos |
| 02 | Validar os requisitos (seção 7 do documento de requisitos); escrever critérios de aceitação; montar este plano |
| 04/05 | Projetar casos com as quatro técnicas caixa-preta; ler e executar os testes JUnit |
| 06 | Caixa-branca: cobertura, MC/DC, complexidade ciclomática; injetar defeitos; relatar incidentes |

## 8. Pessoas

Grupos de 3 a 4 alunos. Papéis rotativos: **analista de teste** (projeta casos), **executor** (roda e registra evidência), **desenvolvedor** (corrige o defeito injetado), **dono do produto** (aprova o relatório).

## 9. Métricas

Casos planejados × executados × aprovados; defeitos por severidade; cobertura de linhas e de desvios; taxa de detecção de defeitos injetados (quantos dos D-01 a D-05 cada conjunto de testes encontrou).

## 10. Aprovação

Revisado pelo professor (papel de gerente de qualidade) e aprovado pelo dono do produto do grupo.

---

# Anexo A — Especificação dos casos caixa-preta

### A.1 Partição de equivalência — RN-01 (idade) e RN-02 (e-mail)

| Classe | Faixa | Situação | Caso | Entrada | Resultado esperado |
|---|---|---|---|---|---|
| CE-1 | idade < 16 | Inválida | CT-01 | 12 | IDADE_INVALIDA |
| CE-2 | 16 ≤ idade ≤ 100 | Válida | CT-02 | 35 | Aceita |
| CE-3 | idade > 100 | Inválida | CT-03 | 130 | IDADE_INVALIDA |
| CE-4 | vazio | Inválida | CT-04 | "" | IDADE_OBRIGATORIA |
| CE-5 | não numérico | Inválida | CT-05 | "vinte" | IDADE_NAO_NUMERICA |
| CE-6 | e-mail bem formado | Válida | CT-06 | ana.souza@aluno.univale.edu.br | Aceita |
| CE-7 | sem “@” | Inválida | CT-07 | ana.univale.edu.br | EMAIL_INVALIDO |
| CE-8 | domínio sem ponto | Inválida | CT-08 | ana@univale | EMAIL_INVALIDO |
| CE-9 | vazio | Inválida | CT-09 | "" | EMAIL_OBRIGATORIO |

Regra aplicada: classes inválidas testadas **uma por caso**, para evitar mascaramento de defeito.

### A.2 Análise de valor limite (3 valores)

| Caso | Regra | Entrada | Esperado | Defeito que o caso pega |
|---|---|---|---|---|
| CT-10 / 11 / 12 | RN-01 borda inferior | 15 / 16 / 17 | rejeita / aceita / aceita | `<=` no lugar de `<` na idade mínima |
| CT-13 / 14 / 15 | RN-01 borda superior | 99 / 100 / 101 | aceita / aceita / rejeita | `>=` no lugar de `>` (defeito D-04) |
| CT-16 / 17 / 18 | RN-05 capacidade (vagas = 40) | 39 / 40 / 41 | bloqueia / aloca / aloca | `<=` no lugar de `<` (defeito D-01) |
| CT-19 / 20 / 21 | RN-03 faixa de alerta (orçado 100.000) | 79.990 / 80.000 / 80.010 | NORMAL / ALERTA / ALERTA | `>` no lugar de `>=` (defeito D-02) |
| CT-22 / 23 | RN-03 esgotado | 99.990 / 100.000 | ALERTA / ESGOTADO | fronteira dos 100% |
| CT-24 / 25 | RN-04 saldo (orçado 1.000, executado 900) | 100,00 / 100,01 | aceita / SALDO_INSUFICIENTE | comparação com ponto flutuante |
| CT-26 / 27 | RN-04 valor mínimo | 0,00 / 0,01 | VALOR_INVALIDO / aceita | aceitar despesa zerada |
| CT-50 a CT-81 | RN-11 e RF-01 | ver `ValidacoesCadastroTest` | — | bordas de cadastro |

### A.3 Tabela de decisão — RN-05 (alocação de sala)

| Condição | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 |
|---|---|---|---|---|---|---|---|---|
| C1 Sala livre? | S | S | S | S | N | N | N | N |
| C2 Capacidade ≥ vagas? | S | S | N | N | S | S | N | N |
| C3 Tipo compatível? | S | N | S | N | S | N | S | N |
| **Aloca** | X | | | | | | | |
| **Bloqueia: tipo** | | X | | | | | | |
| **Bloqueia: capacidade** | | | X | X | | | | |
| **Bloqueia: ocupada** | | | | | X | X | X | X |

Colapsando as colunas com condição indiferente: **8 regras → 4 casos** (CT-30 a CT-33). CT-34 cobre a segunda forma de C3 ser verdadeira (disciplina comum em laboratório) e CT-35 confirma a prioridade da mensagem quando tudo falha.

### A.4 Transição de estados — RN-06 (matrícula)

```
            trancar                 concluir
  ┌───────┐ ───────▶ ┌──────────┐     ┌─────────┐
  │ ATIVA │          │ TRANCADA │     │ FORMADA │ (final)
  └───────┘ ◀─────── └──────────┘     └─────────┘
      │  │   reativar       │               ▲
      │  └──────────────────┼───────────────┘
      │ cancelar            │ cancelar
      ▼                     ▼
  ┌──────────────────────────────┐
  │           CANCELADA          │ (final)
  └──────────────────────────────┘
```

| Caso | Estado | Evento | Esperado |
|---|---|---|---|
| CT-40 | ATIVA | TRANCAR | TRANCADA |
| CT-41 | TRANCADA | REATIVAR | ATIVA |
| CT-42 | ATIVA | CONCLUIR | FORMADA |
| CT-43 | ATIVA | CANCELAR | CANCELADA |
| CT-44 | TRANCADA | CANCELAR | CANCELADA |
| CT-45 | FORMADA | CANCELAR | TRANSICAO_INVALIDA (defeito D-03) |
| CT-46 | CANCELADA | REATIVAR | TRANSICAO_INVALIDA |
| CT-47 | TRANCADA | CONCLUIR | TRANSICAO_INVALIDA |
| CT-48 | ATIVA | REATIVAR | TRANSICAO_INVALIDA |

---

# Anexo B — Especificação dos casos caixa-branca

### B.1 Cobertura de comandos, decisões e condições — `RegraOrcamento.exigeAprovacaoDiretoria`

```java
L1  public static boolean exigeAprovacaoDiretoria(double valor, double percentualApos) {
L2      if (valor > 10_000.00 || percentualApos >= 90.0) {      // A || B
L3          return true;
L4      }
L5      return false;
L6  }
```

| Conjunto executado | Comandos | Decisões | Condições avaliadas (A e B em V e F) | Defeito D-05 (`\|\|` → `&&`) |
|---|---|---|---|---|
| CB-01 (A=V; B nem é avaliada) | 3/4 = 75% | 1/2 = 50% | 1/4 = 25% | sobrevive |
| CB-01 + CB-02 (A=F, B=F) | **100%** | **100%** | 3/4 = 75% (B nunca foi V) | **sobrevive** |
| + CB-03 (A=V, B=F) | 100% | 100% | 3/4 = 75% (não mudou!) | detectado |
| + CB-04 (A=F, B=V) | 100% | 100% | 4/4 = 100% | detectado |
| Conjunto MC/DC: CB-02, CB-03, CB-04 | 100% | 100% | 100% + cada condição decide sozinha | detectado |

**Leitura para a turma:** (1) Com CB-01 e CB-02, comandos e decisões estão em 100% e o defeito passa. (2) O Java usa **curto-circuito**: quando A é verdadeira, B nem é avaliada — por isso, no relatório do JaCoCo, o `if` aparece amarelo com “1 of 4 branches missed”: é a cobertura de condições apontando o buraco. (3) CB-03 **não aumenta nenhum percentual** e mesmo assim mata o defeito: cobertura mede execução, e só o `assert` com o valor certo verifica comportamento. (4) O MC/DC exige que cada condição mude o resultado sozinha: CB-03 × CB-02 prova A; CB-04 × CB-02 prova B — n + 1 = 3 casos.

### B.2 Complexidade ciclomática — `RegraAlocacao.avaliar`

```java
if (!salaLivre)                        return BLOQUEIO_SALA_OCUPADA;  // decisão 1
if (capacidadeSala < vagasDisciplina)  return BLOQUEIO_CAPACIDADE;    // decisão 2
if (exigeLab && tipo != LABORATORIO)   return BLOQUEIO_TIPO_SALA;     // decisão 3
return ALOCADA;
```

V(G) = decisões + 1 = 3 + 1 = **4 caminhos básicos** → casos CB-05 a CB-08. Se contarmos cada condição atômica (o `&&` da decisão 3), V(G) = 5 — é o que ferramentas como Checkstyle e SonarQube reportam. Discutir: qual caso extra seria necessário para cobrir o quinto caminho? (Resposta: `exigeLab = true` com sala LABORATORIO, que é o CT-30.)

### B.3 Caminhos de `RegraOrcamento.situacao` e o antiexemplo

CB-09 a CB-11 percorrem as três saídas; CB-12 percorre o caminho de exceção (orçado = 0). CB-13 é um **antiexemplo**: executa todos os caminhos de `avaliar()` sem nenhum `assert` — soma cobertura e não detecta nenhum defeito.

---

# Anexo C — Testes de integração, dublês e sistema

| Caso | Nível | O que verifica | Requisito |
|---|---|---|---|
| CI-01 | Integração | Código de departamento duplicado → REGISTRO_DUPLICADO | RF-01 / RNF-01 |
| CI-02 | Integração | Excluir departamento com curso → VINCULO_EXISTENTE | RN-10 |
| CI-03 | Integração | UNIQUE (sala, dia, turno) barra alocação duplicada mesmo sem a regra | RN-05 / RNF-01 |
| CI-04 | Integração | Nome com `'); DROP TABLE aluno;--` é gravado literalmente | RNF-05 |
| CI-05 | Integração | CHECK do banco barra idade 12 gravada sem validação | RN-01 / RNF-01 |
| CI-06 | Integração | Segunda alocação no mesmo horário é bloqueada e não grava | RF-08 |
| CI-07 | Integração | Matrícula 20260001 com relógio fixo (teste repetível) | RF-04 |
| CI-08 | Integração | Orçamento não pode ficar abaixo do executado | RN-09 |
| UD-01 | Unidade (spy) | Mudança NORMAL → ALERTA envia 1 e-mail à diretoria | RN-07 |
| UD-02 | Unidade (spy) | Sem mudança de faixa, nenhum e-mail | RN-07 |
| UD-03 | Unidade (spy) | Sem saldo: nada gravado, nenhum e-mail | RN-04 |
| UD-04 | Unidade (stub) | Data da despesa vem do `Clock.fixed` | RF-05 |
| UD-05 | Unidade (fake) | Alocação gravada na agenda em memória | RF-08 |
| UD-06 | Unidade (stub) | Agenda “sempre ocupada” impede `salvar()` | RN-05 |

**Casos de sistema (manuais, no laboratório HTML)**

| Caso | Passos | Resultado esperado | Req. |
|---|---|---|---|
| CS-01 | Cadastrar aluno com idade 15 | Mensagem “A idade deve estar entre 16 e 100 anos”; nada gravado | RN-01 |
| CS-02 | Cadastrar dois departamentos com código ENG | Segundo cadastro recusado com mensagem de duplicidade | RF-01 |
| CS-03 | Alocar GQS em S-101 SEG/NOITE e depois BD1 no mesmo horário | Segunda alocação bloqueada: sala ocupada | RN-05 |
| CS-04 | Registrar despesas até o departamento passar de 80% | Situação muda para ALERTA e o aviso à diretoria aparece no registro de e-mails | RN-03 / RN-07 |
| CS-05 | Trancar e depois concluir a matrícula de um aluno | “Concluir” não é permitido para matrícula TRANCADA | RN-06 |
| CS-06 | Cadastrar aluno com nome `Robert'); DROP TABLE aluno;--` | Aluno gravado com o nome literal; tabela intacta | RNF-05 |
| CS-07 | Gerar 500 alocações de carga e abrir a grade | Tempo exibido ≤ 1 s | RNF-03 |
| CS-08 | Sessão exploratória de 20 min, charter: “explorar alocação com foco em conflito de horário” | Registro do que foi feito e achado | — |

**Aceitação:** CA-01 e CA-02 = os dois cenários Gherkin da seção 6 dos requisitos, executados pelo “dono do produto” do grupo.

---

# Anexo D — Matriz de rastreabilidade

| Requisito | Critério de aceitação | Casos de teste | Nível | Evidência |
|---|---|---|---|---|
| RF-01 | Código único, 3–6 letras | CT-50–55, CI-01, CS-02 | Unidade, integração, sistema | Surefire + registro manual |
| RF-04 / RN-01 | Idade 16–100 | CT-01–05, CT-10–15, CI-05, CI-07, CS-01 | Unidade, integração, sistema | Surefire + laboratório |
| RN-02 | E-mail bem formado | CT-06–09 | Unidade | Surefire |
| RN-03 | Faixas 80% e 100% | CT-19–23, CB-09–11 | Unidade | Surefire + JaCoCo |
| RF-05 / RN-04 | Não ultrapassa o orçado | CT-24–27, UD-03 | Unidade | Surefire |
| RN-05 / RF-08 | Tabela de decisão | CT-16–18, CT-30–35, CB-05–08, CI-03, CI-06, UD-05, UD-06, CS-03, CA-01, CA-02 | Todos | Surefire + laboratório |
| RN-06 / RF-10 | Transições válidas e proibidas | CT-40–48, CS-05 | Unidade, sistema | Surefire + laboratório |
| RN-07 | E-mail só na mudança de faixa | UD-01–02, CS-04 | Unidade, sistema | Surefire + registro de e-mails |
| RN-08 | Valor > 10 mil OU ≥ 90% | CB-01–04 | Unidade (caixa-branca) | Surefire + JaCoCo |
| RN-09 | Orçamento ≥ executado | CI-08 | Integração | Surefire |
| RN-10 | Exclusão com vínculo | CI-02 | Integração | Surefire |
| RN-11 | Faixas de cadastro | CT-56–72 | Unidade | Surefire |
| RNF-02 | Cobertura ≥ 80% | toda a suíte | Unidade | `mvn verify` (gate JaCoCo) |
| RNF-03 | Grade ≤ 1 s | CS-07 | Sistema | Tempo exibido no laboratório |
| RNF-05 | Sem SQL injection | CI-04, CS-06 | Integração, sistema | Surefire + laboratório |

---

# Anexo E — Modelos de registro

### E.1 Relato de incidente

| Campo | Preencher |
|---|---|
| ID do incidente | INC-___ |
| Caso de teste de origem | CT-__ / CB-__ / CI-__ |
| Versão do código e ambiente | commit / Java / SO / navegador |
| Passos para reproduzir | 1. … 2. … 3. … |
| Resultado esperado | |
| Resultado obtido | (copiar a linha `expected: <…> but was: <…>`) |
| Evidência | print, log do Surefire, relatório JaCoCo |
| Severidade (impacto) | Crítica / Alta / Média / Baixa |
| Prioridade (urgência) | Imediata / Alta / Normal / Baixa |
| Requisito afetado | RN-__ |

### E.2 Relatório de conclusão

Produto e versão testados · ambiente · documentos de base · casos planejados/executados/aprovados · defeitos encontrados por severidade · cobertura obtida · o que ficou fora do escopo e risco residual assumido · recomendação (liberar / não liberar) · responsável e data.
