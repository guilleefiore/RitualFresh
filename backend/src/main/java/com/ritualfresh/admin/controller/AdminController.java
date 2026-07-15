package com.ritualfresh.admin.controller;

import com.ritualfresh.admin.dto.AdminMetricsResponse;
import com.ritualfresh.admin.dto.AdminStatusHistoryResponse;
import com.ritualfresh.admin.dto.AdminUserDetailResponse;
import com.ritualfresh.admin.dto.AdminUserStatusRequest;
import com.ritualfresh.admin.dto.AdminUsersPageResponse;
import com.ritualfresh.admin.service.AdminService;
import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// Controlador REST para operaciones administrativas: usuarios y métricas
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public AdminUsersPageResponse listUsers(
            Authentication authentication,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        extractSessionToken(authentication);
        return adminService.listUsers(query, role, status, page, size, sort, direction);
    }

    @GetMapping("/users/{id}")
    public AdminUserDetailResponse getUser(
            Authentication authentication,
            @PathVariable Long id) {
        extractSessionToken(authentication);
        return adminService.getUser(id);
    }

    @PatchMapping("/users/{id}/status")
    public AdminUserDetailResponse updateUserStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AdminUserStatusRequest request) {
        extractSessionToken(authentication);
        return adminService.updateUserStatus(id, request);
    }

    @GetMapping("/users/{id}/status-history")
    public AdminStatusHistoryResponse getStatusHistory(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        extractSessionToken(authentication);
        return adminService.getStatusHistory(id, page, size);
    }

    // GET /api/admin/metrics - Obtiene estadísticas de usuarios
    @GetMapping("/metrics")
    @ResponseStatus(HttpStatus.OK)
    public AdminMetricsResponse getMetrics(Authentication authentication) {
        extractSessionToken(authentication);
        return adminService.getMetrics();
    }

    // Extrae el token de sesión del contexto de autenticación
    private String extractSessionToken(Authentication authentication) {
        return authentication == null || authentication.getCredentials() == null
                ? null
                : authentication.getCredentials().toString();
    }
}
