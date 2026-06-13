package com.ritualfresh.auth.controller;

import com.ritualfresh.auth.RolUsuario;

public enum RolRegistroUsuario {
    CLIENTE,
    TRABAJADOR;

    public RolUsuario toRolUsuario() {
        return RolUsuario.valueOf(name());
    }
}
