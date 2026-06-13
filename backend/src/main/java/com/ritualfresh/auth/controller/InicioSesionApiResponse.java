package com.ritualfresh.auth.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InicioSesionApiResponse {
    private final String mensaje;
    private final String tokenSesion;
    private final LocalDateTime fechaHoraExpiracionSesion;
    private final UsuarioApiResponse usuario;
}
