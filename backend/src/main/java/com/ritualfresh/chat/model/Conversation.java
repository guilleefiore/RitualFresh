package com.ritualfresh.chat.model;

import com.ritualfresh.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_conversations",
        uniqueConstraints = @UniqueConstraint(name = "uk_chat_conversation_pair", columnNames = {"client_id", "worker_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime lastMessageAt;

    public static Conversation active(User client, User worker, LocalDateTime now) {
        Conversation conversation = new Conversation();
        conversation.client = client;
        conversation.worker = worker;
        conversation.status = ConversationStatus.ACTIVE;
        conversation.createdAt = now;
        conversation.updatedAt = now;
        return conversation;
    }

    public void reactivate(LocalDateTime now) {
        this.status = ConversationStatus.ACTIVE;
        this.updatedAt = now;
    }

    public void markReadOnly(LocalDateTime now) {
        this.status = ConversationStatus.READ_ONLY;
        this.updatedAt = now;
    }

    public void registerMessage(LocalDateTime sentAt) {
        this.lastMessageAt = sentAt;
        this.updatedAt = sentAt;
    }

    public boolean hasParticipant(Long userId) {
        return client.getId().equals(userId) || worker.getId().equals(userId);
    }

    public User otherParticipant(Long userId) {
        return client.getId().equals(userId) ? worker : client;
    }
}
