package com.ritualfresh.admin.service;

import com.ritualfresh.admin.dto.AdminAccountStatus;
import com.ritualfresh.admin.dto.AdminMetricsResponse;
import com.ritualfresh.admin.dto.AdminUserResponse;
import com.ritualfresh.admin.dto.AdminUserStatusRequest;
import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        requireAdmin();

        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .map(AdminUserResponse::from)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminUserResponse getUser(Long userId) {
        requireAdmin();

        return AdminUserResponse.from(findUserById(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdminUserResponse updateUserStatus(Long userId, AdminUserStatusRequest request) {
        User actor = requireAdmin();
        validateStatusRequest(request);

        User user = findUserById(userId);
        AccountStatus newStatus = request.status().toAccountStatus();
        validateStatusTransition(actor, user, newStatus);
        user.changeAccountStatus(newStatus);
        userRepository.save(user);

        return AdminUserResponse.from(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminMetricsResponse getMetrics() {
        requireAdmin();

        List<User> users = userRepository.findAll();

        return new AdminMetricsResponse(
                users.size(),
                countByRole(users, UserRole.CLIENT),
                countByRole(users, UserRole.WORKER),
                countByRole(users, UserRole.ADMIN),
                countByStatus(users, AccountStatus.ACTIVE),
                countByStatus(users, AccountStatus.PENDING_VALIDATION),
                countByStatus(users, AccountStatus.SUSPENDED),
                countByStatus(users, AccountStatus.DELETED));
    }

    private User requireAdmin() {
        User user = userService.getAuthenticatedUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new BusinessRuleException("Debe ser administrador para acceder a esta funcionalidad.");
        }

        return user;
    }

    private User findUserById(Long userId) {
        if (userId == null) {
            throw new BusinessRuleException("El usuario no existe.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessRuleException("El usuario no existe."));
    }

    private void validateStatusRequest(AdminUserStatusRequest request) {
        if (request == null || request.status() == null) {
            throw new BusinessRuleException("Debe completar el estado de la cuenta.");
        }
    }

    private void validateStatusTransition(User actor, User target, AccountStatus newStatus) {
        if (target.getId().equals(actor.getId())
                && (newStatus == AccountStatus.SUSPENDED || newStatus == AccountStatus.DELETED)) {
            throw new BusinessRuleException("No puede suspender o eliminar su propia cuenta.");
        }

        if (!isAllowedTransition(target.getAccountStatus(), newStatus)) {
            throw new BusinessRuleException("La transicion de estado no es valida.");
        }
    }

    private boolean isAllowedTransition(AccountStatus currentStatus, AccountStatus newStatus) {
        return switch (currentStatus) {
            case ACTIVE -> newStatus == AccountStatus.ACTIVE
                    || newStatus == AccountStatus.SUSPENDED
                    || newStatus == AccountStatus.DELETED;
            case PENDING_VALIDATION -> newStatus == AccountStatus.PENDING_VALIDATION
                    || newStatus == AccountStatus.ACTIVE
                    || newStatus == AccountStatus.SUSPENDED
                    || newStatus == AccountStatus.DELETED;
            case SUSPENDED -> newStatus == AccountStatus.SUSPENDED
                    || newStatus == AccountStatus.ACTIVE
                    || newStatus == AccountStatus.DELETED;
            case DELETED -> newStatus == AccountStatus.DELETED;
        };
    }

    private long countByRole(List<User> users, UserRole role) {
        return users.stream().filter(user -> user.getRole() == role).count();
    }

    private long countByStatus(List<User> users, AccountStatus status) {
        return users.stream().filter(user -> user.getAccountStatus() == status).count();
    }
}
