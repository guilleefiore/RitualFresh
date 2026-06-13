package com.ritualfresh.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionJpaRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);
}
