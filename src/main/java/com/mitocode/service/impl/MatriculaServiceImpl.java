package com.mitocode.service.impl;

import com.mitocode.model.DetalleMatricula;
import com.mitocode.model.Matricula;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IMatriculaRepo;
import com.mitocode.service.IMatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class MatriculaServiceImpl extends CRUDImpl<Matricula, Integer> implements IMatriculaService {

    private final IMatriculaRepo repo;

    @Override
    protected IGenericRepo<Matricula, Integer> getRepo() {
        return repo;
    }

    @Override
    public Map<String, List<String>> getCursosConEstudiantes() {
        Stream<Matricula> matriculaStream = repo.findAll().stream();
        Stream<List<DetalleMatricula>> lsStream = matriculaStream.map(Matricula::getDetalleMatricula);

        Stream<DetalleMatricula> streamDetail = lsStream.flatMap(Collection::stream);

        return streamDetail.collect(
                groupingBy(
                        d -> d.getCurso().getNombre(),
                        mapping(
                                d -> d.getMatricula().getEstudiante().getNombres() + " " + d.getMatricula().getEstudiante().getApellidos(),
                                toList()
                        )
                )
        );
    }
}
