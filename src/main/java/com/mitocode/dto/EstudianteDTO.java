package com.mitocode.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstudianteDTO {

    private Integer idEstudiante;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 100)
    private String nombres;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 100)
    private String apellidos;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 10)
    private String dni;

    @NotNull
    @Min(1)
    @Max(120)
    private Integer edad;
}
