package com.ritualfresh.admin.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record AdminStatusHistoryResponse(
        List<AdminStatusChangeResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static AdminStatusHistoryResponse from(Page<AdminStatusChangeResponse> result) {
        return new AdminStatusHistoryResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }
}
