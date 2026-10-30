package com.mitocode.controller;

import com.mitocode.dto.EstudianteDTO;
import com.mitocode.exception.GlobalErrorHandler;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Estudiante;
import com.mitocode.service.IEstudianteService;
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

class EstudianteControllerTest {

    private MockMvc mockMvc;
    private IEstudianteService service;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private Estudiante estudiante;

    @BeforeEach
    void init() {
        service = mock(IEstudianteService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new EstudianteController(service, modelMapper))
                .setControllerAdvice(new GlobalErrorHandler())
                .build();

        estudiante = new Estudiante(1, "Jaime", "Medina", "12345678", 32);
    }

    @Test
    void findAllReturnsMappedEstudiantes() throws Exception {
        when(service.findAll()).thenReturn(List.of(estudiante));

        mockMvc.perform(get("/v1/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idEstudiante").value(1))
                .andExpect(jsonPath("$[0].nombres").value("Jaime"))
                .andExpect(jsonPath("$[0].edad").value(32));

        verify(service, times(1)).findAll();
    }

    @Test
    void findByIdReturnsMappedEstudiante() throws Exception {
        when(service.findById(1)).thenReturn(estudiante);

        mockMvc.perform(get("/v1/estudiantes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstudiante").value(1))
                .andExpect(jsonPath("$.dni").value("12345678"));

        verify(service, times(1)).findById(1);
    }

    @Test
    void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
        when(service.findById(999)).thenThrow(new ModelNotFoundException("ID NOT FOUND: 999"));

        mockMvc.perform(get("/v1/estudiantes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ID NOT FOUND: 999"));

        verify(service, times(1)).findById(999);
    }

    @Test
    void saveReturnsCreatedEstudiante() throws Exception {
        EstudianteDTO inputDTO = new EstudianteDTO(null, "Jaime", "Medina", "12345678", 32);
        when(service.save(any(Estudiante.class))).thenReturn(estudiante);

        mockMvc.perform(post("/v1/estudiantes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEstudiante").value(1))
                .andExpect(jsonPath("$.nombres").value("Jaime"));

        verify(service, times(1)).save(any(Estudiante.class));
    }

    @Test
    void saveReturnsBadRequestWhenDniIsTooShort() throws Exception {
        EstudianteDTO invalidDTO = new EstudianteDTO(null, "Jaime", "Medina", "123", 32);

        mockMvc.perform(post("/v1/estudiantes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(Estudiante.class));
    }

    @Test
    void saveReturnsBadRequestWhenEdadIsZero() throws Exception {
        EstudianteDTO invalidDTO = new EstudianteDTO(null, "Jaime", "Medina", "12345678", 0);

        mockMvc.perform(post("/v1/estudiantes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(Estudiante.class));
    }

    @Test
    void updateReturnsUpdatedEstudiante() throws Exception {
        Estudiante updated = new Estudiante(1, "Jaime Actualizado", "Medina", "12345678", 33);
        EstudianteDTO updateDTO = new EstudianteDTO(1, "Jaime Actualizado", "Medina", "12345678", 33);
        when(service.update(anyInt(), any(Estudiante.class))).thenReturn(updated);

        mockMvc.perform(put("/v1/estudiantes/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstudiante").value(1))
                .andExpect(jsonPath("$.nombres").value("Jaime Actualizado"))
                .andExpect(jsonPath("$.edad").value(33));

        verify(service, times(1)).update(anyInt(), any(Estudiante.class));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/v1/estudiantes/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1);
    }

    @Test
    void findAllOrderByEdadDescReturnsSortedEstudiantes() throws Exception {
        Estudiante mayor = new Estudiante(2, "Carlos", "Lopez", "87654321", 40);
        Estudiante menor = new Estudiante(1, "Jaime", "Medina", "12345678", 32);
        when(service.findAllOrderByEdadDesc()).thenReturn(List.of(mayor, menor));

        mockMvc.perform(get("/v1/estudiantes/ordenados-edad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombres").value("Carlos"))
                .andExpect(jsonPath("$[0].edad").value(40))
                .andExpect(jsonPath("$[1].nombres").value("Jaime"))
                .andExpect(jsonPath("$[1].edad").value(32));

        verify(service, times(1)).findAllOrderByEdadDesc();
    }
}
