package com.mitocode.controller;

import com.mitocode.dto.CursoDTO;
import com.mitocode.dto.DetalleMatriculaDTO;
import com.mitocode.dto.EstudianteDTO;
import com.mitocode.dto.MatriculaDTO;
import com.mitocode.exception.GlobalErrorHandler;
import com.mitocode.model.Curso;
import com.mitocode.model.DetalleMatricula;
import com.mitocode.model.Estudiante;
import com.mitocode.model.Matricula;
import com.mitocode.service.IMatriculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MatriculaControllerTest {

    private MockMvc mockMvc;
    private IMatriculaService service;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private Matricula matricula;

    @BeforeEach
    void init() {
        service = mock(IMatriculaService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();

        modelMapper.createTypeMap(MatriculaDTO.class, Matricula.class)
                .setPostConverter(ctx -> {
                    Matricula dest = ctx.getDestination();
                    if (dest.getDetalleMatricula() != null) {
                        dest.getDetalleMatricula().forEach(det -> det.setMatricula(dest));
                    }
                    return dest;
                });

        mockMvc = MockMvcBuilders
                .standaloneSetup(new MatriculaController(service, modelMapper))
                .setControllerAdvice(new GlobalErrorHandler())
                .build();

        Estudiante est = new Estudiante(1, "Jaime", "Medina", "12345678", 32);
        Curso cur = new Curso(1, "Programación", "PROG", true);
        matricula = new Matricula(1, LocalDateTime.of(2026, 10, 23, 10, 0), est, null, true);
        DetalleMatricula det = new DetalleMatricula(1, matricula, cur, "A-101");
        matricula.setDetalleMatricula(List.of(det));
    }

    @Test
    void findAllReturnsMappedMatriculas() throws Exception {
        when(service.findAll()).thenReturn(List.of(matricula));

        mockMvc.perform(get("/v1/matriculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idMatricula").value(1))
                .andExpect(jsonPath("$[0].detalleMatricula", hasSize(1)))
                .andExpect(jsonPath("$[0].detalleMatricula[0].aula").value("A-101"));

        verify(service, times(1)).findAll();
    }

    @Test
    void saveReturnsCreatedMatricula() throws Exception {
        EstudianteDTO estDTO = new EstudianteDTO(1, "Jaime", "Medina", "12345678", 32);
        CursoDTO curDTO = new CursoDTO(1, "Programación", "PROG", true);
        DetalleMatriculaDTO detDTO = new DetalleMatriculaDTO(null, curDTO, "A-101");
        MatriculaDTO inputDTO = new MatriculaDTO(null, LocalDateTime.of(2026, 10, 23, 10, 0), estDTO, List.of(detDTO), true);

        when(service.save(any(Matricula.class))).thenReturn(matricula);

        mockMvc.perform(post("/v1/matriculas")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMatricula").value(1));

        verify(service, times(1)).save(any(Matricula.class));
    }

    @Test
    void saveReturnsBadRequestWhenDetalleIsEmpty() throws Exception {
        EstudianteDTO estDTO = new EstudianteDTO(1, "Jaime", "Medina", "12345678", 32);
        MatriculaDTO invalidDTO = new MatriculaDTO(null, LocalDateTime.of(2026, 10, 23, 10, 0), estDTO, List.of(), true);

        mockMvc.perform(post("/v1/matriculas")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(Matricula.class));
    }

    @Test
    void getCursosConEstudiantesReturnsMap() throws Exception {
        Map<String, List<String>> expectedMap = Map.of(
                "Programación", List.of("Jaime Medina"),
                "Base de Datos", List.of("Mito X", "Code Y")
        );
        when(service.getCursosConEstudiantes()).thenReturn(expectedMap);

        mockMvc.perform(get("/v1/matriculas/cursos-estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Programación']", hasSize(1)))
                .andExpect(jsonPath("$.['Programación'][0]").value("Jaime Medina"))
                .andExpect(jsonPath("$.['Base de Datos']", hasSize(2)))
                .andExpect(jsonPath("$.['Base de Datos'][0]").value("Mito X"))
                .andExpect(jsonPath("$.['Base de Datos'][1]").value("Code Y"));

        verify(service, times(1)).getCursosConEstudiantes();
    }
}
