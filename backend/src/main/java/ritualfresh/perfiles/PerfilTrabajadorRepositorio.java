package ritualfresh.perfiles;

import java.util.Optional;

public interface PerfilTrabajadorRepositorio {
    PerfilTrabajador guardar(PerfilTrabajador perfil);

    Optional<PerfilTrabajador> buscarPorUsuarioId(Long idUsuario);

    boolean existePorUsuarioId(Long idUsuario);
}
