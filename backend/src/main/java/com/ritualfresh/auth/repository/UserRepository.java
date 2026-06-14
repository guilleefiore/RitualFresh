package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;

import java.util.List;
import java.util.Optional;
// Define las operaciones de acceso a datos para usuarios del sistema.
public interface UserRepository {
    // Guarda un usuario nuevo o actualiza uno existente.
    User save(User user);

    // Devuelve todos los usuarios registrados.
    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountValidationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    // Verifica si ya existe un usuario con el correo indicado.
    boolean existsByEmail(String email);
}
