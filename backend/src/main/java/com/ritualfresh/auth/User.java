package com.ritualfresh.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
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

    protected User() {
    }

    public User(
            String firstName,
            String lastName,
            String documentNumber,
            String phoneNumber,
            String email,
            String passwordHash,
            UserRole role,
            String accountValidationToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountStatus = AccountStatus.PENDING_VALIDATION;
        this.createdAt = LocalDateTime.now();
        this.accountValidationToken = accountValidationToken;
    }

    void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public String getAccountValidationToken() {
        return accountValidationToken;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenExpiresAt() {
        return passwordResetTokenExpiresAt;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public void validateAccount() {
        this.accountStatus = AccountStatus.ACTIVE;
        this.accountValidationToken = null;
    }

    public void editData(String firstName, String lastName, String documentNumber, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordResetToken = null;
        this.passwordResetTokenExpiresAt = null;
    }

    public void startPasswordReset(String passwordResetToken, LocalDateTime expiresAt) {
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenExpiresAt = expiresAt;
    }

    public boolean hasValidPasswordResetToken(LocalDateTime now) {
        return passwordResetToken != null
                && passwordResetTokenExpiresAt != null
                && passwordResetTokenExpiresAt.isAfter(now);
    }

    public void deactivate() {
        this.accountStatus = AccountStatus.DELETED;
        this.deactivatedAt = LocalDateTime.now();
    }
}
