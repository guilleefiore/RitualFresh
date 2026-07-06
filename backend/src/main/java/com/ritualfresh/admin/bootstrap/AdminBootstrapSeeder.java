package com.ritualfresh.admin.bootstrap;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.security.PasswordSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

// Crea automáticamente un usuario administrador inicial cuando se inicia la aplicación
@Component
@ConditionalOnProperty(name = "ritualfresh.admin.bootstrap.enabled", havingValue = "true")
public class AdminBootstrapSeeder implements ApplicationRunner {
    // Repositorio para acceder a los datos de usuarios
    private final UserRepository userRepository;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    // Datos del administrador inicial obtenidos de la configuración
    // Constructor que inyecta el repositorio y los datos del administrador inicial
    public AdminBootstrapSeeder(
            UserRepository userRepository,
            @Value("${ritualfresh.admin.bootstrap.first-name:}") String firstName,
            @Value("${ritualfresh.admin.bootstrap.last-name:}") String lastName,
            @Value("${ritualfresh.admin.bootstrap.email:}") String email,
            @Value("${ritualfresh.admin.bootstrap.password:}") String password) {
        this.userRepository = userRepository;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Se ejecuta automáticamente al iniciar la aplicación
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        // Si ya existe un administrador, no hacer nada
        if (userRepository.findAll().stream().anyMatch(user -> user.getRole() == UserRole.ADMIN)) {
            return;
        }

        // Validar que todos los datos requeridos estén configurados
        if (isBlank(firstName) || isBlank(lastName) || isBlank(email) || isBlank(password)) {
            throw new IllegalStateException("Faltan datos para crear el usuario administrador inicial.");
        }

        // Verificar que el email del administrador no esté ya registrado
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo configurado para el administrador inicial ya existe.");
        }

        // Crear el usuario administrador con los datos configurados y contraseña encriptada
        User admin = User.register(new User.RegistrationData(
                firstName.trim(),
                lastName.trim(),
                email.trim().toLowerCase(),
                PasswordSecurity.generateHash(password),
                UserRole.ADMIN,
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusDays(1)));
        // Validar la cuenta para activarla automáticamente
        admin.validateAccount();
        // Guardar el administrador en la base de datos
        userRepository.save(admin);
    }

    // Verifica si un string es nulo o está vacío
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
