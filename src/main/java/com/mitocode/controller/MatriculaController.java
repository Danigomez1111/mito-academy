package com.mitocode.controller;

import com.mitocode.dto.MatriculaDTO;
import com.mitocode.model.Matricula;
import com.mitocode.service.IMatriculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final IMatriculaService service;

    @Qualifier("matriculaMapper")
    private final ModelMapper matriculaMapper;

    @GetMapping
    public ResponseEntity<List<MatriculaDTO>> findAll() {
        List<MatriculaDTO> list = service.findAll().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaDTO> findById(@PathVariable Integer id) {
        Matricula obj = service.findById(id);
        return ResponseEntity.ok(toDTO(obj));
    }

    @PostMapping
    public ResponseEntity<MatriculaDTO> save(@Valid @RequestBody MatriculaDTO dto) {
        Matricula obj = service.save(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatriculaDTO> update(@PathVariable Integer id, @Valid @RequestBody MatriculaDTO dto) throws Exception {
        Matricula obj = service.update(id, toEntity(dto));
        return ResponseEntity.ok(toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Mostrar la relacion de cursos matriculados y sus estudiantes correspondientes usando programacion funcional (Map)
    @GetMapping("/cursos-estudiantes")
    public ResponseEntity<Map<String, List<String>>> getCursosConEstudiantes() {
        return ResponseEntity.ok(service.getCursosConEstudiantes());
    }

    private MatriculaDTO toDTO(Matricula obj) {
        return matriculaMapper.map(obj, MatriculaDTO.class);
    }

    private Matricula toEntity(MatriculaDTO dto) {
        return matriculaMapper.map(dto, Matricula.class);
    }
}
