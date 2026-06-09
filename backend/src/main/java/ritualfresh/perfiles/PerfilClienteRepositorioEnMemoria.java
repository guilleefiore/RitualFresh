package ritualfresh.perfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PerfilClienteRepositorioEnMemoria implements PerfilClienteRepositorio {
    private final Map<Long, PerfilCliente> perfilesPorUsuarioId = new HashMap<>();
    private final AtomicLong secuenciaIds = new AtomicLong(1);

    @Override
    public PerfilCliente guardar(PerfilCliente perfil) {
        perfil.asignarIdSiNoTiene(secuenciaIds.getAndIncrement());
        perfilesPorUsuarioId.put(perfil.getUsuario().getIdUsuario(), perfil);
        return perfil;
    }

    @Override
    public Optional<PerfilCliente> buscarPorUsuarioId(Long idUsuario) {
        return Optional.ofNullable(perfilesPorUsuarioId.get(idUsuario));
    }

    @Override
    public boolean existePorUsuarioId(Long idUsuario) {
        return perfilesPorUsuarioId.containsKey(idUsuario);
    }
}
