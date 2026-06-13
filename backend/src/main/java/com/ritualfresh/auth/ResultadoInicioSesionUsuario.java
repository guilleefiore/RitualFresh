package com.ritualfresh.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResultadoInicioSesionUsuario {
    private final Usuario usuario;
    private final String tokenSesion;
    private final LocalDateTime fechaHoraExpiracionSesion;
}
