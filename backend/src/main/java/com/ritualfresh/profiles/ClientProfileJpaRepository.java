package com.ritualfresh.profiles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientProfileJpaRepository extends JpaRepository<ClientProfile, Long> {
    Optional<ClientProfile> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);
}
