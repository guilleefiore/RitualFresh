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
                "00000000",
                "2610000000",
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
                "00000000",
                "2610000000",
                "admin@example.com",
                "hash",
                UserRole.ADMIN,
                LocalDateTime.now(),
                UUID.randomUUID().toString()));
        admin.validateAccount();
        userRepository.save(admin);

        AdminBootstrapSeeder seeder = new AdminBootstrapSeeder(
                userRepository,
                "Otro",
                "Admin",
                "11111111",
                "2611111111",
                "otro-admin@example.com",
                "admin123");

        seeder.run(null);

        assertEquals(1, userRepository.findAll().size());
    }
}
