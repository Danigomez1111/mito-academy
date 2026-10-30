package com.mitocode.controller;

import com.mitocode.dto.CursoDTO;
import com.mitocode.exception.GlobalErrorHandler;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Curso;
import com.mitocode.service.ICursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CursoControllerTest {

    private MockMvc mockMvc;
    private ICursoService service;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private Curso curso;

    @BeforeEach
    void init() {
        service = mock(ICursoService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CursoController(service, modelMapper))
                .setControllerAdvice(new GlobalErrorHandler())
                .build();

        curso = new Curso(1, "Programación", "PROG", true);
    }

    @Test
    void findAllReturnsMappedCursos() throws Exception {
        when(service.findAll()).thenReturn(List.of(curso));

        mockMvc.perform(get("/v1/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCurso").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Programación"))
                .andExpect(jsonPath("$[0].siglas").value("PROG"));

        verify(service, times(1)).findAll();
    }

    @Test
    void findByIdReturnsMappedCurso() throws Exception {
        when(service.findById(1)).thenReturn(curso);

        mockMvc.perform(get("/v1/cursos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCurso").value(1))
                .andExpect(jsonPath("$.nombre").value("Programación"));

        verify(service, times(1)).findById(1);
    }

    @Test
    void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
        when(service.findById(999)).thenThrow(new ModelNotFoundException("ID NOT FOUND: 999"));

        mockMvc.perform(get("/v1/cursos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ID NOT FOUND: 999"));

        verify(service, times(1)).findById(999);
    }

    @Test
    void saveReturnsCreatedCurso() throws Exception {
        CursoDTO inputDTO = new CursoDTO(null, "Programación", "PROG", true);
        when(service.save(any(Curso.class))).thenReturn(curso);

        mockMvc.perform(post("/v1/cursos")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCurso").value(1))
                .andExpect(jsonPath("$.siglas").value("PROG"));

        verify(service, times(1)).save(any(Curso.class));
    }

    @Test
    void saveReturnsBadRequestWhenSiglasIsInvalid() throws Exception {
        CursoDTO invalidDTO = new CursoDTO(null, "Programación", "P", true);

        mockMvc.perform(post("/v1/cursos")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(Curso.class));
    }

    @Test
    void updateReturnsUpdatedCurso() throws Exception {
        Curso updated = new Curso(1, "Programación Avanzada", "PROGA", false);
        CursoDTO updateDTO = new CursoDTO(1, "Programación Avanzada", "PROGA", false);
        when(service.update(anyInt(), any(Curso.class))).thenReturn(updated);

        mockMvc.perform(put("/v1/cursos/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Programación Avanzada"))
                .andExpect(jsonPath("$.estado").value(false));

        verify(service, times(1)).update(anyInt(), any(Curso.class));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/v1/cursos/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1);
    }
}
