package com.ritualfresh.profiles;

import java.util.Optional;

public interface PerfilClienteRepositorio {
    PerfilCliente guardar(PerfilCliente perfil);

    Optional<PerfilCliente> buscarPorUsuarioId(Long idUsuario);

    boolean existePorUsuarioId(Long idUsuario);
}
