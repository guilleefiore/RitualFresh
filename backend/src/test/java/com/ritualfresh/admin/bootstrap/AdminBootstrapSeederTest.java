package com.ritualfresh.admin.bootstrap;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminBootstrapSeederTest {
    @Test
    void createsAdminWhenNoAdminExists() throws Exception {
        UserRepository userRepository = new InMemoryUserRepository();
        AdminBootstrapSeeder seeder = new AdminBootstrapSeeder(
                userRepository,
                "Admin",
                "Inicial",
                "admin@example.com",
                "admin123");

        seeder.run(null);

        assertEquals(1, userRepository.findAll().size());
        assertTrue(userRepository.findAll().stream().anyMatch(user -> user.getRole() == UserRole.ADMIN && user.isActive()));
    }

    @Test
    void doesNotCreateSecondAdminWhenOneAlreadyExists() throws Exception {
        UserRepository userRepository = new InMemoryUserRepository();
        User admin = User.register(new User.RegistrationData(
                "Admin",
                "Inicial",
                "admin@example.com",
                "hash",
                UserRole.ADMIN,
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusDays(1)));
        admin.validateAccount();
        userRepository.save(admin);

        AdminBootstrapSeeder seeder = new AdminBootstrapSeeder(
                userRepository,
                "Otro",
                "Admin",
                "otro-admin@example.com",
                "admin123");

        seeder.run(null);

        assertEquals(1, userRepository.findAll().size());
    }
}
