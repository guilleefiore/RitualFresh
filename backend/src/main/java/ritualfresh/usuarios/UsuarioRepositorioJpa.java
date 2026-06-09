package ritualfresh.usuarios;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositorioJpa implements UsuarioRepositorio {
    private final UsuarioJpaRepositorio usuarioJpaRepositorio;

    public UsuarioRepositorioJpa(UsuarioJpaRepositorio usuarioJpaRepositorio) {
        this.usuarioJpaRepositorio = usuarioJpaRepositorio;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioJpaRepositorio.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long idUsuario) {
        if (idUsuario == null) {
            return Optional.empty();
        }

        return usuarioJpaRepositorio.findById(idUsuario);
    }

    @Override
    public Optional<Usuario> buscarPorMail(String mail) {
        return usuarioJpaRepositorio.findByMail(normalizarMail(mail));
    }

    @Override
    public Optional<Usuario> buscarPorTokenValidacionCuenta(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuarioJpaRepositorio.findByTokenValidacionCuenta(token);
    }

    @Override
    public Optional<Usuario> buscarPorTokenRecuperacionContrasena(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuarioJpaRepositorio.findByTokenRecuperacionContrasena(token);
    }

    @Override
    public boolean existeMail(String mail) {
        return usuarioJpaRepositorio.existsByMail(normalizarMail(mail));
    }

    private String normalizarMail(String mail) {
        return mail == null ? "" : mail.trim().toLowerCase();
    }
}
