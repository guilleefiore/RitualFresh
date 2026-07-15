package com.ritualfresh.history.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.history.dto.HistoryItemResponse;
import com.ritualfresh.history.dto.HistoryPageResponse;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import com.ritualfresh.history.repository.ServiceHistoryRecordRepository;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 20;

    private final UserService userService;
    private final ServiceHistoryRecordRepository historyRepository;

    @PreAuthorize("hasAnyRole('CLIENT', 'WORKER')")
    @Transactional(readOnly = true)
    public HistoryPageResponse getMyHistory(
            ServiceHistoryStatus status,
            LocalDate from,
            LocalDate to,
            int page,
            int size) {
        validateRange(from, to);
        User user = requireHistoryRole();
        int safePage = Math.max(page, 0);
        int requestedSize = size <= 0 ? DEFAULT_PAGE_SIZE : size;
        int safeSize = Math.min(requestedSize, MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Order.desc("scheduledAt"), Sort.Order.desc("id")));

        return HistoryPageResponse.from(historyRepository
                .findHistory(user.getId(), user.getRole(), status, from, to, pageable)
                .map(record -> HistoryItemResponse.from(record, user.getRole())));
    }

    private User requireHistoryRole() {
        User user = userService.getAuthenticatedUser();
        if (user.getRole() != UserRole.CLIENT && user.getRole() != UserRole.WORKER) {
            throw new BusinessRuleException("El rol del usuario no permite consultar el historial de servicios.");
        }
        return user;
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BusinessRuleException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
    }
}
