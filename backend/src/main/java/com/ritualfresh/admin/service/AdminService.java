package com.ritualfresh.admin.service;

import com.ritualfresh.admin.dto.AdminAccountStatus;
import com.ritualfresh.admin.dto.AdminMetricsResponse;
import com.ritualfresh.admin.dto.AdminStatusChangeResponse;
import com.ritualfresh.admin.dto.AdminStatusHistoryResponse;
import com.ritualfresh.admin.dto.AdminUserDetailResponse;
import com.ritualfresh.admin.dto.AdminUserResponse;
import com.ritualfresh.admin.dto.AdminUsersPageResponse;
import com.ritualfresh.admin.dto.AdminUserStatusRequest;
import com.ritualfresh.admin.model.AdminUserStatusChange;
import com.ritualfresh.admin.repository.AdminStatusChangeRepository;
import com.ritualfresh.admin.repository.AdminUserQueryRepository;
import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_HISTORY_PAGE_SIZE = 50;
    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id", "firstName", "lastName", "email", "role", "accountStatus", "createdAt");

    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminUserQueryRepository adminUserQueryRepository;
    private final AdminStatusChangeRepository statusChangeRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminUsersPageResponse listUsers(
            String query,
            UserRole role,
            AccountStatus status,
            int page,
            int size,
            String sortBy,
            String direction) {
        requireAdmin();
        if (role == UserRole.ADMIN) {
            throw new BusinessRuleException("Las cuentas administrativas no se gestionan desde este listado.");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        String safeSort = sortBy != null && ALLOWED_SORTS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction safeDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(safeDirection, safeSort));
        Page<AdminUserResponse> result = adminUserQueryRepository
                .search(normalizeOptional(query), role, status, pageable)
                .map(AdminUserResponse::from);

        return AdminUsersPageResponse.from(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUser(Long userId) {
        requireAdmin();
        User user = findManageableUser(userId);
        return toDetail(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdminUserDetailResponse updateUserStatus(Long userId, AdminUserStatusRequest request) {
        User actor = requireAdmin();
        if (request == null || request.status() == null) {
            throw new BusinessRuleException("Debe seleccionar un estado de cuenta.");
        }

        User target = findManageableUser(userId);
        AccountStatus previousStatus = target.getAccountStatus();
        AccountStatus newStatus = request.status().toAccountStatus();
        String reason = validateReason(request.reason());
        validateStatusTransition(previousStatus, newStatus);

        target.changeAccountStatus(newStatus);
        userRepository.save(target);
        statusChangeRepository.save(AdminUserStatusChange.record(
                actor,
                target,
                previousStatus,
                newStatus,
                reason,
                LocalDateTime.now()));

        return toDetail(target);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminStatusHistoryResponse getStatusHistory(Long userId, int page, int size) {
        requireAdmin();
        User target = findManageableUser(userId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_HISTORY_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "changedAt"));
        Page<AdminStatusChangeResponse> result = statusChangeRepository
                .findByTargetUserId(target.getId(), pageable)
                .map(AdminStatusChangeResponse::from);

        return AdminStatusHistoryResponse.from(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminMetricsResponse getMetrics() {
        requireAdmin();

        return new AdminMetricsResponse(
                adminUserQueryRepository.countAll(),
                adminUserQueryRepository.countByRole(UserRole.CLIENT),
                adminUserQueryRepository.countByRole(UserRole.WORKER),
                adminUserQueryRepository.countByRole(UserRole.ADMIN),
                adminUserQueryRepository.countByStatus(AccountStatus.ACTIVE),
                adminUserQueryRepository.countByStatus(AccountStatus.PENDING_VALIDATION),
                adminUserQueryRepository.countByStatus(AccountStatus.SUSPENDED),
                adminUserQueryRepository.countByStatus(AccountStatus.DELETED));
    }

    private User requireAdmin() {
        User user = userService.getAuthenticatedUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new BusinessRuleException("Debe ser administrador para acceder a esta funcionalidad.");
        }

        return user;
    }

    private User findManageableUser(Long userId) {
        if (userId == null) {
            throw new BusinessRuleException("El usuario no existe.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessRuleException("El usuario no existe."));
        if (user.getRole() == UserRole.ADMIN) {
            throw new BusinessRuleException("Las cuentas administrativas no pueden modificarse desde este panel.");
        }

        return user;
    }

    private AdminUserDetailResponse toDetail(User user) {
        return AdminUserDetailResponse.from(user, allowedTransitions(user.getAccountStatus()));
    }

    private List<AdminAccountStatus> allowedTransitions(AccountStatus currentStatus) {
        return switch (currentStatus) {
            case PENDING_VALIDATION -> List.of(
                    AdminAccountStatus.ACTIVE,
                    AdminAccountStatus.SUSPENDED,
                    AdminAccountStatus.DELETED);
            case ACTIVE -> List.of(AdminAccountStatus.SUSPENDED, AdminAccountStatus.DELETED);
            case SUSPENDED -> List.of(AdminAccountStatus.ACTIVE, AdminAccountStatus.DELETED);
            case DELETED -> List.of(AdminAccountStatus.ACTIVE);
        };
    }

    private void validateStatusTransition(AccountStatus currentStatus, AccountStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new BusinessRuleException("El estado seleccionado ya es el estado actual.");
        }

        boolean allowed = allowedTransitions(currentStatus).stream()
                .map(AdminAccountStatus::toAccountStatus)
                .anyMatch(status -> status == newStatus);
        if (!allowed) {
            throw new BusinessRuleException("La transicion de estado no es valida.");
        }
    }

    private String validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleException("Debe indicar el motivo del cambio de estado.");
        }

        String normalized = reason.trim();
        if (normalized.length() > 500) {
            throw new BusinessRuleException("El motivo no debe superar los 500 caracteres.");
        }

        return normalized;
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
