package com.ritualfresh.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SeguridadContrasena {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private SeguridadContrasena() {
    }

    public static String generarHash(String contrasena) {
        return PASSWORD_ENCODER.encode(contrasena);
    }

    public static boolean coincide(String contrasenaIngresada, String contrasenaHash) {
        return PASSWORD_ENCODER.matches(contrasenaIngresada, contrasenaHash);
    }
}
