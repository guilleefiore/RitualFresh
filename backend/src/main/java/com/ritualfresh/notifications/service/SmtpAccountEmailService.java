package com.ritualfresh.notifications.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.notifications.config.MailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Implementacion SMTP del envio de correos.
// Se activa solo cuando el entorno habilita el envio real.
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ritualfresh.mail", name = "enabled", havingValue = "true")
public class SmtpAccountEmailService implements AccountEmailService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    // Envia el correo con el link para validar la cuenta recien creada.
    public void sendAccountValidationEmail(User user, String accountValidationToken, LocalDateTime expiresAt) {
        send(
                user.getEmail(),
                "RitualFresh - Validación de cuenta",
                """
                        Hola %s,

                        Para activar tu cuenta, ingresa al siguiente enlace:
                        %s

                        Vigencia: %s
                        """
                        .formatted(user.getFirstName(), buildValidationUrl(accountValidationToken), expiresAt));
    }

    @Override
    // Envia el correo con el link de recuperacion sin exponer el token por separado.
    public void sendPasswordResetEmail(User user, String resetToken, LocalDateTime expiresAt) {
        send(
                user.getEmail(),
                "RitualFresh - Recuperación de contraseña",
                """
                        Hola %s,

                        Recibimos una solicitud para restablecer tu contraseña.
                        Para continuar, ingresá al siguiente enlace:
                        %s

                        Este enlace vence el: %s
                        """
                        .formatted(user.getFirstName(), buildPasswordResetUrl(resetToken), expiresAt));
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            throw new IllegalStateException("No se pudo enviar el correo requerido.", exception);
        }
    }

    private String buildValidationUrl(String token) {
        return mailProperties.getBackendBaseUrl() + "/api/users/validation?token=" + token;
    }

    private String buildPasswordResetUrl(String token) {
        return mailProperties.getFrontendBaseUrl() + "/password-reset/confirm?token=" + token;
    }
}
