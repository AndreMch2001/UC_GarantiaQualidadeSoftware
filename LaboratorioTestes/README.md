# SGA — Sistema de Gestão Acadêmica · laboratório de Garantia da Qualidade de Software

Sistema didático para cadastrar **departamentos (com orçamento), cursos, disciplinas, alunos, salas** e controlar a **distribuição de salas**, com banco de dados relacional (SQLite) e uma suíte de testes organizada pelas técnicas vistas nas aulas.

## Estrutura

```
sga-academico/
├── pom.xml                         Maven: JUnit 5, SQLite JDBC, JaCoCo (gate 80%)
├── .github/workflows/ci.yml        pipeline de integração contínua
├── docs/
│   ├── REQUISITOS.md               requisitos simulados (RF, RN, RNF, Gherkin, exercício de validação)
│   ├── PLANO_DE_TESTES.md          plano ISO/IEC/IEEE 29119-3 + casos + rastreabilidade + modelos
│   └── ROTEIRO_DE_AULA.md          atividades, defeitos injetados e resultados esperados
├── web/sga-laboratorio.html        laboratório para projetar em aula (abre em qualquer navegador)
└── src/
    ├── main/java/br/edu/sga/
    │   ├── modelo/                 entidades (records) e enums
    │   ├── regras/                 regras de negócio puras — alvo principal dos testes
    │   ├── dados/                  Database (SQLite) e repositórios com PreparedStatement
    │   ├── servico/                casos de uso (cadastro, matrícula, orçamento, alocação)
    │   └── App.java                demonstração de ponta a ponta
    ├── main/resources/schema.sql   esquema do banco (PK, FK, UNIQUE, CHECK)
    └── test/java/br/edu/sga/
        ├── caixapreta/             partição, valor limite, tabela de decisão, transição de estados
        ├── caixabranca/            cobertura de condições/MC-DC, caminhos básicos, antiexemplo
        ├── integracao/             serviços + repositórios + SQLite em memória
        └── unidade/                dublês de teste: stub, spy e fake
```

## Como executar (Java 17+ e Maven 3.9+)

| Comando | O que faz |
|---|---|
| `mvn test` | roda os 102 testes e gera `target/site/jacoco/index.html` |
| `mvn verify` | roda os testes **e** aplica o gate: cobertura ≥ 80% em `br.edu.sga.regras` |
| `mvn compile exec:java` | executa a demonstração e cria `sga.db` (abra no *DB Browser for SQLite*) |
| `mvn test -Dtest=ValorLimiteTest` | roda uma classe de teste |
| `mvn test -Dtest="*Test#limitesDaIdade"` | roda um único método |

No IntelliJ ou VS Code, os nomes definidos em `@DisplayName` (com os IDs CT-xx, CB-xx…) aparecem na árvore de testes.

## Laboratório HTML
`web/sga-laboratorio.html` reproduz as mesmas regras em JavaScript (mesmos nomes, mesmas fronteiras e mesmos IDs de caso), com banco em memória no navegador. Serve para projetar em sala: cadastrar, alocar salas, executar as suítes, ver a cobertura linha a linha e ligar os defeitos D-01 a D-05.
