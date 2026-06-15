package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
// Adaptador que implementa el puerto UserRepository usando Spring Data JPA.
public class JpaUserRepository implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    // Guarda un usuario nuevo o actualiza uno existente.
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    // Devuelve todos los usuarios persistidos.
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    // Busca por id y evita consultar si el id llega nulo.
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return userJpaRepository.findById(id);
    }

    @Override
    // Busca por email después de normalizarlo para mantener consistencia.
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(normalizeEmail(email));
    }

    @Override
    // Busca por token de validación y corta antes si el valor no sirve.
    public Optional<User> findByAccountValidationToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userJpaRepository.findByAccountValidationToken(token);
    }

    @Override
    // Busca por token de reseteo y evita consultas con tokens vacíos.
    public Optional<User> findByPasswordResetToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userJpaRepository.findByPasswordResetToken(token);
    }

    @Override
    // Verifica existencia por email usando el mismo criterio de normalización.
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(normalizeEmail(email));
    }

    // Unifica el formato del email para evitar diferencias por espacios o mayúsculas.
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
