package com.ritualfresh.profiles.repository;

import com.ritualfresh.profiles.model.WorkerProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryWorkerProfileRepository implements WorkerProfileRepository {
    private final Map<Long, WorkerProfile> profilesByUserId = new HashMap<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public WorkerProfile save(WorkerProfile profile) {
        profile.assignIdIfMissing(sequenceIds.getAndIncrement());
        profilesByUserId.put(profile.getUser().getId(), profile);
        return profile;
    }

    @Override
    public Optional<WorkerProfile> findByUserId(Long userId) {
        return Optional.ofNullable(profilesByUserId.get(userId));
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return profilesByUserId.containsKey(userId);
    }
}
