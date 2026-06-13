package com.ritualfresh.auth;

import java.util.Optional;

public interface SesionUsuarioRepositorio {
    SesionUsuario guardar(SesionUsuario sesionUsuario);

    Optional<SesionUsuario> buscarPorToken(String token);
}
