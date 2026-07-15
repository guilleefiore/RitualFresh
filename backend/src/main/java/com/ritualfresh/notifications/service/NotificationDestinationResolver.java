package com.ritualfresh.notifications.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.notifications.model.NotificationResourceType;

import java.util.Optional;

public interface NotificationDestinationResolver {
    boolean supports(NotificationResourceType resourceType);

    Optional<String> resolve(User recipient, Long resourceId);
}
