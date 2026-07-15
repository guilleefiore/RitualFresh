import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { FiGrid, FiLogOut, FiUsers } from 'react-icons/fi';
import { useState } from 'react';
import { useAuth } from '../../auth/hooks/useAuth.js';
import '../styles/adminDashboard.css';

export function AdminLayout() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [error, setError] = useState('');

  async function handleLogout() {
    setIsLoggingOut(true);
    setError('');

    try {
      await logout();
      navigate('/login', { replace: true, state: { message: 'Sesión cerrada correctamente.' } });
    } catch (logoutError) {
      setError(logoutError.message || 'No se pudo cerrar la sesión.');
    } finally {
      setIsLoggingOut(false);
    }
  }

  return (
    <div className="admin-shell">
      <aside className="admin-sidebar">
        <div className="admin-brand">
          <div>
            <strong>RitualFresh</strong>
            <span>Administración</span>
          </div>
        </div>

        <nav className="admin-nav" aria-label="Navegación administrativa">
          <NavLink to="/admin/home" className={({ isActive }) => `admin-nav__link${isActive ? ' admin-nav__link--active' : ''}`}>
            <FiGrid aria-hidden="true" />
            <span>Resumen</span>
          </NavLink>
          <NavLink to="/admin/users" className={({ isActive }) => `admin-nav__link${isActive ? ' admin-nav__link--active' : ''}`}>
            <FiUsers aria-hidden="true" />
            <span>Usuarios</span>
          </NavLink>
        </nav>

        <div className="admin-sidebar__account">
          <span className="admin-account-avatar" aria-hidden="true">{getInitials(user)}</span>
          <div>
            <strong>{user?.firstName || 'Administrador'}</strong>
            <span>{user?.email}</span>
          </div>
        </div>
        {error ? <p className="admin-inline-error">{error}</p> : null}
        <button className="admin-logout" type="button" onClick={handleLogout} disabled={isLoggingOut}>
          <FiLogOut aria-hidden="true" />
          <span>{isLoggingOut ? 'Cerrando...' : 'Cerrar sesión'}</span>
        </button>
      </aside>

      <div className="admin-main">
        <Outlet />
      </div>
    </div>
  );
}

function getInitials(user) {
  const first = user?.firstName?.trim()?.[0] || 'A';
  const last = user?.lastName?.trim()?.[0] || '';
  return `${first}${last}`.toUpperCase();
}
