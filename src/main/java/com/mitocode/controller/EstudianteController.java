package com.mitocode.controller;

import com.mitocode.dto.EstudianteDTO;
import com.mitocode.model.Estudiante;
import com.mitocode.service.IEstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final IEstudianteService service;

    @Qualifier("defaultMapper")
    private final ModelMapper defaultMapper;

    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> findAll() {
        List<EstudianteDTO> list = service.findAll().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteDTO> findById(@PathVariable Integer id) {
        Estudiante obj = service.findById(id);
        return ResponseEntity.ok(toDTO(obj));
    }

    @PostMapping
    public ResponseEntity<EstudianteDTO> save(@Valid @RequestBody EstudianteDTO dto) {
        Estudiante obj = service.save(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteDTO> update(@PathVariable Integer id, @Valid @RequestBody EstudianteDTO dto) throws Exception {
        Estudiante obj = service.update(id, toEntity(dto));
        return ResponseEntity.ok(toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Listar estudiantes ordenados de forma descendente por edad usando programacion funcional
    @GetMapping("/ordenados-edad")
    public ResponseEntity<List<EstudianteDTO>> findAllOrderByEdadDesc() {
        List<EstudianteDTO> list = service.findAllOrderByEdadDesc().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    private EstudianteDTO toDTO(Estudiante obj) {
        return defaultMapper.map(obj, EstudianteDTO.class);
    }

    private Estudiante toEntity(EstudianteDTO dto) {
        return defaultMapper.map(dto, Estudiante.class);
    }
}
