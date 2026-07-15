package com.ritualfresh.notifications.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringNotificationPublisher implements NotificationPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(NotificationCommand command) {
        eventPublisher.publishEvent(command);
    }
}
