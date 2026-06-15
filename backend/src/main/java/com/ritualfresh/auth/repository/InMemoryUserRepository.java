package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
// Se usa en tests para simular persistencia sin depender de JPA ni PostgreSQL.
public class InMemoryUserRepository implements UserRepository {
    // Indice por email para resolver busquedas y validaciones rapido.
    private final Map<String, User> usersByEmail = new HashMap<>();
    // Indice por id para recuperar usuarios y listar el contenido guardado.
    private final Map<Long, User> usersById = new HashMap<>();
    // Simula el autoincremental que en produccion genera la base de datos.
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    // Guarda o actualiza un usuario en memoria.
    public User save(User user) {
        user.assignIdIfMissing(sequenceIds.getAndIncrement());
        usersByEmail.put(normalizeEmail(user.getEmail()), user);
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    // Devuelve una copia de los usuarios almacenados.
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    // Busca un usuario por su identificador.
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    // Busca un usuario por email usando el mismo formato normalizado.
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(normalizeEmail(email)));
    }

    @Override
    // Recorre los usuarios guardados hasta encontrar el token de validacion.
    public Optional<User> findByAccountValidationToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usersByEmail.values().stream()
                .filter(user -> token.equals(user.getAccountValidationToken()))
                .findFirst();
    }

    @Override
    // Recorre los usuarios guardados hasta encontrar el token de reseteo.
    public Optional<User> findByPasswordResetToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usersByEmail.values().stream()
                .filter(user -> token.equals(user.getPasswordResetToken()))
                .findFirst();
    }

    @Override
    // Verifica si ya existe un usuario con ese email en memoria.
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(normalizeEmail(email));
    }

    // Evita diferencias por espacios o mayusculas al comparar emails.
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
