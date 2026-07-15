package com.ritualfresh.history.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record HistoryPageResponse(
        List<HistoryItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext) {

    public static HistoryPageResponse from(Page<HistoryItemResponse> page) {
        return new HistoryPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext());
    }
}
