package com.ritualfresh.chat.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReadMessagesApiRequest(@NotEmpty List<Long> messageIds) {
}
