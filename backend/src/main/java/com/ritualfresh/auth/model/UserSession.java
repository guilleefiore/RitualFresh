package com.ritualfresh.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_sessions",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_sessions_token", columnNames = "token"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_sessions_users"))
    private User user;

    @Column(nullable = false, length = 80)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime closedAt;

    public UserSession(User user, String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Permite simular el autoincremental de la BD en repositorios en memoria.
    public void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    // Indica si la sesion sigue vigente y no fue cerrada.
    public boolean isActive(LocalDateTime now) {
        return closedAt == null && expiresAt.isAfter(now);
    }

    // Marca la sesion como cerrada.
    public void close(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
