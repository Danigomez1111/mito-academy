package com.mitocode.service;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Curso;
import com.mitocode.repo.ICursoRepo;
import com.mitocode.service.impl.CursoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CursoServiceImplTest {

    private ICursoRepo repo;
    private CursoServiceImpl service;
    private Curso curso;

    @BeforeEach
    void init() {
        repo = mock(ICursoRepo.class);
        service = new CursoServiceImpl(repo);
        curso = new Curso(1, "Programación", "PROG", true);
    }

    @Test
    void saveReturnsSavedCurso() {
        when(repo.save(curso)).thenReturn(curso);

        Curso result = service.save(curso);

        assertThat(result).isEqualTo(curso);
        verify(repo, times(1)).save(curso);
    }

    @Test
    void findAllReturnsAllCursos() {
        when(repo.findAll()).thenReturn(List.of(curso));

        List<Curso> result = service.findAll();

        assertThat(result).containsExactly(curso);
    }

    @Test
    void updateThrowsWhenMissing() {
        Curso updated = new Curso(null, "Base de Datos", "BD", true);
        when(repo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, updated))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("ID NOT FOUND: 999");
    }

    @Test
    void deleteRemovesCursoWhenPresent() {
        when(repo.findById(1)).thenReturn(Optional.of(curso));

        service.delete(1);

        verify(repo, times(1)).deleteById(1);
    }
}
