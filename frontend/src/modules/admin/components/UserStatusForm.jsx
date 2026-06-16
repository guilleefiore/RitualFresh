import { useState } from 'react';
import { updateUserStatus } from '../services/adminService.js';

const STATUS_OPTIONS = [
  { value: 'ACTIVE', label: 'Activo' },
  { value: 'PENDING_VALIDATION', label: 'Pendiente de validación' },
  { value: 'SUSPENDED', label: 'Suspendido' },
  { value: 'DELETED', label: 'Eliminado' },
];

export function UserStatusForm({ user, onStatusUpdated, onCancel }) {
  const [selectedStatus, setSelectedStatus] = useState(user.accountStatus);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (selectedStatus === user.accountStatus) {
      onCancel();
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      
      await updateUserStatus(user.id, { status: selectedStatus });
      
      if (onStatusUpdated) {
        onStatusUpdated();
      }
    } catch (err) {
      setError(err.message || 'Error al actualizar el estado');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="status">Nuevo estado:</label>
        <select
          id="status"
          value={selectedStatus}
          onChange={(e) => setSelectedStatus(e.target.value)}
          disabled={isLoading}
        >
          {STATUS_OPTIONS.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>

      {error && <p className="error-message">{error}</p>}

      <div className="form-actions">
        <button
          type="submit"
          disabled={isLoading || selectedStatus === user.accountStatus}
        >
          {isLoading ? 'Actualizando...' : 'Actualizar estado'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          disabled={isLoading}
          className="secondary"
        >
          Cancelar
        </button>
      </div>
    </form>
  );
}
