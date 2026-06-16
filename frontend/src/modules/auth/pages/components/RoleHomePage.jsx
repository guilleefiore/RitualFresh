import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth.js';

export function RoleHomePage({ roleLabel, title }) {
  const navigate = useNavigate();
  const { user, accountStatus, sessionExpiresAt, logout, deleteAccount } = useAuth();
  const [errorMessage, setErrorMessage] = useState('');
  const [busyAction, setBusyAction] = useState('');

  async function handleLogout() {
    setBusyAction('logout');
    setErrorMessage('');

    try {
      await logout();
      navigate('/login', {
        replace: true,
        state: { message: 'Sesión cerrada correctamente.' },
      });
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setBusyAction('');
    }
  }

  async function handleDeleteAccount() {
    const confirmed = window.confirm('¿Confirma que desea eliminar su cuenta?');
    if (!confirmed) {
      return;
    }

    setBusyAction('delete');
    setErrorMessage('');

    try {
      const response = await deleteAccount();
      navigate('/login', {
        replace: true,
        state: { message: response.message },
      });
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setBusyAction('');
    }
  }

  return (
    <main className="screen dashboard-screen">
      <section className="card dashboard-card">
        <p className="eyebrow">{roleLabel}</p>
        <h1>{title}</h1>
        <p className="muted">Pantalla mínima para validar navegación y sesión del módulo de autenticación.</p>

        <dl className="info-grid">
          <div>
            <dt>Nombre</dt>
            <dd>{user?.firstName} {user?.lastName}</dd>
          </div>
          <div>
            <dt>Email</dt>
            <dd>{user?.email}</dd>
          </div>
          <div>
            <dt>Rol</dt>
            <dd>{formatRole(user?.role)}</dd>
          </div>
          <div>
            <dt>Estado de cuenta</dt>
            <dd>{formatAccountStatus(accountStatus)}</dd>
          </div>
          <div>
            <dt>Expira sesión</dt>
            <dd>{formatSessionExpiresAt(sessionExpiresAt)}</dd>
          </div>
        </dl>
        {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

        <div className="dashboard-actions">
          <button className="button button--primary" type="button" onClick={handleLogout} disabled={busyAction === 'logout'}>
            {busyAction === 'logout' ? 'Cerrando...' : 'Cerrar sesión'}
          </button>
          <button className="button button--ghost" type="button" onClick={handleDeleteAccount} disabled={busyAction === 'delete'}>
            {busyAction === 'delete' ? 'Eliminando...' : 'Eliminar cuenta'}
          </button>
        </div>
      </section>
    </main>
  );
}

function formatRole(role) {
  if (role === 'CLIENT') return 'Cliente';
  if (role === 'WORKER') return 'Trabajador';
  if (role === 'ADMIN') return 'Administrador';
  return role || '-';
}

function formatAccountStatus(status) {
  if (status === 'PENDING_VALIDATION') return 'Pendiente de validación';
  if (status === 'ACTIVE') return 'Activa';
  if (status === 'SUSPENDED') return 'Suspendida';
  if (status === 'DELETED') return 'Eliminada';
  return '-';
}

function formatSessionExpiresAt(value) {
  if (!value) {
    return '-';
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('es-AR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}
