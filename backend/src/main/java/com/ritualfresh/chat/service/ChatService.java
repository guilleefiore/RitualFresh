package com.ritualfresh.chat.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.chat.dto.ConversationApiResponse;
import com.ritualfresh.chat.dto.MessageApiResponse;
import com.ritualfresh.chat.dto.PresenceApiResponse;
import com.ritualfresh.chat.dto.ReadMessagesApiResponse;
import com.ritualfresh.chat.model.ChatMessage;
import com.ritualfresh.chat.model.ChatPresence;
import com.ritualfresh.chat.model.Conversation;
import com.ritualfresh.chat.repository.ChatMessageJpaRepository;
import com.ritualfresh.chat.repository.ChatPresenceJpaRepository;
import com.ritualfresh.chat.repository.ConversationJpaRepository;
import com.ritualfresh.chat.websocket.ChatWebSocketHub;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    public static final int MESSAGE_PAGE_SIZE = 50;
    public static final int MESSAGE_MAX_LENGTH = 500;
    private static final Duration ONLINE_WINDOW = Duration.ofSeconds(35);

    private final UserService userService;
    private final UserRepository userRepository;
    private final ConversationJpaRepository conversationRepository;
    private final ChatMessageJpaRepository messageRepository;
    private final ChatPresenceJpaRepository presenceRepository;
    private final ChatAccessPolicy chatAccessPolicy;
    private final ChatWebSocketHub webSocketHub;

    @Transactional
    public ConversationApiResponse createOrReactivateConversation(Long otherUserId) {
        User currentUser = userService.getAuthenticatedUser();
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new BusinessRuleException("El usuario indicado no existe."));
        chatAccessPolicy.validateCanCreateOrReactivate(currentUser, otherUser);

        User client = currentUser.getRole() == UserRole.CLIENT ? currentUser : otherUser;
        User worker = currentUser.getRole() == UserRole.WORKER ? currentUser : otherUser;
        LocalDateTime now = LocalDateTime.now();

        Conversation conversation = conversationRepository.findByClient_IdAndWorker_Id(client.getId(), worker.getId())
                .map(existing -> {
                    existing.reactivate(now);
                    return existing;
                })
                .orElseGet(() -> Conversation.active(client, worker, now));

        Conversation saved = conversationRepository.save(conversation);
        ConversationApiResponse response = toConversationResponse(saved, currentUser);
        sendConversationUpdated(saved);
        return response;
    }

    @Transactional(readOnly = true)
    public List<ConversationApiResponse> listConversations() {
        User currentUser = userService.getAuthenticatedUser();
        return conversationRepository.findByClient_IdOrWorker_IdOrderByLastMessageAtDescUpdatedAtDesc(
                        currentUser.getId(), currentUser.getId()).stream()
                .map(conversation -> toConversationResponse(conversation, currentUser))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageApiResponse> listMessages(Long conversationId, Long beforeMessageId) {
        User currentUser = userService.getAuthenticatedUser();
        Conversation conversation = requireConversationForUser(conversationId, currentUser);
        List<ChatMessage> messages = beforeMessageId == null
                ? messageRepository.findByConversation_IdOrderByIdDesc(conversation.getId(), PageRequest.of(0, MESSAGE_PAGE_SIZE))
                : messageRepository.findByConversation_IdAndIdLessThanOrderByIdDesc(conversation.getId(), beforeMessageId, PageRequest.of(0, MESSAGE_PAGE_SIZE));
        List<ChatMessage> chronological = new ArrayList<>(messages);
        Collections.reverse(chronological);
        return chronological.stream().map(MessageApiResponse::from).toList();
    }

    @Transactional
    public MessageApiResponse sendMessage(Long conversationId, String content, String clientMessageId) {
        User currentUser = userService.getAuthenticatedUser();
        Conversation conversation = requireConversationForUser(conversationId, currentUser);
        chatAccessPolicy.validateCanSendMessage(conversation, currentUser);

        String normalizedContent = normalizeContent(content);
        LocalDateTime now = LocalDateTime.now();
        ChatMessage message = ChatMessage.create(conversation, currentUser, normalizedContent, now);
        ChatMessage savedMessage = messageRepository.save(message);
        conversation.registerMessage(now);
        conversationRepository.save(conversation);

        MessageApiResponse response = MessageApiResponse.from(savedMessage, clientMessageId);
        webSocketHub.sendToUsers(conversation.getClient().getId(), conversation.getWorker().getId(), "MESSAGE_CREATED", response);
        sendConversationUpdated(conversation);
        return response;
    }

    @Transactional
    public ReadMessagesApiResponse markMessagesRead(Long conversationId, List<Long> messageIds) {
        User currentUser = userService.getAuthenticatedUser();
        Conversation conversation = requireConversationForUser(conversationId, currentUser);
        LocalDateTime readAt = LocalDateTime.now();
        List<ChatMessage> messages = messageRepository.findByIdInAndConversation_IdAndSender_IdNotAndReadAtIsNull(
                messageIds,
                conversation.getId(),
                currentUser.getId());
        messages.forEach(message -> message.markRead(readAt));
        messageRepository.saveAll(messages);
        List<Long> readMessageIds = messages.stream().map(ChatMessage::getId).toList();
        ReadMessagesApiResponse response = new ReadMessagesApiResponse(readMessageIds, readAt);
        if (!readMessageIds.isEmpty()) {
            webSocketHub.sendToUsers(conversation.getClient().getId(), conversation.getWorker().getId(), "MESSAGE_READ", response);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public long countUnreadMessages() {
        User currentUser = userService.getAuthenticatedUser();
        if (currentUser.getRole() == UserRole.CLIENT) {
            return messageRepository.countByConversation_Client_IdAndSender_IdNotAndReadAtIsNull(currentUser.getId(), currentUser.getId());
        }
        if (currentUser.getRole() == UserRole.WORKER) {
            return messageRepository.countByConversation_Worker_IdAndSender_IdNotAndReadAtIsNull(currentUser.getId(), currentUser.getId());
        }
        return 0;
    }

    @Transactional
    public PresenceApiResponse heartbeat() {
        User currentUser = userService.getAuthenticatedUser();
        LocalDateTime now = LocalDateTime.now();
        ChatPresence presence = presenceRepository.findByUser_Id(currentUser.getId())
                .map(existing -> {
                    existing.heartbeat(now);
                    return existing;
                })
                .orElseGet(() -> ChatPresence.create(currentUser, now));
        presenceRepository.save(presence);
        PresenceApiResponse response = new PresenceApiResponse(true, now);
        webSocketHub.sendToUser(currentUser.getId(), "PRESENCE_CHANGED", response);
        return response;
    }

    private Conversation requireConversationForUser(Long conversationId, User currentUser) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessRuleException("La conversacion indicada no existe."));
        if (!conversation.hasParticipant(currentUser.getId())) {
            throw new BusinessRuleException("No pertenece a esta conversacion.");
        }
        return conversation;
    }

    private ConversationApiResponse toConversationResponse(Conversation conversation, User currentUser) {
        long unreadCount = messageRepository.countByConversation_IdAndSender_IdNotAndReadAtIsNull(
                conversation.getId(), currentUser.getId());
        User otherUser = conversation.otherParticipant(currentUser.getId());
        return ConversationApiResponse.from(conversation, currentUser, unreadCount, presenceOf(otherUser));
    }

    private PresenceApiResponse presenceOf(User user) {
        LocalDateTime lastSeenAt = presenceRepository.findByUser_Id(user.getId())
                .map(ChatPresence::getLastSeenAt)
                .orElse(null);
        boolean online = lastSeenAt != null && Duration.between(lastSeenAt, LocalDateTime.now()).compareTo(ONLINE_WINDOW) <= 0;
        return new PresenceApiResponse(online, lastSeenAt);
    }

    private void sendConversationUpdated(Conversation conversation) {
        webSocketHub.sendToUser(
                conversation.getClient().getId(),
                "CONVERSATION_UPDATED",
                toConversationResponse(conversation, conversation.getClient()));
        webSocketHub.sendToUser(
                conversation.getWorker().getId(),
                "CONVERSATION_UPDATED",
                toConversationResponse(conversation, conversation.getWorker()));
    }

    private String normalizeContent(String content) {
        String normalized = content == null ? "" : content.trim();
        if (normalized.isBlank()) {
            throw new BusinessRuleException("No puede enviar mensajes vacios.");
        }
        if (normalized.length() > MESSAGE_MAX_LENGTH) {
            throw new BusinessRuleException("El mensaje no puede superar los 500 caracteres.");
        }
        return normalized;
    }
}
