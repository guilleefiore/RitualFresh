package com.ritualfresh.auth.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Clase utilitaria para trabajar con contrasenas de forma segura.
// Usa BCrypt para generar hashes y validar contrasenas.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordSecurity {
    // Instancia reutilizable del encoder de contrasenas.
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // Convierte una contrasena en texto plano a su hash seguro.
    public static String generateHash(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    // Verifica si la contrasena ingresada coincide con el hash guardado.
    public static boolean matches(String rawPassword, String passwordHash) {
        return PASSWORD_ENCODER.matches(rawPassword, passwordHash);
    }
}
