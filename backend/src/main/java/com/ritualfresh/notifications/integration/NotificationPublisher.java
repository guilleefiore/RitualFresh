package com.ritualfresh.notifications.integration;

public interface NotificationPublisher {
    void publish(NotificationCommand command);
}
