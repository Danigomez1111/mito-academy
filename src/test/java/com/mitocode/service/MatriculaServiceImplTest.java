package com.mitocode.service;

import com.mitocode.model.Curso;
import com.mitocode.model.DetalleMatricula;
import com.mitocode.model.Estudiante;
import com.mitocode.model.Matricula;
import com.mitocode.repo.IMatriculaRepo;
import com.mitocode.service.impl.MatriculaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MatriculaServiceImplTest {

    private IMatriculaRepo repo;
    private MatriculaServiceImpl service;

    @BeforeEach
    void init() {
        repo = mock(IMatriculaRepo.class);
        service = new MatriculaServiceImpl(repo);
    }

    @Test
    void getCursosConEstudiantesGroupsStudentsByCourseName() {
        Curso prog = new Curso(1, "Programación", "PROG", true);
        Curso bd = new Curso(2, "Base de Datos", "BD", true);

        Estudiante jaime = new Estudiante(1, "Jaime", "Medina", "12345678", 32);
        Estudiante mito = new Estudiante(2, "Mito", "X", "23456789", 25);
        Estudiante code = new Estudiante(3, "Code", "Y", "34567890", 28);

        Matricula m1 = new Matricula(1, LocalDateTime.now(), jaime, null, true);
        DetalleMatricula d1 = new DetalleMatricula(1, m1, prog, "A-101");
        m1.setDetalleMatricula(List.of(d1));

        Matricula m2 = new Matricula(2, LocalDateTime.now(), mito, null, true);
        DetalleMatricula d2 = new DetalleMatricula(2, m2, bd, "B-201");
        m2.setDetalleMatricula(List.of(d2));

        Matricula m3 = new Matricula(3, LocalDateTime.now(), code, null, true);
        DetalleMatricula d3 = new DetalleMatricula(3, m3, bd, "B-201");
        m3.setDetalleMatricula(List.of(d3));

        when(repo.findAll()).thenReturn(List.of(m1, m2, m3));

        Map<String, List<String>> result = service.getCursosConEstudiantes();

        assertThat(result)
                .hasSize(2)
                .containsEntry("Programación", List.of("Jaime Medina"))
                .containsEntry("Base de Datos", List.of("Mito X", "Code Y"));
    }
}
