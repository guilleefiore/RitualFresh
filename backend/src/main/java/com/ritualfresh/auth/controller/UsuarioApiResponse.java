package com.ritualfresh.auth.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.ritualfresh.auth.EstadoCuenta;
import com.ritualfresh.auth.RolUsuario;
import com.ritualfresh.auth.Usuario;

@Getter
@AllArgsConstructor
public class UsuarioApiResponse {
    private final Long idUsuario;
    private final String nombre;
    private final String apellido;
    private final String mail;
    private final RolUsuario rol;
    private final EstadoCuenta estadoCuenta;

    public static UsuarioApiResponse desde(Usuario usuario) {
        return new UsuarioApiResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getMail(),
                usuario.getRol(),
                usuario.getEstadoCuenta());
    }
}
