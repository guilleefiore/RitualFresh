package com.ritualfresh.profiles;

import java.util.Optional;

public interface ClientProfileRepository {
    ClientProfile save(ClientProfile profile);

    Optional<ClientProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
