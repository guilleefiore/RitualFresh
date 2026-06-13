package com.ritualfresh.profiles.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CrearPerfilTrabajadorApiRequest {
    private String urlFotoPerfil;

    @NotBlank
    private String descripcion;

    @NotNull
    @Min(0)
    private Integer aniosExperiencia;

    @NotBlank
    private String serviciosOfrecidos;

    @NotBlank
    private String zonaTrabajo;

    @NotBlank
    private String disponibilidad;

    @NotNull
    @Positive
    private BigDecimal precioHoraOrientativo;
}
