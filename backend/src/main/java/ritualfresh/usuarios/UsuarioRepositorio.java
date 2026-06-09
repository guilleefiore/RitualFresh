package ritualfresh.usuarios;

import java.util.Optional;

public interface UsuarioRepositorio {
    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long idUsuario);

    Optional<Usuario> buscarPorMail(String mail);

    Optional<Usuario> buscarPorTokenValidacionCuenta(String token);

    Optional<Usuario> buscarPorTokenRecuperacionContrasena(String token);

    boolean existeMail(String mail);
}
