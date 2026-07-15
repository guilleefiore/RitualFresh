package com.ritualfresh.history.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeBucketResponse(
        LocalDate from,
        LocalDate to,
        long count,
        BigDecimal amountArs) {
}
