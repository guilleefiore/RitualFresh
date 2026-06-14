package com.ritualfresh.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ritualfresh.auth.model.UserSession;

import java.util.Optional;

public interface UserSessionJpaRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);
}
