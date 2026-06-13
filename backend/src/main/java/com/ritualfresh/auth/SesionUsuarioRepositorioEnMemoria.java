package com.ritualfresh.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class SesionUsuarioRepositorioEnMemoria implements SesionUsuarioRepositorio {
    private final Map<String, SesionUsuario> sesionesPorToken = new HashMap<>();
    private final AtomicLong secuenciaIds = new AtomicLong(1);

    @Override
    public SesionUsuario guardar(SesionUsuario sesionUsuario) {
        sesionUsuario.asignarIdSiNoTiene(secuenciaIds.getAndIncrement());
        sesionesPorToken.put(sesionUsuario.getToken(), sesionUsuario);
        return sesionUsuario;
    }

    @Override
    public Optional<SesionUsuario> buscarPorToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(sesionesPorToken.get(token.trim()));
    }
}
