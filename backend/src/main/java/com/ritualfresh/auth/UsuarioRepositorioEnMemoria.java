package com.ritualfresh.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class UsuarioRepositorioEnMemoria implements UsuarioRepositorio {
    private final Map<String, Usuario> usuariosPorMail = new HashMap<>();
    private final Map<Long, Usuario> usuariosPorId = new HashMap<>();
    private final AtomicLong secuenciaIds = new AtomicLong(1);

    @Override
    public Usuario guardar(Usuario usuario) {
        usuario.asignarIdSiNoTiene(secuenciaIds.getAndIncrement());
        usuariosPorMail.put(normalizarMail(usuario.getMail()), usuario);
        usuariosPorId.put(usuario.getIdUsuario(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long idUsuario) {
        return Optional.ofNullable(usuariosPorId.get(idUsuario));
    }

    @Override
    public Optional<Usuario> buscarPorMail(String mail) {
        return Optional.ofNullable(usuariosPorMail.get(normalizarMail(mail)));
    }

    @Override
    public Optional<Usuario> buscarPorTokenValidacionCuenta(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuariosPorMail.values().stream()
                .filter(usuario -> token.equals(usuario.getTokenValidacionCuenta()))
                .findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorTokenRecuperacionContrasena(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuariosPorMail.values().stream()
                .filter(usuario -> token.equals(usuario.getTokenRecuperacionContrasena()))
                .findFirst();
    }

    @Override
    public boolean existeMail(String mail) {
        return usuariosPorMail.containsKey(normalizarMail(mail));
    }

    private String normalizarMail(String mail) {
        return mail == null ? "" : mail.trim().toLowerCase();
    }
}
