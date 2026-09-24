# SGA — Sistema de Gestão Acadêmica

Laboratório da disciplina **Garantia da Qualidade de Software**.

É um sistema didático para cadastrar **departamentos** (com orçamento), **cursos**, **disciplinas**, **alunos** e **salas**, e controlar a **distribuição de salas**. Usa banco de dados relacional (SQLite) e uma suíte de testes organizada pelas técnicas vistas em aula.

---

## Duas formas de usar

Você pode explorar o laboratório de dois jeitos:

| Forma | Para quem | O que precisa |
| ----- | --------- | ------------- |
| **No navegador (web)** | Quem quer experimentar rápido, sem instalar nada | Só um navegador (Chrome, Edge, Firefox…) |
| **Em Java (Maven)** | Quem vai estudar o código e rodar os testes automatizados | Java 17+ e Maven 3.9+ |

### Opção 1 — Executar na web (mais fácil)

Existe uma versão do sistema que roda **direto no navegador**, sem instalar Java nem Maven.

1. Abra a pasta `web/` deste projeto.
2. Dê um duplo clique no arquivo [`web/sga-laboratorio.html`](./web/sga-laboratorio.html)  
   **ou** arraste o arquivo para uma aba do navegador.

Nessa versão web você pode:

- cadastrar dados e alocar salas;
- executar as suítes de teste;
- ver a cobertura linha a linha;
- ligar/desligar os defeitos didáticos (D-01 a D-05).

As regras são as mesmas do código Java (mesmos nomes, mesmas fronteiras e mesmos IDs de caso). O banco fica **em memória no navegador** — ideal para projetar e praticar em sala.

### Opção 2 — Executar em Java

Requisitos: **Java 17+** e **Maven 3.9+**.

No terminal, dentro da pasta deste projeto (`LaboratorioTestes/`):

| Comando | O que faz |
| ------- | --------- |
| `mvn test` | Roda os 102 testes e gera o relatório de cobertura em `target/site/jacoco/index.html` |
| `mvn verify` | Roda os testes **e** exige cobertura ≥ 80% no pacote `br.edu.sga.regras` |
| `mvn compile exec:java` | Executa a demonstração completa e cria o arquivo `sga.db` (pode abrir no *DB Browser for SQLite*) |
| `mvn test -Dtest=ValorLimiteTest` | Roda só uma classe de teste |
| `mvn test -Dtest="*Test#limitesDaIdade"` | Roda só um método de teste |

No IntelliJ ou VS Code, os nomes definidos em `@DisplayName` (com os IDs CT-xx, CB-xx…) aparecem na árvore de testes.

---

## Estrutura do projeto

```
LaboratorioTestes/
├── pom.xml                         Configuração Maven (JUnit 5, SQLite, JaCoCo com gate 80%)
├── .github/workflows/ci.yml        Pipeline de integração contínua
├── docs/
│   ├── REQUISITOS.md               Requisitos simulados (RF, RN, RNF, Gherkin, exercício de validação)
│   ├── PLANO_DE_TESTES.md          Plano ISO/IEC/IEEE 29119-3 + casos + rastreabilidade + modelos
│   └── ROTEIRO_DE_AULA.md          Atividades, defeitos injetados e resultados esperados
├── web/
│   └── sga-laboratorio.html        Versão web — abre em qualquer navegador
└── src/
    ├── main/java/br/edu/sga/
    │   ├── modelo/                 Entidades (records) e enums
    │   ├── regras/                 Regras de negócio — alvo principal dos testes
    │   ├── dados/                  Banco SQLite e repositórios
    │   ├── servico/                Casos de uso (cadastro, matrícula, orçamento, alocação)
    │   └── App.java                Demonstração de ponta a ponta
    ├── main/resources/schema.sql   Esquema do banco (PK, FK, UNIQUE, CHECK)
    └── test/java/br/edu/sga/
        ├── caixapreta/             Partição, valor limite, tabela de decisão, transição de estados
        ├── caixabranca/            Cobertura de condições/MC-DC, caminhos básicos, antiexemplo
        ├── integracao/             Serviços + repositórios + SQLite em memória
        └── unidade/                Dublês de teste: stub, spy e fake
```

---

## Documentação

| Arquivo | Conteúdo |
| ------- | -------- |
| [docs/REQUISITOS.md](./docs/REQUISITOS.md) | Requisitos do sistema (funcionais, de negócio e não funcionais) |
| [docs/PLANO_DE_TESTES.md](./docs/PLANO_DE_TESTES.md) | Plano de testes e rastreabilidade |
| [docs/ROTEIRO_DE_AULA.md](./docs/ROTEIRO_DE_AULA.md) | Roteiro de aula com atividades e defeitos injetados |

---

## Resumo rápido

1. **Só quero ver o sistema funcionando?** → Abra [`web/sga-laboratorio.html`](./web/sga-laboratorio.html) no navegador.
2. **Quero rodar os testes em Java?** → Use `mvn test` (precisa de Java e Maven).
3. **Quero entender o que foi pedido?** → Comece pelos arquivos em `docs/`.
