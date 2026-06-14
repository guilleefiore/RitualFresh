package com.ritualfresh.profiles.repository;

import com.ritualfresh.profiles.model.ClientProfile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaClientProfileRepository implements ClientProfileRepository {
    private final ClientProfileJpaRepository clientProfileJpaRepository;

    public JpaClientProfileRepository(ClientProfileJpaRepository clientProfileJpaRepository) {
        this.clientProfileJpaRepository = clientProfileJpaRepository;
    }

    @Override
    public ClientProfile save(ClientProfile profile) {
        return clientProfileJpaRepository.save(profile);
    }

    @Override
    public Optional<ClientProfile> findByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return clientProfileJpaRepository.findByUser_Id(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return userId != null && clientProfileJpaRepository.existsByUser_Id(userId);
    }
}
