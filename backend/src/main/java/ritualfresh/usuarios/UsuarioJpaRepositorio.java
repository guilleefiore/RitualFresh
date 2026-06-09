package ritualfresh.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMail(String mail);

    Optional<Usuario> findByTokenValidacionCuenta(String tokenValidacionCuenta);

    Optional<Usuario> findByTokenRecuperacionContrasena(String tokenRecuperacionContrasena);

    boolean existsByMail(String mail);
}
