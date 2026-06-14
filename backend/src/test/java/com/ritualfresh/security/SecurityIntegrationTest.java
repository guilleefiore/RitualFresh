package com.ritualfresh.security;

import com.ritualfresh.admin.controller.AdminController;
import com.ritualfresh.admin.service.AdminService;
import com.ritualfresh.auth.controller.UserController;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.model.UserSession;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.profiles.controller.ProfileController;
import com.ritualfresh.profiles.repository.ClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
import com.ritualfresh.profiles.repository.WorkerProfileRepository;
import com.ritualfresh.profiles.service.ProfileService;
import com.ritualfresh.shared.config.SecurityConfig;
import com.ritualfresh.shared.security.RestAccessDeniedHandler;
import com.ritualfresh.shared.security.RestAuthenticationEntryPoint;
import com.ritualfresh.shared.security.SessionAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        ProfileController.class,
        AdminController.class
})
@Import({
        SecurityConfig.class,
        SessionAuthenticationFilter.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        SecurityIntegrationTest.TestBeans.class
})
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @BeforeEach
    void setUp() {
        // WebMvcTest recreates the context for this test class once, so each test uses unique fixture data.
    }

    @Test
    void userWithoutTokenCannotAccessPrivateEndpoint() throws Exception {
        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Debe iniciar sesion para acceder a esta funcionalidad."));
    }

    @Test
    void invalidTokenCannotAccessPrivateEndpoint() throws Exception {
        mockMvc.perform(get("/api/profiles/me")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Debe iniciar sesion para acceder a esta funcionalidad."));
    }

    @Test
    void expiredTokenCannotAccessPrivateEndpoint() throws Exception {
        persistSession("token-expirado", UserRole.CLIENT, LocalDateTime.now().minusMinutes(1), null);

        mockMvc.perform(get("/api/profiles/me")
                        .header("Authorization", "Bearer token-expirado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("La sesion expiro. Debe iniciar sesion nuevamente."));
    }

    @Test
    void closedTokenCannotAccessPrivateEndpoint() throws Exception {
        persistSession("token-cerrado", UserRole.CLIENT, LocalDateTime.now().plusHours(1), LocalDateTime.now());

        mockMvc.perform(get("/api/profiles/me")
                        .header("Authorization", "Bearer token-cerrado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("La sesion expiro. Debe iniciar sesion nuevamente."));
    }

    @Test
    void clientCannotAccessWorkerEndpoints() throws Exception {
        persistSession("token-client-worker", UserRole.CLIENT, LocalDateTime.now().plusHours(1), null);

        mockMvc.perform(post("/api/profiles/trabajadores")
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header("Authorization", "Bearer token-client-worker"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("No posee permisos para acceder a esta funcionalidad."));
    }

    @Test
    void workerCannotAccessClientEndpoints() throws Exception {
        persistSession("token-worker-client", UserRole.WORKER, LocalDateTime.now().plusHours(1), null);

        mockMvc.perform(post("/api/profiles/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header("Authorization", "Bearer token-worker-client"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("No posee permisos para acceder a esta funcionalidad."));
    }

    @Test
    void clientCannotAccessAdminEndpoints() throws Exception {
        persistSession("token-client-admin", UserRole.CLIENT, LocalDateTime.now().plusHours(1), null);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer token-client-admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("No posee permisos para acceder a esta funcionalidad."));
    }

    @Test
    void workerCannotAccessAdminEndpoints() throws Exception {
        persistSession("token-worker-admin", UserRole.WORKER, LocalDateTime.now().plusHours(1), null);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer token-worker-admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("No posee permisos para acceder a esta funcionalidad."));
    }

    @Test
    void adminCanAccessAdminEndpoints() throws Exception {
        persistSession("token-admin", UserRole.ADMIN, LocalDateTime.now().plusHours(1), null);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer token-admin"))
                .andExpect(status().isOk());
    }

    private void persistSession(String token, UserRole role, LocalDateTime expiresAt, LocalDateTime closedAt) {
        User user = User.register(new User.RegistrationData(
                "Test",
                "User",
                token.hashCode() + "",
                "2610000000",
                token + "@example.com",
                "hash",
                role,
                LocalDateTime.now().minusHours(1),
                "validation-token-" + token));
        user.validateAccount();
        userRepository.save(user);

        UserSession session = new UserSession(user, token, LocalDateTime.now().minusMinutes(5), expiresAt);
        if (closedAt != null) {
            session.close(closedAt);
        }

        userSessionRepository.save(session);
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        UserRepository userRepository() {
            return new InMemoryUserRepository();
        }

        @Bean
        UserSessionRepository userSessionRepository() {
            return new InMemoryUserSessionRepository();
        }

        @Bean
        ClientProfileRepository clientProfileRepository() {
            return new InMemoryClientProfileRepository();
        }

        @Bean
        WorkerProfileRepository workerProfileRepository() {
            return new InMemoryWorkerProfileRepository();
        }

        @Bean
        UserService userService(UserRepository userRepository, UserSessionRepository userSessionRepository) {
            return new UserService(userRepository, userSessionRepository);
        }

        @Bean
        ProfileService profileService(
                UserService userService,
                ClientProfileRepository clientProfileRepository,
                WorkerProfileRepository workerProfileRepository) {
            return new ProfileService(userService, clientProfileRepository, workerProfileRepository);
        }

        @Bean
        AdminService adminService(UserService userService, UserRepository userRepository) {
            return new AdminService(userService, userRepository);
        }
    }
}
