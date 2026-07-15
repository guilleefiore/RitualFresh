package com.ritualfresh.admin.dto;

import com.ritualfresh.admin.model.AdminUserStatusChange;
import com.ritualfresh.auth.model.AccountStatus;

import java.time.LocalDateTime;

public record AdminStatusChangeResponse(
        Long id,
        Long actorAdminId,
        String actorEmail,
        Long targetUserId,
        String targetEmail,
        AccountStatus previousStatus,
        AccountStatus newStatus,
        String reason,
        LocalDateTime changedAt) {

    public static AdminStatusChangeResponse from(AdminUserStatusChange change) {
        return new AdminStatusChangeResponse(
                change.getId(),
                change.getActorAdminId(),
                change.getActorEmail(),
                change.getTargetUserId(),
                change.getTargetEmail(),
                change.getPreviousStatus(),
                change.getNewStatus(),
                change.getReason(),
                change.getChangedAt());
    }
}
