package com.ritualfresh.profiles;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PerfilClienteRepositorioJpa implements PerfilClienteRepositorio {
    private final PerfilClienteJpaRepositorio perfilClienteJpaRepositorio;

    public PerfilClienteRepositorioJpa(PerfilClienteJpaRepositorio perfilClienteJpaRepositorio) {
        this.perfilClienteJpaRepositorio = perfilClienteJpaRepositorio;
    }

    @Override
    public PerfilCliente guardar(PerfilCliente perfil) {
        return perfilClienteJpaRepositorio.save(perfil);
    }

    @Override
    public Optional<PerfilCliente> buscarPorUsuarioId(Long idUsuario) {
        if (idUsuario == null) {
            return Optional.empty();
        }

        return perfilClienteJpaRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public boolean existePorUsuarioId(Long idUsuario) {
        return idUsuario != null && perfilClienteJpaRepositorio.existsByUsuario_IdUsuario(idUsuario);
    }
}
