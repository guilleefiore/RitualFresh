package com.ritualfresh.shared.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.LocalDateTime;

// Utilidad central para emitir y leer la cookie de sesion del backend.
// En desarrollo local se deja secure=false para que funcione sobre http://localhost.
public final class SessionCookieUtils {
    public static final String SESSION_COOKIE_NAME = "RITUALFRESH_SESSION";

    private SessionCookieUtils() {
    }

    public static String buildSessionCookieHeader(String sessionToken, LocalDateTime expiresAt) {
        return buildSessionCookieHeader(sessionToken, expiresAt, false, "Lax");
    }

    public static String buildSessionCookieHeader(
            String sessionToken,
            LocalDateTime expiresAt,
            boolean secure,
            String sameSite) {
        Duration maxAge = Duration.between(LocalDateTime.now(), expiresAt);
        if (maxAge.isNegative()) {
            maxAge = Duration.ZERO;
        }

        return ResponseCookie.from(SESSION_COOKIE_NAME, sessionToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(normalizeSameSite(sameSite))
                .path("/")
                .maxAge(maxAge)
                .build()
                .toString();
    }

    public static String buildExpiredSessionCookieHeader() {
        return buildExpiredSessionCookieHeader(false, "Lax");
    }

    public static String buildExpiredSessionCookieHeader(boolean secure, String sameSite) {
        return ResponseCookie.from(SESSION_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(normalizeSameSite(sameSite))
                .path("/")
                .maxAge(Duration.ZERO)
                .build()
                .toString();
    }

    public static String resolveSessionTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                String value = cookie.getValue();
                return value == null || value.isBlank() ? null : value.trim();
            }
        }

        return null;
    }

    private static String normalizeSameSite(String sameSite) {
        return sameSite == null || sameSite.isBlank() ? "Lax" : sameSite.trim();
    }
}
