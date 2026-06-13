package com.ritualfresh.auth;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SesionUsuarioRepositorioJpa implements SesionUsuarioRepositorio {
    private final SesionUsuarioJpaRepositorio sesionUsuarioJpaRepositorio;

    public SesionUsuarioRepositorioJpa(SesionUsuarioJpaRepositorio sesionUsuarioJpaRepositorio) {
        this.sesionUsuarioJpaRepositorio = sesionUsuarioJpaRepositorio;
    }

    @Override
    public SesionUsuario guardar(SesionUsuario sesionUsuario) {
        return sesionUsuarioJpaRepositorio.save(sesionUsuario);
    }

    @Override
    public Optional<SesionUsuario> buscarPorToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return sesionUsuarioJpaRepositorio.findByToken(token.trim());
    }
}
