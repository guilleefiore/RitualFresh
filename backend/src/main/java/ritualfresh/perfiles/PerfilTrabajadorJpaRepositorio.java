package ritualfresh.perfiles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilTrabajadorJpaRepositorio extends JpaRepository<PerfilTrabajador, Long> {
    Optional<PerfilTrabajador> findByUsuario_IdUsuario(Long idUsuario);

    boolean existsByUsuario_IdUsuario(Long idUsuario);
}
