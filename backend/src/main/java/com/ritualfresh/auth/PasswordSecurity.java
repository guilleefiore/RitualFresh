package com.ritualfresh.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordSecurity {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private PasswordSecurity() {
    }

    public static String generateHash(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    public static boolean matches(String rawPassword, String passwordHash) {
        return PASSWORD_ENCODER.matches(rawPassword, passwordHash);
    }
}
