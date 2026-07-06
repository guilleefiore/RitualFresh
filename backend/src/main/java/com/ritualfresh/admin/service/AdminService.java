package com.ritualfresh.admin.service;

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
// Servicio que gestiona operaciones administrativas: listar usuarios, cambiar estados, obtener métricas
public class AdminService {
    private final UserService userService;
    private final UserRepository userRepository;

    // Obtiene todos los usuarios ordenados por ID (excluye al propio admin)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        User actor = requireAdmin();

        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(actor.getId()))
                .sorted(Comparator.comparing(User::getId))
                .map(AdminUserResponse::from)
                .toList();
    }

    // Obtiene los datos de un usuario específico por ID
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminUserResponse getUser(Long userId) {
        requireAdmin();

        return AdminUserResponse.from(findUserById(userId));
    }

    // Cambia el estado de cuenta de un usuario (validando transiciones permitidas)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdminUserResponse updateUserStatus(Long userId, AdminUserStatusRequest request) {
        User actor = requireAdmin();

        User user = findUserById(userId);
        AccountStatus newStatus = request.status().toAccountStatus();
        validateStatusTransition(actor, user, newStatus);
        user.changeAccountStatus(newStatus);
        userRepository.save(user);

        return AdminUserResponse.from(user);
    }

    // Obtiene estadísticas de usuarios: total, por rol y por estado
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

    // Valida que el usuario autenticado sea administrador
    private User requireAdmin() {
        User user = userService.getAuthenticatedUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new BusinessRuleException("Debe ser administrador para acceder a esta funcionalidad.");
        }

        return user;
    }

    // Obtiene un usuario por ID, lanza excepción si no existe
    private User findUserById(Long userId) {
        if (userId == null) {
            throw new BusinessRuleException("El usuario no existe.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessRuleException("El usuario no existe."));
    }

    // Valida que la transición de estado sea permitida y que no se suspenda/elimine a sí mismo
    private void validateStatusTransition(User actor, User target, AccountStatus newStatus) {
        if (target.getId().equals(actor.getId())
                && (newStatus == AccountStatus.SUSPENDED || newStatus == AccountStatus.DELETED)) {
            throw new BusinessRuleException("No puede suspender o eliminar su propia cuenta.");
        }

        if (!isAllowedTransition(target.getAccountStatus(), newStatus)) {
            throw new BusinessRuleException("La transicion de estado no es valida.");
        }
    }

    // Define las transiciones de estado permitidas según el estado actual
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
            case DELETED -> newStatus == AccountStatus.DELETED
                    || newStatus == AccountStatus.ACTIVE;
        };
    }

    // Cuenta usuarios por rol
    private long countByRole(List<User> users, UserRole role) {
        return users.stream().filter(user -> user.getRole() == role).count();
    }

    // Cuenta usuarios por estado de cuenta
    private long countByStatus(List<User> users, AccountStatus status) {
        return users.stream().filter(user -> user.getAccountStatus() == status).count();
    }
}
