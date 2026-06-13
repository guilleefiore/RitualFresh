package com.ritualfresh.profiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PerfilTrabajadorRepositorioEnMemoria implements PerfilTrabajadorRepositorio {
    private final Map<Long, PerfilTrabajador> perfilesPorUsuarioId = new HashMap<>();
    private final AtomicLong secuenciaIds = new AtomicLong(1);

    @Override
    public PerfilTrabajador guardar(PerfilTrabajador perfil) {
        perfil.asignarIdSiNoTiene(secuenciaIds.getAndIncrement());
        perfilesPorUsuarioId.put(perfil.getUsuario().getIdUsuario(), perfil);
        return perfil;
    }

    @Override
    public Optional<PerfilTrabajador> buscarPorUsuarioId(Long idUsuario) {
        return Optional.ofNullable(perfilesPorUsuarioId.get(idUsuario));
    }

    @Override
    public boolean existePorUsuarioId(Long idUsuario) {
        return perfilesPorUsuarioId.containsKey(idUsuario);
    }
}
