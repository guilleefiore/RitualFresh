package ritualfresh.perfiles;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PerfilTrabajadorRepositorioJpa implements PerfilTrabajadorRepositorio {
    private final PerfilTrabajadorJpaRepositorio perfilTrabajadorJpaRepositorio;

    public PerfilTrabajadorRepositorioJpa(PerfilTrabajadorJpaRepositorio perfilTrabajadorJpaRepositorio) {
        this.perfilTrabajadorJpaRepositorio = perfilTrabajadorJpaRepositorio;
    }

    @Override
    public PerfilTrabajador guardar(PerfilTrabajador perfil) {
        return perfilTrabajadorJpaRepositorio.save(perfil);
    }

    @Override
    public Optional<PerfilTrabajador> buscarPorUsuarioId(Long idUsuario) {
        if (idUsuario == null) {
            return Optional.empty();
        }

        return perfilTrabajadorJpaRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public boolean existePorUsuarioId(Long idUsuario) {
        return idUsuario != null && perfilTrabajadorJpaRepositorio.existsByUsuario_IdUsuario(idUsuario);
    }
}
