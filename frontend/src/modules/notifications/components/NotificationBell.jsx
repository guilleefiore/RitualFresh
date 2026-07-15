import { useEffect, useRef, useState } from 'react';
import { FiBell } from 'react-icons/fi';
import { useNotifications } from '../hooks/useNotifications.js';
import { NotificationPanel } from './NotificationPanel.jsx';
import '../styles/notifications.css';

export function NotificationBell() {
  const { unreadCount } = useNotifications();
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef(null);
  const buttonRef = useRef(null);

  useEffect(() => {
    if (!isOpen) return undefined;

    function handlePointerDown(event) {
      if (!containerRef.current?.contains(event.target)) closePanel(false);
    }

    function handleKeyDown(event) {
      if (event.key === 'Escape') closePanel(true);
    }

    document.addEventListener('pointerdown', handlePointerDown);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('pointerdown', handlePointerDown);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen]);

  function closePanel(returnFocus = true) {
    setIsOpen(false);
    if (returnFocus) window.requestAnimationFrame(() => buttonRef.current?.focus());
  }

  const countLabel = unreadCount === 1
    ? '1 notificación sin leer'
    : `${unreadCount} notificaciones sin leer`;

  return (
    <div className="notification-center" ref={containerRef}>
      <button
        ref={buttonRef}
        className={`notification-bell${isOpen ? ' notification-bell--open' : ''}`}
        type="button"
        aria-label={unreadCount > 0 ? `Notificaciones: ${countLabel}` : 'Notificaciones'}
        aria-haspopup="dialog"
        aria-expanded={isOpen}
        onClick={() => setIsOpen((current) => !current)}
      >
        <FiBell aria-hidden="true" />
        {unreadCount > 0 ? (
          <span className="notification-bell__badge" aria-hidden="true">{unreadCount}</span>
        ) : null}
      </button>

      {isOpen ? <NotificationPanel onClose={() => closePanel(true)} /> : null}
    </div>
  );
}
