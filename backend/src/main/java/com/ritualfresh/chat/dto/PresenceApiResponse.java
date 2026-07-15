package com.ritualfresh.chat.dto;

import java.time.LocalDateTime;

public record PresenceApiResponse(boolean online, LocalDateTime lastSeenAt) {
}
