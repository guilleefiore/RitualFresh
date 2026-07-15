import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FiAlertCircle,
  FiBriefcase,
  FiCheck,
  FiCheckCircle,
  FiCreditCard,
  FiFlag,
  FiRefreshCw,
  FiX,
} from 'react-icons/fi';
import { useNotifications } from '../hooks/useNotifications.js';

export function NotificationPanel({ onClose }) {
  const navigate = useNavigate();
  const {
    notifications,
    unreadCount,
    isLoading,
    isMarkingAll,
    busyNotificationIds,
    error,
    refresh,
    markOneAsRead,
    markAllAsRead,
  } = useNotifications();
  const [informationMessage, setInformationMessage] = useState('');
  const closeButtonRef = useRef(null);

  useEffect(() => {
    closeButtonRef.current?.focus();
  }, []);

  async function handleNotificationClick(notificationId) {
    setInformationMessage('');
    try {
      const response = await markOneAsRead(notificationId);
      if (response.destination?.available && response.destination.path) {
        onClose();
        navigate(response.destination.path);
        return;
      }
      setInformationMessage(
        response.destination?.message || 'El contenido ya no se encuentra disponible.'
      );
    } catch {
      // El provider conserva el error y revierte la actualización optimista.
    }
  }

  async function handleMarkAll() {
    setInformationMessage('');
    try {
      await markAllAsRead();
    } catch {
      // El provider muestra el error correspondiente.
    }
  }

  return (
    <section
      className="notification-panel"
      role="dialog"
      aria-modal="false"
      aria-labelledby="notification-panel-title"
    >
      <header className="notification-panel__header">
        <div>
          <p className="notification-panel__eyebrow">Actividad reciente</p>
          <h2 id="notification-panel-title">Notificaciones</h2>
        </div>
        <div className="notification-panel__actions">
          <button
            className="notification-panel__read-all"
            type="button"
            onClick={handleMarkAll}
            disabled={unreadCount === 0 || isMarkingAll}
          >
            <FiCheck aria-hidden="true" />
            <span>{isMarkingAll ? 'Marcando...' : 'Marcar todas como leídas'}</span>
          </button>
          <button
            ref={closeButtonRef}
            className="notification-panel__close"
            type="button"
            aria-label="Cerrar notificaciones"
            onClick={onClose}
          >
            <FiX aria-hidden="true" />
          </button>
        </div>
      </header>

      {informationMessage ? (
        <p className="notification-panel__notice" role="status">
          <FiAlertCircle aria-hidden="true" />
          {informationMessage}
        </p>
      ) : null}

      {error ? (
        <div className="notification-panel__error" role="alert">
          <span>{error}</span>
          <button type="button" onClick={() => refresh().catch(() => null)}>
            <FiRefreshCw aria-hidden="true" />
            Reintentar
          </button>
        </div>
      ) : null}

      <div className="notification-panel__body">
        {isLoading ? <NotificationLoadingState /> : null}
        {!isLoading && notifications.length === 0 ? <NotificationEmptyState /> : null}
        {!isLoading && notifications.length > 0 ? (
          <ol className="notification-list" aria-label="Notificaciones recientes">
            {notifications.map((notification) => (
              <li key={notification.id} className="notification-list__entry">
                <button
                  className={`notification-item${notification.read ? '' : ' notification-item--unread'}`}
                  type="button"
                  onClick={() => handleNotificationClick(notification.id)}
                  disabled={busyNotificationIds.includes(notification.id)}
                >
                  <span className={`notification-item__marker notification-item__marker--${typeClass(notification.type)}`}>
                    <NotificationTypeIcon type={notification.type} />
                  </span>
                  <span className="notification-item__content">
                    <span className="notification-item__heading">
                      <strong>{notification.title}</strong>
                      {!notification.read ? (
                        <span className="notification-item__unread-dot">
                          <span className="notification-sr-only">No leída</span>
                        </span>
                      ) : null}
                    </span>
                    <span className="notification-item__message">{notification.message}</span>
                    <time dateTime={notification.createdAt}>{formatTimestamp(notification.createdAt)}</time>
                  </span>
                </button>
              </li>
            ))}
          </ol>
        ) : null}
      </div>

      <footer className="notification-panel__footer">
        Se muestran las 20 notificaciones más recientes.
      </footer>
    </section>
  );
}

function NotificationTypeIcon({ type }) {
  if (type === 'PAYMENT_APPROVED') return <FiCreditCard aria-hidden="true" />;
  if (type === 'CLAIM_RESOLVED') return <FiFlag aria-hidden="true" />;
  return <FiBriefcase aria-hidden="true" />;
}

function NotificationLoadingState() {
  return (
    <div className="notification-loading" role="status" aria-label="Cargando notificaciones">
      <span />
      <span />
      <span />
    </div>
  );
}

function NotificationEmptyState() {
  return (
    <div className="notification-empty">
      <span className="notification-empty__icon"><FiCheckCircle aria-hidden="true" /></span>
      <strong>No tienes notificaciones recientes</strong>
      <p>Las novedades de tus servicios y actividades aparecerán aquí.</p>
    </div>
  );
}

function typeClass(type) {
  if (type === 'PAYMENT_APPROVED') return 'payment';
  if (type === 'CLAIM_RESOLVED') return 'claim';
  return 'service';
}

function formatTimestamp(value) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return 'Fecha no disponible';
  return new Intl.DateTimeFormat('es-AR', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}
