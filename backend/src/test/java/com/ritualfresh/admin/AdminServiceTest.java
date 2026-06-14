package com.ritualfresh.admin;

import com.ritualfresh.admin.dto.AdminAccountStatus;
import com.ritualfresh.admin.dto.AdminMetricsResponse;
import com.ritualfresh.admin.dto.AdminUserResponse;
import com.ritualfresh.admin.dto.AdminUserStatusRequest;
import com.ritualfresh.admin.service.AdminService;
import com.ritualfresh.auth.dto.LoginRequest;
import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.dto.RegisterUserRequest;
import com.ritualfresh.auth.dto.RegisterUserResult;
import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.auth.security.PasswordSecurity;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminServiceTest {
    private UserRepository userRepository;
    private UserSessionRepository userSessionRepository;
    private UserService userService;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRepository = new InMemoryUserRepository();
        userSessionRepository = new InMemoryUserSessionRepository();
        userService = new UserService(userRepository, userSessionRepository);
        adminService = new AdminService(userService, userRepository);
    }

    @Test
    void adminCanListUsersAndReadMetrics() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        registerValidatedClient();

        List<AdminUserResponse> users = adminService.listUsers();
        AdminMetricsResponse metrics = adminService.getMetrics();

        assertEquals(2, users.size());
        assertEquals(1, metrics.clientUsers());
        assertEquals(1, metrics.adminUsers());
        assertEquals(2, metrics.totalUsers());
    }

    @Test
    void adminCanReadUserDetailAndChangeStatus() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User client = registerValidatedClient().user();

        AdminUserResponse detail = adminService.getUser(client.getId());
        AdminUserResponse updated = adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED));

        assertEquals(client.getEmail(), detail.email());
        assertEquals(AccountStatus.SUSPENDED, updated.accountStatus());
        assertEquals(AccountStatus.SUSPENDED, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void nonAdminCannotAccessAdminFunctions() {
        LoginResult clientSession = loginValidatedClient();
        authenticate(clientSession);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, adminService::listUsers);

        assertEquals("Debe ser administrador para acceder a esta funcionalidad.", exception.getMessage());
    }

    @Test
    void adminCannotSuspendHimself() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User admin = userRepository.findByEmail("admin@example.com").orElseThrow();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> adminService.updateUserStatus(
                admin.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED)));

        assertEquals("No puede suspender o eliminar su propia cuenta.", exception.getMessage());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(admin.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void adminCannotDeleteHimself() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User admin = userRepository.findByEmail("admin@example.com").orElseThrow();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> adminService.updateUserStatus(
                admin.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.DELETED)));

        assertEquals("No puede suspender o eliminar su propia cuenta.", exception.getMessage());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(admin.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void adminCanReactivateSuspendedUserButNotDeletedOne() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User client = registerValidatedClient().user();

        adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED));

        AdminUserResponse reactivated = adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.ACTIVE));

        assertEquals(AccountStatus.ACTIVE, reactivated.accountStatus());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());

        adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.DELETED));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.ACTIVE)));

        assertEquals("La transicion de estado no es valida.", exception.getMessage());
        assertEquals(AccountStatus.DELETED, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());
    }

    private LoginResult createAdminSession() {
        User admin = User.register(new User.RegistrationData(
                "Admin",
                "Ritual",
                "00000000",
                "2610000000",
                "admin@example.com",
                PasswordSecurity.generateHash("admin123"),
                UserRole.ADMIN,
                LocalDateTime.now(),
                "admin-token"));
        admin.validateAccount();
        userRepository.save(admin);

        return userService.login(new LoginRequest("admin@example.com", "admin123"));
    }

    private RegisterUserResult registerValidatedClient() {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "clave123",
                UserRole.CLIENT));
        userService.validateAccount(result.accountValidationToken());
        return result;
    }

    private LoginResult loginValidatedClient() {
        RegisterUserResult result = registerValidatedClient();
        return userService.login(new LoginRequest(result.user().getEmail(), "clave123"));
    }

    private void authenticate(LoginResult session) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                AuthenticatedUserPrincipal.from(session.user()),
                session.sessionToken(),
                List.of(new SimpleGrantedAuthority("ROLE_" + session.user().getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
