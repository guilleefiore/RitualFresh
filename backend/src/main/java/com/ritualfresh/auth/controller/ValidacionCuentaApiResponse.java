package com.ritualfresh.auth.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidacionCuentaApiResponse {
    private final String mensaje;
    private final UsuarioApiResponse usuario;
}
