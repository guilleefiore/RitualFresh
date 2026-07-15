package com.ritualfresh.notifications.integration;

import com.ritualfresh.notifications.model.NotificationResourceType;
import com.ritualfresh.notifications.model.NotificationType;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

public record NotificationCommand(
        String eventKey,
        Long recipientId,
        NotificationType type,
        String title,
        String message,
        NotificationResourceType resourceType,
        Long resourceId,
        LocalDateTime occurredAt) {

    public static NotificationCommand serviceConfirmed(
            String eventKey,
            Long recipientId,
            Long contractId,
            String serviceName,
            LocalDateTime occurredAt) {
        return new NotificationCommand(
                eventKey,
                recipientId,
                NotificationType.SERVICE_CONFIRMED,
                "Servicio confirmado",
                "Se confirmó la contratación de " + normalized(serviceName, "tu servicio") + ".",
                NotificationResourceType.CONTRACT,
                contractId,
                occurredAt);
    }

    public static NotificationCommand paymentApproved(
            String eventKey,
            Long recipientId,
            Long paymentId,
            BigDecimal amountArs,
            LocalDateTime occurredAt) {
        String amount = amountArs == null
                ? ""
                : " por " + NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR")).format(amountArs);
        return new NotificationCommand(
                eventKey,
                recipientId,
                NotificationType.PAYMENT_APPROVED,
                "Pago aprobado",
                "El pago" + amount + " fue procesado correctamente.",
                NotificationResourceType.PAYMENT,
                paymentId,
                occurredAt);
    }

    public static NotificationCommand claimResolved(
            String eventKey,
            Long recipientId,
            Long claimId,
            String resolutionSummary,
            LocalDateTime occurredAt) {
        return new NotificationCommand(
                eventKey,
                recipientId,
                NotificationType.CLAIM_RESOLVED,
                "Reclamo resuelto",
                normalized(resolutionSummary, "Tu reclamo ya tiene una resolución."),
                NotificationResourceType.CLAIM,
                claimId,
                occurredAt);
    }

    private static String normalized(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
