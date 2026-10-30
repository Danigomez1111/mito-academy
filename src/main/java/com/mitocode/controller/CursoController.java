package com.mitocode.controller;

import com.mitocode.dto.CursoDTO;
import com.mitocode.model.Curso;
import com.mitocode.service.ICursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService service;

    @Qualifier("defaultMapper")
    private final ModelMapper defaultMapper;

    @GetMapping
    public ResponseEntity<List<CursoDTO>> findAll() {
        List<CursoDTO> list = service.findAll().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> findById(@PathVariable Integer id) {
        Curso obj = service.findById(id);
        return ResponseEntity.ok(toDTO(obj));
    }

    @PostMapping
    public ResponseEntity<CursoDTO> save(@Valid @RequestBody CursoDTO dto) {
        Curso obj = service.save(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> update(@PathVariable Integer id, @Valid @RequestBody CursoDTO dto) throws Exception {
        Curso obj = service.update(id, toEntity(dto));
        return ResponseEntity.ok(toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CursoDTO toDTO(Curso obj) {
        return defaultMapper.map(obj, CursoDTO.class);
    }

    private Curso toEntity(CursoDTO dto) {
        return defaultMapper.map(dto, Curso.class);
    }
}
