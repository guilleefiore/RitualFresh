package com.ritualfresh.notifications.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.notifications.config.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Fallback para desarrollo y tests manuales.
// Si SMTP no esta habilitado, deja los datos del correo en logs sin romper el flujo.
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ritualfresh.mail", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LoggingAccountEmailService implements AccountEmailService {
    private final MailProperties mailProperties;

    @Override
    public void sendAccountValidationEmail(User user, String accountValidationToken, LocalDateTime expiresAt) {
        log.info(
                "Correo de validacion no enviado por SMTP. destinatario={}, enlace={}/api/users/validation?token={}, expira={}",
                user.getEmail(),
                mailProperties.getBackendBaseUrl(),
                accountValidationToken,
                expiresAt);
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken, LocalDateTime expiresAt) {
        log.info(
                "Correo de recuperacion no enviado por SMTP. destinatario={}, enlace={}/password-reset/confirm?token={}, expira={}",
                user.getEmail(),
                mailProperties.getFrontendBaseUrl(),
                resetToken,
                expiresAt);
    }
}
