package com.ritualfresh.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ritualfresh.auth.model.User;

import java.util.Optional;

// Repositorio técnico de Spring Data JPA para la entidad User.
public interface UserJpaRepository extends JpaRepository<User, Long> {
    // Busca un usuario por su email.
    Optional<User> findByEmail(String email);

    // Busca un usuario por el token usado para validar la cuenta.
    Optional<User> findByAccountValidationToken(String tokenValidationToken);

    // Busca un usuario por el token usado para resetear la contraseña.
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    // Verifica si ya existe un usuario con el email indicado.
    boolean existsByEmail(String email);
}
