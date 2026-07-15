import { NavLink, Outlet } from 'react-router-dom';
import { FiBarChart2, FiClock, FiHome, FiMessageCircle, FiUser } from 'react-icons/fi';
import { useAuth } from '../../modules/auth/hooks/useAuth.js';
import { NotificationBell } from '../../modules/notifications/components/NotificationBell.jsx';
import './authenticatedLayout.css';

export function UserLayout() {
  const { role } = useAuth();
  const homePath = role === 'WORKER' ? '/worker/home' : '/client/home';

  return (
    <div className="user-layout">
      <header className="user-layout__header">
        <NavLink className="user-layout__brand" to={homePath} aria-label="RitualFresh, ir al inicio">
          <span className="user-layout__brand-mark" aria-hidden="true">R</span>
          <span>
            <strong>RitualFresh</strong>
            <small>{role === 'WORKER' ? 'Espacio trabajador' : 'Espacio cliente'}</small>
          </span>
        </NavLink>

        <nav className="user-layout__nav" aria-label="Navegación principal">
          <UserNavLink to={homePath} icon={<FiHome aria-hidden="true" />} label="Inicio" />
          <UserNavLink to="/profiles" icon={<FiUser aria-hidden="true" />} label="Perfil" />
          <UserNavLink to="/chat" icon={<FiMessageCircle aria-hidden="true" />} label="Chat" />
          <UserNavLink to="/history" icon={<FiClock aria-hidden="true" />} label="Historial" />
          <UserNavLink to="/statistics" icon={<FiBarChart2 aria-hidden="true" />} label="Estadísticas" />
        </nav>

        <NotificationBell />
      </header>
      <div className="user-layout__content">
        <Outlet />
      </div>
    </div>
  );
}

function UserNavLink({ to, icon, label }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => `user-layout__nav-link${isActive ? ' user-layout__nav-link--active' : ''}`}
    >
      {icon}
      <span>{label}</span>
    </NavLink>
  );
}
