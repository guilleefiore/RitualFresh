package com.ritualfresh.chat.repository;

import com.ritualfresh.chat.model.ChatPresence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatPresenceJpaRepository extends JpaRepository<ChatPresence, Long> {
    Optional<ChatPresence> findByUser_Id(Long userId);
}
