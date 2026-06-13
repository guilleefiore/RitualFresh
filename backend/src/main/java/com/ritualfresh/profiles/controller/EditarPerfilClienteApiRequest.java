package com.ritualfresh.profiles.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditarPerfilClienteApiRequest {
    private String urlFotoPerfil;

    @NotBlank
    private String telefonoContacto;

    @NotBlank
    private String nombreCalle;

    @NotBlank
    private String numeroDomicilio;

    private String piso;

    private String departamentoDomicilio;

    @NotBlank
    private String codigoPostal;

    @NotBlank
    private String localidad;

    @NotBlank
    private String provincia;

    @NotBlank
    private String preferenciasContratacion;
}
