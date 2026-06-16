package com.ritualfresh.admin.controller;

import com.ritualfresh.admin.dto.AdminMetricsResponse;
import com.ritualfresh.admin.dto.AdminUserResponse;
import com.ritualfresh.admin.dto.AdminUserStatusRequest;
import com.ritualfresh.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// Controlador REST para operaciones administrativas: usuarios y métricas
public class AdminController {
    private final AdminService adminService;

    // GET /api/admin/users - Lista todos los usuarios
    @GetMapping("/users")
    public List<AdminUserResponse> listUsers(Authentication authentication) {
        extractSessionToken(authentication);
        return adminService.listUsers();
    }

    // GET /api/admin/users/{id} - Obtiene los datos de un usuario específico
    @GetMapping("/users/{id}")
    public AdminUserResponse getUser(
            Authentication authentication,
            @PathVariable Long id) {
        extractSessionToken(authentication);
        return adminService.getUser(id);
    }

    // PATCH /api/admin/users/{id}/status - Cambia el estado de cuenta de un usuario
    @PatchMapping("/users/{id}/status")
    public AdminUserResponse updateUserStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AdminUserStatusRequest request) {
        extractSessionToken(authentication);
        return adminService.updateUserStatus(id, request);
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
