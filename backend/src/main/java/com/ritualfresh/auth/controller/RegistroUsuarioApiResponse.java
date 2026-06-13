package com.ritualfresh.auth.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistroUsuarioApiResponse {
    private final String mensaje;
    private final String tokenValidacionCuenta;
    private final UsuarioApiResponse usuario;
}
