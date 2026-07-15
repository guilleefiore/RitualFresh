package com.ritualfresh.history.dto;

public enum StatisticsPeriod {
    LAST_7_DAYS(7),
    LAST_30_DAYS(30),
    LAST_365_DAYS(365);

    private final int days;

    StatisticsPeriod(int days) {
        this.days = days;
    }

    public int days() {
        return days;
    }
}
