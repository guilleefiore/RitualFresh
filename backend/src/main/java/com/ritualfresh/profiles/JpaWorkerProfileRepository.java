package com.ritualfresh.profiles;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaWorkerProfileRepository implements WorkerProfileRepository {
    private final WorkerProfileJpaRepository workerProfileJpaRepository;

    public JpaWorkerProfileRepository(WorkerProfileJpaRepository workerProfileJpaRepository) {
        this.workerProfileJpaRepository = workerProfileJpaRepository;
    }

    @Override
    public WorkerProfile save(WorkerProfile profile) {
        return workerProfileJpaRepository.save(profile);
    }

    @Override
    public Optional<WorkerProfile> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return workerProfileJpaRepository.findByUser_Id(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return userId != null && workerProfileJpaRepository.existsByUser_Id(userId);
    }
}
