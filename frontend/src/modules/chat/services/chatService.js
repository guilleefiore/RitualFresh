import { API_BASE_URL, apiRequest } from '../../../shared/services/apiClient.js';

export function listConversations() {
  return apiRequest('/api/chat/conversations');
}

export function createConversation(otherUserId) {
  return apiRequest('/api/chat/conversations', {
    method: 'POST',
    body: { otherUserId: Number(otherUserId) },
  });
}

export function listMessages(conversationId, beforeMessageId) {
  const query = beforeMessageId ? `?beforeMessageId=${beforeMessageId}` : '';
  return apiRequest(`/api/chat/conversations/${conversationId}/messages${query}`);
}

export function sendMessage(conversationId, content, clientMessageId) {
  return apiRequest(`/api/chat/conversations/${conversationId}/messages`, {
    method: 'POST',
    body: { content, clientMessageId },
  });
}

export function markMessagesRead(conversationId, messageIds) {
  return apiRequest(`/api/chat/conversations/${conversationId}/read`, {
    method: 'POST',
    body: { messageIds },
  });
}

export function getUnreadCount() {
  return apiRequest('/api/chat/unread-count');
}

export function sendPresenceHeartbeat() {
  return apiRequest('/api/chat/presence/heartbeat', { method: 'POST' });
}

export function openChatSocket(onEvent) {
  const url = API_BASE_URL.replace(/^http/, 'ws') + '/ws/chat';
  const socket = new WebSocket(url);

  socket.addEventListener('message', (event) => {
    try {
      onEvent(JSON.parse(event.data));
    } catch {
      // Ignore malformed real-time frames; REST remains the source of truth.
    }
  });

  return socket;
}
