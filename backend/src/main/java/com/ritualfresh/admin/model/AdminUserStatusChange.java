package com.ritualfresh.admin.model;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "admin_user_status_changes",
        indexes = @Index(name = "idx_admin_status_changes_target_date", columnList = "target_user_id, changed_at"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUserStatusChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_admin_id", nullable = false)
    private Long actorAdminId;

    @Column(name = "actor_email", nullable = false, length = 120)
    private String actorEmail;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "target_email", nullable = false, length = 120)
    private String targetEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false, length = 30)
    private AccountStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private AccountStatus newStatus;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public static AdminUserStatusChange record(
            User actor,
            User target,
            AccountStatus previousStatus,
            AccountStatus newStatus,
            String reason,
            LocalDateTime changedAt) {
        AdminUserStatusChange change = new AdminUserStatusChange();
        change.actorAdminId = actor.getId();
        change.actorEmail = actor.getEmail();
        change.targetUserId = target.getId();
        change.targetEmail = target.getEmail();
        change.previousStatus = previousStatus;
        change.newStatus = newStatus;
        change.reason = reason;
        change.changedAt = changedAt;
        return change;
    }

    public void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }
}
