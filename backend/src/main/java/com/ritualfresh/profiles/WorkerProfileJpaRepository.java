package com.ritualfresh.profiles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerProfileJpaRepository extends JpaRepository<WorkerProfile, Long> {
    Optional<WorkerProfile> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);
}
