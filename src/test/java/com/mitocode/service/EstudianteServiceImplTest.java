package com.mitocode.service;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Estudiante;
import com.mitocode.repo.IEstudianteRepo;
import com.mitocode.service.impl.EstudianteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EstudianteServiceImplTest {

    private IEstudianteRepo repo;
    private EstudianteServiceImpl service;
    private Estudiante estudiante;

    @BeforeEach
    void init() {
        repo = mock(IEstudianteRepo.class);
        service = new EstudianteServiceImpl(repo);
        estudiante = new Estudiante(1, "Jaime", "Medina", "12345678", 32);
    }

    @Test
    void saveReturnsSavedEstudiante() {
        when(repo.save(estudiante)).thenReturn(estudiante);

        Estudiante result = service.save(estudiante);

        assertThat(result).isEqualTo(estudiante);
        verify(repo, times(1)).save(estudiante);
    }

    @Test
    void findByIdReturnsEstudianteWhenPresent() {
        when(repo.findById(1)).thenReturn(Optional.of(estudiante));

        Estudiante result = service.findById(1);

        assertThat(result).isEqualTo(estudiante);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("ID NOT FOUND: 999");
    }

    @Test
    void updateReturnsUpdatedEstudianteWhenPresent() throws Exception {
        Estudiante updated = new Estudiante(null, "Jaime Actualizado", "Medina", "12345678", 33);
        when(repo.findById(1)).thenReturn(Optional.of(estudiante));
        when(repo.save(updated)).thenReturn(updated);

        Estudiante result = service.update(1, updated);

        assertThat(result.getIdEstudiante()).isEqualTo(1);
        assertThat(result.getNombres()).isEqualTo("Jaime Actualizado");
        verify(repo, times(1)).save(updated);
    }

    @Test
    void findAllOrderByEdadDescReturnsSortedList() {
        Estudiante est1 = new Estudiante(1, "Ana", "Gomez", "11111111", 21);
        Estudiante est2 = new Estudiante(2, "Jaime", "Medina", "22222222", 35);
        Estudiante est3 = new Estudiante(3, "Luis", "Perez", "33333333", 28);
        when(repo.findAll()).thenReturn(List.of(est1, est2, est3));

        List<Estudiante> result = service.findAllOrderByEdadDesc();

        assertThat(result).containsExactly(est2, est3, est1);
    }
}
