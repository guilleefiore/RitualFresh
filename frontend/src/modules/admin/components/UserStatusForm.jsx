import { useEffect, useRef, useState } from 'react';
import { FiAlertTriangle, FiX } from 'react-icons/fi';
import { updateUserStatus } from '../services/adminService.js';
import { STATUS_LABELS, getDisplayName } from './UsersList.jsx';

export function UserStatusForm({ user, onStatusUpdated, onCancel }) {
  const transitions = user.allowedStatusTransitions || [];
  const [selectedStatus, setSelectedStatus] = useState(transitions[0] || '');
  const [reason, setReason] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const selectRef = useRef(null);

  useEffect(() => {
    selectRef.current?.focus();
    function handleKeyDown(event) {
      if (event.key === 'Escape' && !isLoading) onCancel();
    }
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isLoading, onCancel]);

  async function handleSubmit(event) {
    event.preventDefault();
    const cleanReason = reason.trim();
    if (!selectedStatus || !cleanReason) {
      setError('Seleccioná un estado e indicá el motivo del cambio.');
      return;
    }

    try {
      setIsLoading(true);
      setError('');
      const updatedUser = await updateUserStatus(user.id, { status: selectedStatus, reason: cleanReason });
      onStatusUpdated?.(updatedUser);
    } catch (requestError) {
      setError(requestError.message || 'No se pudo actualizar el estado de la cuenta.');
    } finally {
      setIsLoading(false);
    }
  }

  const isDestructive = selectedStatus === 'SUSPENDED' || selectedStatus === 'DELETED';

  return (
    <div className="admin-dialog-backdrop" role="presentation" onMouseDown={(event) => {
      if (event.target === event.currentTarget && !isLoading) onCancel();
    }}>
      <section className="admin-dialog" role="dialog" aria-modal="true" aria-labelledby="status-dialog-title">
        <button className="admin-dialog__close" type="button" onClick={onCancel} disabled={isLoading} aria-label="Cerrar">
          <FiX aria-hidden="true" />
        </button>
        <span className={`admin-dialog__icon${isDestructive ? ' admin-dialog__icon--warning' : ''}`} aria-hidden="true">
          <FiAlertTriangle />
        </span>
        <p className="admin-eyebrow">Acción administrativa</p>
        <h2 id="status-dialog-title">Cambiar estado de la cuenta</h2>
        <p>Vas a modificar la cuenta de <strong>{getDisplayName(user)}</strong>. El motivo quedará guardado en el historial.</p>

        <form onSubmit={handleSubmit} className="admin-dialog__form">
          <label className="admin-field">
            <span>Nuevo estado</span>
            <select ref={selectRef} value={selectedStatus} onChange={(event) => setSelectedStatus(event.target.value)} disabled={isLoading} required>
              {transitions.map((status) => <option key={status} value={status}>{STATUS_LABELS[status] || status}</option>)}
            </select>
          </label>

          <label className="admin-field">
            <span>Motivo del cambio <small>Obligatorio</small></span>
            <textarea
              value={reason}
              onChange={(event) => setReason(event.target.value)}
              placeholder="Describí brevemente por qué se realiza este cambio"
              maxLength={500}
              rows={4}
              disabled={isLoading}
              required
            />
            <small className="admin-field__counter">{reason.length}/500</small>
          </label>

          {error ? <div className="admin-state admin-state--error" role="alert">{error}</div> : null}

          <div className="admin-dialog__actions">
            <button className="admin-button admin-button--secondary" type="button" onClick={onCancel} disabled={isLoading}>Cancelar</button>
            <button className={`admin-button${isDestructive ? ' admin-button--danger' : ' admin-button--primary'}`} type="submit" disabled={isLoading || !selectedStatus || !reason.trim()}>
              {isLoading ? 'Guardando...' : 'Confirmar cambio'}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
