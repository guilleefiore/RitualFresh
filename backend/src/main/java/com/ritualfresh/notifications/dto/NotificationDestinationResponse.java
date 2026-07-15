package com.ritualfresh.notifications.dto;

public record NotificationDestinationResponse(
        boolean available,
        String path,
        String message) {

    public static NotificationDestinationResponse available(String path) {
        return new NotificationDestinationResponse(true, path, null);
    }

    public static NotificationDestinationResponse unavailable() {
        return new NotificationDestinationResponse(
                false,
                null,
                "El contenido ya no se encuentra disponible.");
    }
}
