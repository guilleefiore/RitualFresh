package com.ritualfresh.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InicioSesionRequest {
    private final String mail;
    private final String contrasena;
}
