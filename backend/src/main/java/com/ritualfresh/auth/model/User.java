package com.ritualfresh.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String documentNumber;

    @Column(nullable = false, length = 30)
    private String phoneNumber;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus accountStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deactivatedAt;

    @Column(length = 80)
    private String accountValidationToken;

    @Column(length = 80)
    private String passwordResetToken;

    private LocalDateTime passwordResetTokenExpiresAt;

    public static User register(RegistrationData data) {
        User user = new User();
        user.firstName = data.firstName();
        user.lastName = data.lastName();
        user.documentNumber = data.documentNumber();
        user.phoneNumber = data.phoneNumber();
        user.email = data.email();
        user.passwordHash = data.passwordHash();
        user.role = data.role();
        user.accountStatus = AccountStatus.PENDING_VALIDATION;
        user.createdAt = data.createdAt();
        user.accountValidationToken = data.accountValidationToken();
        return user;
    }

    // Datos agrupados para registrar un usuario nuevo sin pasar muchos parámetros sueltos.
    public record RegistrationData(
            String firstName,
            String lastName,
            String documentNumber,
            String phoneNumber,
            String email,
            String passwordHash,
            UserRole role,
            LocalDateTime createdAt,
            String accountValidationToken) {
    }

    // Indica si la cuenta ya quedó habilitada para operar.
    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    // Confirma la cuenta y limpia el token de validación.
    public void validateAccount() {
        changeAccountStatus(AccountStatus.ACTIVE);
    }

    // Actualiza los datos básicos editables del usuario.
    public void editData(String firstName, String lastName, String documentNumber, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
    }

    // Reemplaza el hash de contraseña y limpia el token de reseteo.
    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordResetToken = null;
        this.passwordResetTokenExpiresAt = null;
    }

    // Inicia la ventana de recuperación de contraseña.
    public void startPasswordReset(String passwordResetToken, LocalDateTime expiresAt) {
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenExpiresAt = expiresAt;
    }

    // Verifica si el token de recuperación sigue vigente.
    public boolean hasValidPasswordResetToken(LocalDateTime now) {
        return passwordResetToken != null
                && passwordResetTokenExpiresAt != null
                && passwordResetTokenExpiresAt.isAfter(now);
    }

    // Cambia el estado administrativo de la cuenta y limpia tokens sensibles.
    public void changeAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        this.deactivatedAt = accountStatus == AccountStatus.ACTIVE ? null : LocalDateTime.now();
        this.accountValidationToken = null;
        this.passwordResetToken = null;
        this.passwordResetTokenExpiresAt = null;
    }

    // Marca la cuenta como eliminada y registra el momento.
    public void deactivate() {
        changeAccountStatus(AccountStatus.DELETED);
    }
}
