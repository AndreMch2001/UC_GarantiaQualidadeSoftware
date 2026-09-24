package br.edu.sga.servico;

import br.edu.sga.dados.CursoRepository;
import br.edu.sga.dados.DepartamentoRepository;
import br.edu.sga.dados.DisciplinaRepository;
import br.edu.sga.dados.SalaRepository;
import br.edu.sga.modelo.Curso;
import br.edu.sga.modelo.Departamento;
import br.edu.sga.modelo.Disciplina;
import br.edu.sga.modelo.Sala;
import br.edu.sga.modelo.TipoSala;
import br.edu.sga.regras.RegraException;
import br.edu.sga.regras.ValidadorCadastro;

/** RF-01, RF-02, RF-03 e RF-07: cadastros básicos com validação antes de gravar. */
public class CadastroService {

    private final DepartamentoRepository departamentos;
    private final CursoRepository cursos;
    private final DisciplinaRepository disciplinas;
    private final SalaRepository salas;

    public CadastroService(DepartamentoRepository departamentos, CursoRepository cursos,
                           DisciplinaRepository disciplinas, SalaRepository salas) {
        this.departamentos = departamentos;
        this.cursos = cursos;
        this.disciplinas = disciplinas;
        this.salas = salas;
    }

    public Departamento criarDepartamento(String codigo, String nome, double orcamentoAnual) {
        ValidadorCadastro.validarCodigoDepartamento(codigo);
        ValidadorCadastro.validarNomeObrigatorio(nome);
        ValidadorCadastro.validarOrcamentoAnual(orcamentoAnual);
        return departamentos.salvar(new Departamento(null, codigo, nome.trim(), orcamentoAnual));
    }

    public Curso criarCurso(String codigo, String nome, int duracaoSemestres, int departamentoId) {
        ValidadorCadastro.validarCodigo(codigo);
        ValidadorCadastro.validarNomeObrigatorio(nome);
        ValidadorCadastro.validarDuracaoCurso(duracaoSemestres);
        departamentos.buscarPorId(departamentoId).orElseThrow(() ->
                new RegraException("DEPARTAMENTO_INEXISTENTE", "Selecione um departamento cadastrado."));
        return cursos.salvar(new Curso(null, codigo, nome.trim(), duracaoSemestres, departamentoId));
    }

    public Disciplina criarDisciplina(String codigo, String nome, int cargaHoraria, int vagas,
                                      boolean exigeLaboratorio, int cursoId) {
        ValidadorCadastro.validarCodigo(codigo);
        ValidadorCadastro.validarNomeObrigatorio(nome);
        ValidadorCadastro.validarCargaHoraria(cargaHoraria);
        ValidadorCadastro.validarVagas(vagas);
        cursos.buscarPorId(cursoId).orElseThrow(() ->
                new RegraException("CURSO_INEXISTENTE", "Selecione um curso cadastrado."));
        return disciplinas.salvar(new Disciplina(null, codigo, nome.trim(), cargaHoraria, vagas,
                exigeLaboratorio, cursoId));
    }

    public Sala criarSala(String codigo, int capacidade, TipoSala tipo) {
        ValidadorCadastro.validarCodigo(codigo);
        ValidadorCadastro.validarCapacidadeSala(capacidade);
        if (tipo == null) {
            throw new RegraException("TIPO_OBRIGATORIO", "Informe se a sala é COMUM ou LABORATORIO.");
        }
        return salas.salvar(new Sala(null, codigo, capacidade, tipo));
    }
}
