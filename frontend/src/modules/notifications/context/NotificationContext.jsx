import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import { useAuth } from '../../auth/hooks/useAuth.js';
import {
  getRecentNotifications,
  markAllNotificationsRead,
  markNotificationRead,
  openNotificationSocket,
} from '../services/notificationService.js';

export const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const { isAuthenticated, isAuthReady, user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [isMarkingAll, setIsMarkingAll] = useState(false);
  const [busyNotificationIds, setBusyNotificationIds] = useState([]);
  const [error, setError] = useState('');

  const applyPanelResponse = useCallback((response) => {
    setNotifications(Array.isArray(response?.items) ? response.items : []);
    setUnreadCount(Number(response?.unreadCount || 0));
  }, []);

  const refresh = useCallback(async (options = {}) => {
    const response = await getRecentNotifications(options);
    applyPanelResponse(response);
    setError('');
    return response;
  }, [applyPanelResponse]);

  useEffect(() => {
    if (!isAuthReady || !isAuthenticated || !user?.id) {
      setNotifications([]);
      setUnreadCount(0);
      setIsLoading(false);
      setError('');
      return undefined;
    }

    let active = true;
    const controller = new AbortController();
    setIsLoading(true);

    refresh({ signal: controller.signal })
      .catch((requestError) => {
        if (active && requestError?.name !== 'AbortError') {
          setError(requestError.message || 'No se pudieron cargar las notificaciones.');
        }
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    const connection = openNotificationSocket({
      onEvent: (event) => {
        if (!active) return;
        applyRealtimeEvent(event, setNotifications, setUnreadCount);
      },
      onConnected: ({ reconnected }) => {
        if (active && reconnected) {
          refresh().catch(() => setError('No se pudieron actualizar las notificaciones.'));
        }
      },
    });

    return () => {
      active = false;
      controller.abort();
      connection.close();
    };
  }, [isAuthenticated, isAuthReady, user?.id, refresh]);

  async function markOneAsRead(notificationId) {
    const previousNotifications = notifications;
    const previousUnreadCount = unreadCount;
    const selected = notifications.find((notification) => notification.id === notificationId);
    const optimisticReadAt = new Date().toISOString();

    setBusyNotificationIds((current) => [...new Set([...current, notificationId])]);
    setError('');
    if (selected && !selected.read) {
      setNotifications((current) => current.map((notification) => (
        notification.id === notificationId
          ? { ...notification, read: true, readAt: optimisticReadAt }
          : notification
      )));
      setUnreadCount((current) => Math.max(0, current - 1));
    }

    try {
      const response = await markNotificationRead(notificationId);
      setNotifications((current) => current.map((notification) => (
        notification.id === notificationId ? response.notification : notification
      )));
      setUnreadCount(Number(response.unreadCount || 0));
      return response;
    } catch (requestError) {
      setNotifications(previousNotifications);
      setUnreadCount(previousUnreadCount);
      setError(requestError.message || 'No se pudo marcar la notificación como leída.');
      throw requestError;
    } finally {
      setBusyNotificationIds((current) => current.filter((id) => id !== notificationId));
    }
  }

  async function markAllAsRead() {
    const previousNotifications = notifications;
    const previousUnreadCount = unreadCount;
    const optimisticReadAt = new Date().toISOString();

    setIsMarkingAll(true);
    setError('');
    setNotifications((current) => current.map((notification) => (
      notification.read ? notification : { ...notification, read: true, readAt: optimisticReadAt }
    )));
    setUnreadCount(0);

    try {
      const response = await markAllNotificationsRead();
      setUnreadCount(Number(response.unreadCount || 0));
      return response;
    } catch (requestError) {
      setNotifications(previousNotifications);
      setUnreadCount(previousUnreadCount);
      setError(requestError.message || 'No se pudieron marcar las notificaciones como leídas.');
      throw requestError;
    } finally {
      setIsMarkingAll(false);
    }
  }

  const value = useMemo(() => ({
    notifications,
    unreadCount,
    isLoading,
    isMarkingAll,
    busyNotificationIds,
    error,
    refresh,
    markOneAsRead,
    markAllAsRead,
  }), [
    notifications,
    unreadCount,
    isLoading,
    isMarkingAll,
    busyNotificationIds,
    error,
    refresh,
  ]);

  return <NotificationContext.Provider value={value}>{children}</NotificationContext.Provider>;
}

function applyRealtimeEvent(event, setNotifications, setUnreadCount) {
  if (!event?.type || !event.payload) return;

  if (event.type === 'notification.created' && event.payload.notification) {
    setNotifications((current) => [
      event.payload.notification,
      ...current.filter((notification) => notification.id !== event.payload.notification.id),
    ].sort(compareNotifications).slice(0, 20));
    setUnreadCount(Number(event.payload.unreadCount || 0));
  }

  if (event.type === 'notification.read') {
    setNotifications((current) => current.map((notification) => (
      notification.id === event.payload.notificationId
        ? { ...notification, read: true, readAt: event.payload.readAt }
        : notification
    )));
    setUnreadCount(Number(event.payload.unreadCount || 0));
  }

  if (event.type === 'notifications.read-all') {
    setNotifications((current) => current.map((notification) => (
      notification.read ? notification : { ...notification, read: true, readAt: event.payload.readAt }
    )));
    setUnreadCount(Number(event.payload.unreadCount || 0));
  }
}

function compareNotifications(first, second) {
  const dateDifference = new Date(second.createdAt).getTime() - new Date(first.createdAt).getTime();
  return dateDifference || Number(second.id) - Number(first.id);
}
