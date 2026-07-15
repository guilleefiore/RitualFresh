package com.ritualfresh.chat.repository;

import com.ritualfresh.chat.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversation_IdOrderByIdDesc(Long conversationId, Pageable pageable);

    List<ChatMessage> findByConversation_IdAndIdLessThanOrderByIdDesc(Long conversationId, Long beforeMessageId, Pageable pageable);

    long countByConversation_IdAndSender_IdNotAndReadAtIsNull(Long conversationId, Long senderId);

    long countByConversation_Client_IdAndSender_IdNotAndReadAtIsNull(Long clientId, Long senderId);

    long countByConversation_Worker_IdAndSender_IdNotAndReadAtIsNull(Long workerId, Long senderId);

    List<ChatMessage> findByIdInAndConversation_IdAndSender_IdNotAndReadAtIsNull(Collection<Long> ids, Long conversationId, Long senderId);
}
