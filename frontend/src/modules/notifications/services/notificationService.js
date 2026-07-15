import { API_BASE_URL, apiRequest } from '../../../shared/services/apiClient.js';

export function getRecentNotifications(options = {}) {
  return apiRequest('/api/notifications/recent', options);
}

export function markNotificationRead(notificationId) {
  return apiRequest(`/api/notifications/${notificationId}/read`, { method: 'PATCH' });
}

export function markAllNotificationsRead() {
  return apiRequest('/api/notifications/read-all', { method: 'PATCH' });
}

export function openNotificationSocket({ onEvent, onConnected }) {
  const socketUrl = API_BASE_URL.replace(/^http/, 'ws') + '/ws/notifications';
  let socket = null;
  let reconnectTimer = null;
  let reconnectAttempts = 0;
  let connectedBefore = false;
  let closedByClient = false;

  function connect() {
    if (closedByClient) return;

    socket = new WebSocket(socketUrl);
    socket.onopen = () => {
      const reconnected = connectedBefore;
      connectedBefore = true;
      reconnectAttempts = 0;
      onConnected?.({ reconnected });
    };
    socket.onmessage = (message) => {
      try {
        onEvent?.(JSON.parse(message.data));
      } catch {
        // Se ignoran mensajes que no respeten el contrato JSON del canal.
      }
    };
    socket.onclose = () => {
      if (closedByClient) return;
      const delay = Math.min(1000 * (2 ** reconnectAttempts), 15000);
      reconnectAttempts += 1;
      reconnectTimer = window.setTimeout(connect, delay);
    };
  }

  connect();

  return {
    close() {
      closedByClient = true;
      if (reconnectTimer) window.clearTimeout(reconnectTimer);
      if (socket && socket.readyState < WebSocket.CLOSING) socket.close();
    },
  };
}
