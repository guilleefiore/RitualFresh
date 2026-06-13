package com.ritualfresh.profiles;

import java.util.Optional;

public interface WorkerProfileRepository {
    WorkerProfile save(WorkerProfile profile);

    Optional<WorkerProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
