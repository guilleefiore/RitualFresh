import { useState } from 'react';
import { Link } from 'react-router-dom';
import { UserStatusForm } from './UserStatusForm.jsx';
import '../styles/usersList.css';

const ROLE_LABELS = {
  ADMIN: 'Administrador',
  CLIENT: 'Cliente',
  WORKER: 'Trabajador',
};

const STATUS_LABELS = {
  ACTIVE: 'Activo',
  PENDING_VALIDATION: 'Pendiente',
  SUSPENDED: 'Suspendido',
  DELETED: 'Eliminado',
};

const STATUS_COLORS = {
  ACTIVE: 'success',
  PENDING_VALIDATION: 'warning',
  SUSPENDED: 'error',
  DELETED: 'info',
};

export function UsersList({ users, onUserUpdated }) {
  const [editingUserId, setEditingUserId] = useState(null);

  if (!users || users.length === 0) {
    return <p className="empty-state">No hay usuarios para mostrar</p>;
  }

  const handleStatusFormCancel = () => {
    setEditingUserId(null);
  };

  const handleStatusUpdated = () => {
    setEditingUserId(null);
    if (onUserUpdated) {
      onUserUpdated();
    }
  };

  return (
    <div className="users-list">
      <table className="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Email</th>
            <th>Rol</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.firstName} {user.lastName}</td>
              <td>{user.email}</td>
              <td>{ROLE_LABELS[user.role] || user.role}</td>
              <td>
                <span className={`badge badge-${STATUS_COLORS[user.accountStatus]}`}>
                  {STATUS_LABELS[user.accountStatus] || user.accountStatus}
                </span>
              </td>
              <td>
                <Link to={`/admin/users/${user.id}`} className="btn-link">
                  Ver detalles
                </Link>
                {editingUserId === user.id ? (
                  <div className="status-form-inline">
                    <UserStatusForm
                      user={user}
                      onStatusUpdated={handleStatusUpdated}
                      onCancel={handleStatusFormCancel}
                    />
                  </div>
                ) : (
                  <button
                    className="btn-link secondary"
                    onClick={() => setEditingUserId(user.id)}
                  >
                    Cambiar estado
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
