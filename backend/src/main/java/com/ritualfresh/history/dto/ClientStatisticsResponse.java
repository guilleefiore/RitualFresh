package com.ritualfresh.history.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ClientStatisticsResponse(
        StatisticsPeriod period,
        LocalDate from,
        LocalDate to,
        long hiredServices,
        long pendingServices,
        long completedServices,
        BigDecimal totalSpentArs,
        List<TimeBucketResponse> spendingTimeline,
        List<CategoryMetricResponse> categories,
        List<FrequentWorkerResponse> frequentWorkers) {
}
