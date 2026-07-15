package com.ritualfresh.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReadMessagesApiResponse(List<Long> messageIds, LocalDateTime readAt) {
}
