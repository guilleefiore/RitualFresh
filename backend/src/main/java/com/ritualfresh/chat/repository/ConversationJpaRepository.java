package com.ritualfresh.chat.repository;

import com.ritualfresh.chat.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationJpaRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByClient_IdAndWorker_Id(Long clientId, Long workerId);

    List<Conversation> findByClient_IdOrWorker_IdOrderByLastMessageAtDescUpdatedAtDesc(Long clientId, Long workerId);
}
