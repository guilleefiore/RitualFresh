package com.ritualfresh.admin;

import com.ritualfresh.admin.dto.AdminAccountStatus;
import com.ritualfresh.admin.dto.AdminMetricsResponse;
import com.ritualfresh.admin.dto.AdminStatusHistoryResponse;
import com.ritualfresh.admin.dto.AdminUserDetailResponse;
import com.ritualfresh.admin.dto.AdminUserStatusRequest;
import com.ritualfresh.admin.dto.AdminUsersPageResponse;
import com.ritualfresh.admin.repository.AdminStatusChangeRepository;
import com.ritualfresh.admin.repository.AdminUserQueryRepository;
import com.ritualfresh.admin.repository.InMemoryAdminStatusChangeRepository;
import com.ritualfresh.admin.repository.InMemoryAdminUserQueryRepository;
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
import com.ritualfresh.notifications.InMemoryAccountEmailService;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
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
    private AdminStatusChangeRepository statusChangeRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRepository = new InMemoryUserRepository();
        userSessionRepository = new InMemoryUserSessionRepository();
        userService = new UserService(
                userRepository,
                userSessionRepository,
                new InMemoryAccountEmailService(),
                new InMemoryClientProfileRepository(),
                new InMemoryWorkerProfileRepository());
        AdminUserQueryRepository adminUserQueryRepository = new InMemoryAdminUserQueryRepository(userRepository);
        statusChangeRepository = new InMemoryAdminStatusChangeRepository();
        adminService = new AdminService(
                userService,
                userRepository,
                adminUserQueryRepository,
                statusChangeRepository);
    }

    @Test
    void adminCanListUsersAndReadMetrics() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        registerValidatedClient();

        AdminUsersPageResponse users = adminService.listUsers(null, null, null, 0, 20, "createdAt", "desc");
        AdminMetricsResponse metrics = adminService.getMetrics();

        assertEquals(1, users.content().size());
        assertEquals(1, metrics.clientUsers());
        assertEquals(1, metrics.adminUsers());
        assertEquals(2, metrics.totalUsers());
    }

    @Test
    void adminCanReadUserDetailAndChangeStatus() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User client = registerValidatedClient().user();

        AdminUserDetailResponse detail = adminService.getUser(client.getId());
        AdminUserDetailResponse updated = adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED, "Incumplimiento de normas"));

        assertEquals(client.getEmail(), detail.email());
        assertEquals(AccountStatus.SUSPENDED, updated.accountStatus());
        assertEquals(AccountStatus.SUSPENDED, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void nonAdminCannotAccessAdminFunctions() {
        LoginResult clientSession = loginValidatedClient();
        authenticate(clientSession);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                adminService.listUsers(null, null, null, 0, 20, "createdAt", "desc"));

        assertEquals("Debe ser administrador para acceder a esta funcionalidad.", exception.getMessage());
    }

    @Test
    void adminCannotManageAdministrativeAccounts() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User admin = userRepository.findByEmail("admin@example.com").orElseThrow();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> adminService.updateUserStatus(
                admin.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED, "Motivo")));

        assertEquals("Las cuentas administrativas no pueden modificarse desde este panel.", exception.getMessage());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(admin.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void statusChangeRequiresReason() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User client = registerValidatedClient().user();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED, " ")));

        assertEquals("Debe indicar el motivo del cambio de estado.", exception.getMessage());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void adminCanReactivateSuspendedAndDeletedUsers() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User client = registerValidatedClient().user();

        adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED, "Revision preventiva"));

        AdminUserDetailResponse reactivated = adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.ACTIVE, "Revision completada"));

        assertEquals(AccountStatus.ACTIVE, reactivated.accountStatus());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());

        adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.DELETED, "Solicitud administrativa"));

        AdminUserDetailResponse restored = adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.ACTIVE, "Cuenta restaurada"));

        assertEquals(AccountStatus.ACTIVE, restored.accountStatus());
        assertEquals(AccountStatus.ACTIVE, userRepository.findById(client.getId()).orElseThrow().getAccountStatus());
    }

    @Test
    void adminCanFilterAndPaginateUsers() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        registerValidatedClient();

        AdminUsersPageResponse page = adminService.listUsers(
                "guillermina",
                UserRole.CLIENT,
                AccountStatus.ACTIVE,
                0,
                1,
                "email",
                "asc");

        assertEquals(1, page.content().size());
        assertEquals(1, page.totalElements());
        assertEquals("guillermina@example.com", page.content().getFirst().email());
    }

    @Test
    void statusChangesAreAudited() {
        LoginResult adminSession = createAdminSession();
        authenticate(adminSession);
        User client = registerValidatedClient().user();

        adminService.updateUserStatus(
                client.getId(),
                new AdminUserStatusRequest(AdminAccountStatus.SUSPENDED, "Incumplimiento reiterado"));
        AdminStatusHistoryResponse history = adminService.getStatusHistory(client.getId(), 0, 10);

        assertEquals(1, history.totalElements());
        assertEquals(AccountStatus.ACTIVE, history.content().getFirst().previousStatus());
        assertEquals(AccountStatus.SUSPENDED, history.content().getFirst().newStatus());
        assertEquals("Incumplimiento reiterado", history.content().getFirst().reason());
        assertEquals("admin@example.com", history.content().getFirst().actorEmail());
    }

    private LoginResult createAdminSession() {
        User admin = User.register(new User.RegistrationData(
                "Admin",
                "Ritual",
                "admin@example.com",
                PasswordSecurity.generateHash("admin123"),
                UserRole.ADMIN,
                LocalDateTime.now(),
                "admin-token",
                LocalDateTime.now().plusDays(1)));
        admin.validateAccount();
        userRepository.save(admin);

        return userService.login(new LoginRequest("admin@example.com", "admin123"));
    }

    private RegisterUserResult registerValidatedClient() {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                "guillermina@example.com",
                "Clave123",
                "Clave123",
                UserRole.CLIENT));
        userService.validateAccount(result.accountValidationToken());
        return result;
    }

    private LoginResult loginValidatedClient() {
        RegisterUserResult result = registerValidatedClient();
        return userService.login(new LoginRequest(result.user().getEmail(), "Clave123"));
    }

    private void authenticate(LoginResult session) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                AuthenticatedUserPrincipal.from(session.user()),
                session.sessionToken(),
                List.of(new SimpleGrantedAuthority("ROLE_" + session.user().getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
