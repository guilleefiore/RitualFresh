package com.ritualfresh.profiles;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditarPerfilClienteRequest {
    private final String urlFotoPerfil;
    private final String telefonoContacto;
    private final String nombreCalle;
    private final String numeroDomicilio;
    private final String piso;
    private final String departamentoDomicilio;
    private final String codigoPostal;
    private final String localidad;
    private final String provincia;
    private final String preferenciasContratacion;
}
