import { Link } from 'react-router-dom';
import { FiArrowRight, FiUser } from 'react-icons/fi';
import '../styles/usersList.css';

export const ROLE_LABELS = {
  CLIENT: 'Cliente',
  WORKER: 'Trabajador',
  ADMIN: 'Administrador',
};

export const STATUS_LABELS = {
  ACTIVE: 'Activo',
  PENDING_VALIDATION: 'Pendiente',
  SUSPENDED: 'Suspendido',
  DELETED: 'Eliminado',
};

export function UsersList({ users, emptyMessage = 'No encontramos usuarios con esos criterios.' }) {
  if (!users?.length) {
    return (
      <div className="admin-empty-state">
        <span aria-hidden="true"><FiUser /></span>
        <strong>Sin resultados</strong>
        <p>{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="users-list">
      <div className="users-table-wrapper">
        <table className="users-table">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Registrado</th>
              <th><span className="sr-only">Acciones</span></th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>
                  <div className="user-cell">
                    <span className="user-cell__avatar" aria-hidden="true">{getInitials(user)}</span>
                    <div>
                      <strong>{getDisplayName(user)}</strong>
                      <span>{user.email}</span>
                    </div>
                  </div>
                </td>
                <td>{ROLE_LABELS[user.role] || user.role}</td>
                <td><StatusBadge status={user.accountStatus} /></td>
                <td>{formatDate(user.createdAt)}</td>
                <td>
                  <Link to={`/admin/users/${user.id}`} className="admin-row-action">
                    <span>Ver usuario</span>
                    <FiArrowRight aria-hidden="true" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="user-cards">
        {users.map((user) => (
          <article key={user.id} className="user-card-mobile">
            <div className="user-cell">
              <span className="user-cell__avatar" aria-hidden="true">{getInitials(user)}</span>
              <div>
                <strong>{getDisplayName(user)}</strong>
                <span>{user.email}</span>
              </div>
            </div>
            <div className="user-card-mobile__meta">
              <span>{ROLE_LABELS[user.role] || user.role}</span>
              <StatusBadge status={user.accountStatus} />
            </div>
            <Link to={`/admin/users/${user.id}`} className="admin-row-action">
              <span>Ver usuario</span>
              <FiArrowRight aria-hidden="true" />
            </Link>
          </article>
        ))}
      </div>
    </div>
  );
}

export function StatusBadge({ status }) {
  return <span className={`status-badge status-badge--${String(status).toLowerCase()}`}>{STATUS_LABELS[status] || status}</span>;
}

export function getDisplayName(user) {
  const name = `${user?.firstName || ''} ${user?.lastName || ''}`.trim();
  return name || 'Sin nombre cargado';
}

export function formatDate(value, includeTime = false) {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat('es-AR', includeTime
    ? { dateStyle: 'medium', timeStyle: 'short' }
    : { dateStyle: 'medium' }).format(date);
}

function getInitials(user) {
  const first = user?.firstName?.trim()?.[0] || user?.email?.[0] || 'U';
  const last = user?.lastName?.trim()?.[0] || '';
  return `${first}${last}`.toUpperCase();
}
