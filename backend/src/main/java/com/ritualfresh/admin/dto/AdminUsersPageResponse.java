package com.ritualfresh.admin.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record AdminUsersPageResponse(
        List<AdminUserResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static AdminUsersPageResponse from(Page<AdminUserResponse> result) {
        return new AdminUsersPageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }
}
