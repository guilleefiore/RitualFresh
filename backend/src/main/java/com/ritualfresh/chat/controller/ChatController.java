package com.ritualfresh.chat.controller;

import com.ritualfresh.chat.dto.ConversationApiResponse;
import com.ritualfresh.chat.dto.CreateConversationApiRequest;
import com.ritualfresh.chat.dto.MessageApiResponse;
import com.ritualfresh.chat.dto.PresenceApiResponse;
import com.ritualfresh.chat.dto.ReadMessagesApiRequest;
import com.ritualfresh.chat.dto.ReadMessagesApiResponse;
import com.ritualfresh.chat.dto.SendMessageApiRequest;
import com.ritualfresh.chat.dto.UnreadCountApiResponse;
import com.ritualfresh.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/conversations")
    public List<ConversationApiResponse> listConversations() {
        return chatService.listConversations();
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationApiResponse createConversation(@Valid @RequestBody CreateConversationApiRequest request) {
        return chatService.createOrReactivateConversation(request.otherUserId());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageApiResponse> listMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long beforeMessageId) {
        return chatService.listMessages(conversationId, beforeMessageId);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageApiResponse sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageApiRequest request) {
        return chatService.sendMessage(conversationId, request.content(), request.clientMessageId());
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ReadMessagesApiResponse markMessagesRead(
            @PathVariable Long conversationId,
            @Valid @RequestBody ReadMessagesApiRequest request) {
        return chatService.markMessagesRead(conversationId, request.messageIds());
    }

    @GetMapping("/unread-count")
    public UnreadCountApiResponse unreadCount() {
        return new UnreadCountApiResponse(chatService.countUnreadMessages());
    }

    @PostMapping("/presence/heartbeat")
    public PresenceApiResponse heartbeat() {
        return chatService.heartbeat();
    }
}
