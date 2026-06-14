package com.ritualfresh.admin.dto;

public record AdminMetricsResponse(
        long totalUsers,
        long clientUsers,
        long workerUsers,
        long adminUsers,
        long activeUsers,
        long pendingValidationUsers,
        long suspendedUsers,
        long deletedUsers) {
}
