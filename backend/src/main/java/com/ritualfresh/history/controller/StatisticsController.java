package com.ritualfresh.history.controller;

import com.ritualfresh.history.dto.ClientStatisticsResponse;
import com.ritualfresh.history.dto.StatisticsPeriod;
import com.ritualfresh.history.dto.WorkerStatisticsResponse;
import com.ritualfresh.history.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/workers/me")
    public WorkerStatisticsResponse getMyWorkerStatistics(
            @RequestParam(defaultValue = "LAST_30_DAYS") StatisticsPeriod period) {
        return statisticsService.getMyWorkerStatistics(period);
    }

    @GetMapping("/clients/me")
    public ClientStatisticsResponse getMyClientStatistics(
            @RequestParam(defaultValue = "LAST_30_DAYS") StatisticsPeriod period) {
        return statisticsService.getMyClientStatistics(period);
    }
}
