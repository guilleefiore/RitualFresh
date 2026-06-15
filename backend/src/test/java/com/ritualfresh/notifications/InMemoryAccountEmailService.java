package com.ritualfresh.notifications;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.notifications.service.AccountEmailService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Implementacion en memoria para verificar correos en tests sin usar SMTP.
public class InMemoryAccountEmailService implements AccountEmailService {
    private final List<String> validationTokens = new ArrayList<>();
    private final List<String> resetTokens = new ArrayList<>();

    @Override
    public void sendAccountValidationEmail(User user, String accountValidationToken, LocalDateTime expiresAt) {
        validationTokens.add(accountValidationToken);
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken, LocalDateTime expiresAt) {
        resetTokens.add(resetToken);
    }

    public List<String> getValidationTokens() {
        return validationTokens;
    }

    public List<String> getResetTokens() {
        return resetTokens;
    }
}
