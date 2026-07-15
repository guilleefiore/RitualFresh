package com.ritualfresh.history.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WorkerStatisticsResponse(
        StatisticsPeriod period,
        LocalDate from,
        LocalDate to,
        long completedJobs,
        BigDecimal averageRating,
        List<TimeBucketResponse> completedJobsTimeline) {
}
