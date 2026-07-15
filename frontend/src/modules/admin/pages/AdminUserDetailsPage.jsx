import { useEffect, useState } from 'react';
import { FiArrowLeft, FiCalendar, FiClock, FiEdit3, FiMail, FiPhone, FiShield, FiUser } from 'react-icons/fi';
import { Link, useParams } from 'react-router-dom';
import { UserStatusForm } from '../components/UserStatusForm.jsx';
import { ROLE_LABELS, STATUS_LABELS, StatusBadge, formatDate, getDisplayName } from '../components/UsersList.jsx';
import { getUser, getUserStatusHistory } from '../services/adminService.js';

export function AdminUserDetailsPage() {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [history, setHistory] = useState([]);
  const [historyTotal, setHistoryTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [showStatusForm, setShowStatusForm] = useState(false);

  useEffect(() => {
    let cancelled = false;
    async function loadUser() {
      setIsLoading(true);
      setError('');
      try {
        const [userData, historyData] = await Promise.all([
          getUser(userId),
          getUserStatusHistory(userId, { size: 20 }),
        ]);
        if (!cancelled) {
          setUser(userData);
          setHistory(historyData.content || []);
          setHistoryTotal(historyData.totalElements || 0);
        }
      } catch (requestError) {
        if (!cancelled) setError(requestError.message || 'No se pudo cargar la información del usuario.');
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }
    loadUser();
    return () => { cancelled = true; };
  }, [userId]);

  async function handleStatusUpdated(updatedUser) {
    setUser(updatedUser);
    setShowStatusForm(false);
    try {
      const historyData = await getUserStatusHistory(userId, { size: 20 });
      setHistory(historyData.content || []);
      setHistoryTotal(historyData.totalElements || 0);
    } catch (requestError) {
      setError(requestError.message || 'El estado se actualizó, pero no se pudo recargar el historial.');
    }
  }

  if (isLoading) {
    return <main className="admin-page"><div className="admin-detail-skeleton" aria-label="Cargando detalle"><span /><span /><span /></div></main>;
  }

  if (error && !user) {
    return (
      <main className="admin-page">
        <Link className="admin-back-link" to="/admin/users"><FiArrowLeft /> Volver a usuarios</Link>
        <div className="admin-state admin-state--error" role="alert">{error}</div>
      </main>
    );
  }

  return (
    <main className="admin-page admin-user-detail">
      <Link className="admin-back-link" to="/admin/users"><FiArrowLeft aria-hidden="true" /> Volver a usuarios</Link>

      <header className="admin-user-hero">
        <span className="admin-user-hero__avatar" aria-hidden="true">{getInitials(user)}</span>
        <div>
          <p className="admin-eyebrow">Detalle de cuenta</p>
          <h1>{getDisplayName(user)}</h1>
          <p>{user.email}</p>
        </div>
        <StatusBadge status={user.accountStatus} />
      </header>

      {error ? <div className="admin-state admin-state--error" role="alert">{error}</div> : null}

      <div className="admin-detail-grid">
        <section className="admin-panel admin-profile-panel">
          <div className="admin-panel__header">
            <div>
              <p className="admin-eyebrow">Información</p>
              <h2>Datos del usuario</h2>
            </div>
          </div>
          <dl className="admin-data-list">
            <DataItem icon={<FiUser />} label="Rol" value={ROLE_LABELS[user.role] || user.role} />
            <DataItem icon={<FiMail />} label="Correo electrónico" value={user.email} />
            <DataItem icon={<FiPhone />} label="Teléfono" value={user.phoneNumber || 'No informado'} />
            <DataItem icon={<FiShield />} label="Documento" value={user.documentNumber || 'No informado'} />
            <DataItem icon={<FiCalendar />} label="Fecha de registro" value={formatDate(user.createdAt, true)} />
            <DataItem icon={<FiClock />} label="Última desactivación" value={user.deactivatedAt ? formatDate(user.deactivatedAt, true) : 'No registra'} />
          </dl>
        </section>

        <aside className="admin-panel admin-account-panel">
          <p className="admin-eyebrow">Cuenta</p>
          <h2>Estado actual</h2>
          <div className="admin-account-status">
            <StatusBadge status={user.accountStatus} />
            <p>{getStatusDescription(user.accountStatus)}</p>
          </div>
          {user.allowedStatusTransitions?.length ? (
            <button className="admin-button admin-button--primary admin-button--full" type="button" onClick={() => setShowStatusForm(true)}>
              <FiEdit3 aria-hidden="true" /> Cambiar estado
            </button>
          ) : <p className="admin-help-text">Esta cuenta no admite cambios de estado.</p>}
        </aside>
      </div>

      <section className="admin-panel admin-history-panel">
        <div className="admin-panel__header">
          <div>
            <p className="admin-eyebrow">Trazabilidad</p>
            <h2>Historial de estados</h2>
          </div>
          <span className="admin-count-label">{historyTotal} {historyTotal === 1 ? 'cambio' : 'cambios'}</span>
        </div>
        {history.length ? (
          <ol className="admin-timeline">
            {history.map((entry) => (
              <li key={entry.id}>
                <span className="admin-timeline__marker" aria-hidden="true" />
                <div className="admin-timeline__content">
                  <div className="admin-timeline__heading">
                    <p><StatusBadge status={entry.previousStatus} /> <span aria-hidden="true">→</span> <StatusBadge status={entry.newStatus} /></p>
                    <time dateTime={entry.changedAt}>{formatDate(entry.changedAt, true)}</time>
                  </div>
                  <blockquote>{entry.reason}</blockquote>
                  <small>Realizado por {entry.actorEmail}</small>
                </div>
              </li>
            ))}
          </ol>
        ) : (
          <div className="admin-empty-state admin-empty-state--compact">
            <FiClock aria-hidden="true" />
            <strong>Sin cambios registrados</strong>
            <p>Las próximas modificaciones aparecerán en este historial.</p>
          </div>
        )}
      </section>

      {showStatusForm ? <UserStatusForm user={user} onStatusUpdated={handleStatusUpdated} onCancel={() => setShowStatusForm(false)} /> : null}
    </main>
  );
}

function DataItem({ icon, label, value }) {
  return (
    <div>
      <dt><span aria-hidden="true">{icon}</span>{label}</dt>
      <dd>{value}</dd>
    </div>
  );
}

function getInitials(user) {
  return `${user?.firstName?.[0] || user?.email?.[0] || 'U'}${user?.lastName?.[0] || ''}`.toUpperCase();
}

function getStatusDescription(status) {
  const descriptions = {
    ACTIVE: 'La persona puede iniciar sesión y utilizar las funciones correspondientes a su rol.',
    PENDING_VALIDATION: 'La cuenta todavía debe completar la validación de su correo electrónico.',
    SUSPENDED: 'El acceso está temporalmente bloqueado hasta que un administrador reactive la cuenta.',
    DELETED: 'La cuenta está desactivada y no puede acceder al sistema.',
  };
  return descriptions[status] || `Estado: ${STATUS_LABELS[status] || status}`;
}
