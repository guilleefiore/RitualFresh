package com.ritualfresh.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultadoRegistroUsuario {
    private final Usuario usuario;
    private final String mensaje;
    private final String tokenValidacionCuenta;
}
