package com.ritualfresh.notifications.realtime;

public interface NotificationRealtimePublisher {
    void publish(Long recipientId, String type, Object payload);
}
