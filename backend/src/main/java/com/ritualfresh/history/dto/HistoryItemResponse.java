package com.ritualfresh.history.dto;

import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoryItemResponse(
        Long id,
        LocalDateTime scheduledAt,
        Long counterpartId,
        String counterpartName,
        String serviceName,
        String category,
        ServiceHistoryStatus status,
        BigDecimal amountArs) {

    public static HistoryItemResponse from(ServiceHistoryRecord record, UserRole viewerRole) {
        var counterpart = viewerRole == UserRole.WORKER ? record.getClient() : record.getWorker();
        String name = (counterpart.getFirstName() + " " + counterpart.getLastName()).trim();
        return new HistoryItemResponse(
                record.getId(),
                record.getScheduledAt(),
                counterpart.getId(),
                name.isBlank() ? counterpart.getEmail() : name,
                record.getServiceName(),
                record.getCategory(),
                record.getStatus(),
                record.getAmountArs());
    }
}
