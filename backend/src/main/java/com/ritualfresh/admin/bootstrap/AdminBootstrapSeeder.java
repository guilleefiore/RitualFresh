package com.ritualfresh.admin.bootstrap;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.security.PasswordSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "ritualfresh.admin.bootstrap.enabled", havingValue = "true")
public class AdminBootstrapSeeder implements ApplicationRunner {
    private final UserRepository userRepository;
    private final String firstName;
    private final String lastName;
    private final String documentNumber;
    private final String phoneNumber;
    private final String email;
    private final String password;

    public AdminBootstrapSeeder(
            UserRepository userRepository,
            @Value("${ritualfresh.admin.bootstrap.first-name:}") String firstName,
            @Value("${ritualfresh.admin.bootstrap.last-name:}") String lastName,
            @Value("${ritualfresh.admin.bootstrap.document-number:}") String documentNumber,
            @Value("${ritualfresh.admin.bootstrap.phone-number:}") String phoneNumber,
            @Value("${ritualfresh.admin.bootstrap.email:}") String email,
            @Value("${ritualfresh.admin.bootstrap.password:}") String password) {
        this.userRepository = userRepository;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getRole() == UserRole.ADMIN)) {
            return;
        }

        if (isBlank(firstName) || isBlank(lastName) || isBlank(documentNumber)
                || isBlank(phoneNumber) || isBlank(email) || isBlank(password)) {
            throw new IllegalStateException("Faltan datos para crear el usuario administrador inicial.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo configurado para el administrador inicial ya existe.");
        }

        User admin = User.register(new User.RegistrationData(
                firstName.trim(),
                lastName.trim(),
                documentNumber.trim(),
                phoneNumber.trim(),
                email.trim().toLowerCase(),
                PasswordSecurity.generateHash(password),
                UserRole.ADMIN,
                LocalDateTime.now(),
                UUID.randomUUID().toString()));
        admin.validateAccount();
        userRepository.save(admin);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
