# SGA — Sistema de Gestão Acadêmica
## Especificação de requisitos (simulada, para fins didáticos)

**Cliente fictício:** Centro Universitário UniVale — Pró-Reitoria Acadêmica
**Versão do documento:** 1.0 · **Release do produto:** SGA 1.0
**Uso em aula:** base de teste para a UC Garantia da Qualidade de Software (Aulas 01 a 06)

---

### 1. Contexto e objetivo

A UniVale administra hoje departamentos, cursos, disciplinas, alunos, orçamento e a distribuição de salas em planilhas separadas. Isso produz três problemas recorrentes: duas turmas marcadas na mesma sala e horário, departamentos que gastam além do orçamento sem ninguém perceber, e alunos com situação de matrícula inconsistente (por exemplo, “formado” que volta a “ativo”).

O SGA centraliza esses cadastros em um banco de dados relacional e aplica as regras de negócio antes de gravar qualquer informação.

**Partes interessadas:** Pró-Reitoria (dona do produto), secretaria acadêmica (usuária principal), chefes de departamento (orçamento), coordenação de espaço físico (salas), equipe de TI (desenvolvimento e teste).

### 2. Glossário

| Termo | Significado |
|---|---|
| Departamento | Unidade administrativa que agrupa cursos e possui orçamento anual próprio |
| Orçamento anual | Valor, em reais, que o departamento pode gastar no ano |
| Executado | Soma das despesas já registradas pelo departamento |
| Alocação | Reserva de uma sala para uma disciplina em um dia da semana e turno |
| Vagas | Número máximo de alunos previstos para a disciplina |
| Matrícula | Vínculo do aluno com o curso; possui uma situação (ATIVA, TRANCADA, FORMADA, CANCELADA) |

---

### 3. Requisitos funcionais (RF)

| ID | Requisito | Prioridade | Risco |
|---|---|---|---|
| RF-01 | Cadastrar departamento com código (3 a 6 letras maiúsculas, único), nome e orçamento anual | Alta | Médio |
| RF-02 | Cadastrar curso vinculado a um departamento, com código único, nome e duração de 1 a 12 semestres | Média | Baixo |
| RF-03 | Cadastrar disciplina vinculada a um curso, com código único, nome, carga horária, número de vagas e indicação se exige laboratório | Média | Médio |
| RF-04 | Cadastrar aluno com nome, e-mail, idade e curso; o sistema gera a matrícula no formato AAAA + sequencial de 4 dígitos | Alta | Médio |
| RF-05 | Registrar despesa no orçamento do departamento (descrição e valor); a data é a do dia do registro | Alta | Alto |
| RF-06 | Consultar a situação orçamentária de cada departamento (orçado, executado, percentual e situação) | Alta | Alto |
| RF-07 | Cadastrar sala com código único, capacidade e tipo (COMUM ou LABORATORIO) | Média | Baixo |
| RF-08 | Alocar sala para disciplina em um dia da semana (SEG a SAB) e turno (MANHA, TARDE, NOITE) | Alta | Alto |
| RF-09 | Exibir a grade de ocupação das salas (sala × dia × turno) | Média | Médio |
| RF-10 | Alterar a situação da matrícula do aluno (trancar, reativar, concluir, cancelar) | Alta | Alto |

### 4. Regras de negócio (RN)

| ID | Regra | Técnica de teste sugerida |
|---|---|---|
| RN-01 | A idade do aluno deve ser um número inteiro entre **16 e 100 anos, inclusive** | Partição de equivalência + valor limite |
| RN-02 | O e-mail deve ter usuário, “@” e domínio contendo ao menos um ponto (ex.: nome@univale.edu.br) | Partição de equivalência |
| RN-03 | A situação do orçamento é **NORMAL** quando o executado é menor que 80% do orçado; **ALERTA** de 80% (inclusive) até menos de 100%; **ESGOTADO** em 100% | Valor limite |
| RN-04 | Uma despesa só é aceita se o valor for maior que zero **e** se o executado após a despesa não ultrapassar o orçado. Gastar exatamente o saldo é permitido | Valor limite |
| RN-05 | Uma alocação só é aceita se: (C1) a sala estiver livre no dia e turno, (C2) a capacidade da sala for maior ou igual às vagas da disciplina e (C3) o tipo da sala for compatível (disciplina que exige laboratório só pode ir para sala LABORATORIO; disciplina comum pode ir para qualquer sala). Quando mais de uma condição falhar, a mensagem segue a prioridade C1 → C2 → C3 | Tabela de decisão |
| RN-06 | Ciclo de vida da matrícula: ATIVA → TRANCADA (trancar); TRANCADA → ATIVA (reativar); ATIVA → FORMADA (concluir); ATIVA ou TRANCADA → CANCELADA (cancelar). FORMADA e CANCELADA são estados finais. Qualquer outra transição é proibida | Transição de estados |
| RN-07 | Ao mudar de faixa de situação orçamentária (NORMAL → ALERTA ou ALERTA → ESGOTADO), o sistema envia aviso por e-mail à diretoria | Teste com dublê (spy) |
| RN-08 | Uma despesa exige aprovação da diretoria quando o valor for **maior que R$ 10.000,00 OU** quando o percentual executado após a despesa for **maior ou igual a 90%** | Cobertura de condições (caixa-branca) |
| RN-09 | O orçamento anual de um departamento não pode ser reduzido para um valor menor que o já executado | Partição / integração |
| RN-10 | Não é possível excluir departamento que possua cursos vinculados | Integração (banco) |
| RN-11 | Carga horária da disciplina: 20 a 120 horas, sempre múltipla de 20. Vagas: 1 a 200. Capacidade da sala: 10 a 200 | Partição + valor limite |

### 5. Requisitos não funcionais (RNF) — ligados à ISO/IEC 25010

| ID | Característica 25010 | Requisito verificável | Como verificar |
|---|---|---|---|
| RNF-01 | Confiabilidade (integridade) | Os dados ficam em banco relacional SQLite com chaves estrangeiras ativas, UNIQUE e CHECK, de modo que o banco rejeite dado inconsistente mesmo que a regra em Java falhe | Testes de integração CI-01 a CI-05 |
| RNF-02 | Manutenibilidade (testabilidade) | Cobertura de linhas e de desvios ≥ 80% no pacote `br.edu.sga.regras`; nenhum método de regra com complexidade ciclomática acima de 10 | JaCoCo (`mvn verify`) como gate no pipeline |
| RNF-03 | Eficiência de desempenho | A grade de ocupação com 500 alocações é montada em até 1 segundo em um notebook de laboratório | Medição no HTML (aba Sistema → Grade) |
| RNF-04 | Usabilidade | Toda mensagem de erro informa o campo e o que o usuário deve fazer (ex.: “A idade deve estar entre 16 e 100 anos”) | Checklist na revisão e teste exploratório |
| RNF-05 | Segurança | Todas as consultas usam parâmetros (PreparedStatement); texto digitado nunca é concatenado ao SQL | Teste CI-04 (SQL injection) + revisão de código |
| RNF-06 | Portabilidade | Executa com Java 17 ou superior em Windows, Linux e macOS; o laboratório HTML roda em qualquer navegador atual sem instalação | Execução em dois sistemas operacionais |

### 6. Critérios de aceitação (exemplos em Gherkin)

**RF-08 / RN-05 — Alocar sala**
```gherkin
Funcionalidade: Alocação de salas

  Cenário: Alocar disciplina de laboratório em laboratório livre
    Dado que a sala "LAB-1" é do tipo LABORATORIO com 30 lugares
    E que a disciplina "BD1" exige laboratório e tem 30 vagas
    E que a sala "LAB-1" está livre na terça à noite
    Quando a secretaria aloca "BD1" em "LAB-1" na terça à noite
    Então a alocação é gravada
    E a grade mostra "BD1" na célula LAB-1 / TER / NOITE

  Cenário: Bloquear sala já ocupada
    Dado que "GQS" já está alocada em "S-101" na segunda à noite
    Quando a secretaria tenta alocar "BD1" em "S-101" na segunda à noite
    Então o sistema bloqueia com a mensagem "Sala ocupada neste dia e turno"
    E nenhuma alocação nova é gravada
```

**RF-05 / RN-04 — Registrar despesa (formato regra a regra)**
- Despesa de valor zero ou negativo é recusada com a mensagem de valor inválido.
- Despesa que faz o executado passar do orçado é recusada; nada é gravado.
- Despesa igual ao saldo restante é aceita e a situação passa a ESGOTADO.
- Ao mudar de faixa, a diretoria recebe exatamente um e-mail.

### 7. Requisitos com problemas — exercício de validação (Aula 02)

Os requisitos abaixo estavam na **versão 0.9** do documento. Antes de ler a versão corrigida (seção 4), peça aos alunos que apontem qual característica de um bom requisito cada um viola (completo, não ambíguo, consistente, verificável, rastreável, viável).

| Versão 0.9 (com defeito) | Problema | Virou |
|---|---|---|
| “O sistema deve avisar quando o orçamento estiver quase acabando.” | Ambíguo e não verificável: o que é “quase”? | RN-03 (80%) e RN-07 |
| “Alunos menores de idade não podem ser cadastrados, exceto em cursos técnicos.” | Inconsistente com RN-01 (16 anos) e incompleto (o SGA não tem “curso técnico”) | RN-01 |
| “A sala deve comportar a turma.” | Não diz se capacidade igual às vagas é aceita (fronteira indefinida) | RN-05, condição C2 (≥) |
| “O sistema deve ser rápido.” | Não verificável | RNF-03 |
| “Despesas grandes precisam de aprovação.” | Ambíguo: grande em valor absoluto ou em proporção do orçamento? | RN-08 (as duas condições, ligadas por OU) |
