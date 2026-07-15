package com.ritualfresh.chat.model;

import com.ritualfresh.auth.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_presence", uniqueConstraints = @UniqueConstraint(name = "uk_chat_presence_user", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatPresence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime lastSeenAt;

    public static ChatPresence create(User user, LocalDateTime now) {
        ChatPresence presence = new ChatPresence();
        presence.user = user;
        presence.lastSeenAt = now;
        return presence;
    }

    public void heartbeat(LocalDateTime now) {
        this.lastSeenAt = now;
    }
}
