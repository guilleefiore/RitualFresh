package com.ritualfresh.notifications.service;

import com.ritualfresh.auth.model.User;

import java.time.LocalDateTime;

    // Puerto simple para el envio de correos de cuenta.
    // Permite desacoplar la logica de negocio del detalle SMTP.
public interface AccountEmailService {
    void sendAccountValidationEmail(User user, String accountValidationToken, LocalDateTime expiresAt);

    void sendPasswordResetEmail(User user, String resetToken, LocalDateTime expiresAt);
}
