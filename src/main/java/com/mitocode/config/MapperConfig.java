package com.mitocode.config;

import com.mitocode.dto.MatriculaDTO;
import com.mitocode.model.Matricula;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    @Bean
    public ModelMapper matriculaMapper() {
        ModelMapper mapper = new ModelMapper();

        // Matricula.detalleMatricula es el lado inverso (mappedBy = "matricula"); cada DetalleMatricula
        // debe llevar la referencia a su Matricula padre para que la FK id_matricula no se inserte como null.
        mapper.createTypeMap(MatriculaDTO.class, Matricula.class)
                .setPostConverter(matriculaBackReferenceConverter());

        return mapper;
    }

    private Converter<MatriculaDTO, Matricula> matriculaBackReferenceConverter() {
        return ctx -> {
            Matricula matricula = ctx.getDestination();
            if (matricula.getDetalleMatricula() != null) {
                matricula.getDetalleMatricula().forEach(det -> det.setMatricula(matricula));
            }
            return matricula;
        };
    }
}
