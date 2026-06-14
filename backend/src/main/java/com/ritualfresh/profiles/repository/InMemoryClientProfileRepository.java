package com.ritualfresh.profiles.repository;

import com.ritualfresh.profiles.model.ClientProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryClientProfileRepository implements ClientProfileRepository {
    private final Map<Long, ClientProfile> profilesByUserId = new HashMap<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public ClientProfile save(ClientProfile profile) {
        profile.assignIdIfMissing(sequenceIds.getAndIncrement());
        profilesByUserId.put(profile.getUser().getId(), profile);
        return profile;
    }

    @Override
    public Optional<ClientProfile> findByUserId(Long userId) {
        return Optional.ofNullable(profilesByUserId.get(userId));
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return profilesByUserId.containsKey(userId);
    }
}
