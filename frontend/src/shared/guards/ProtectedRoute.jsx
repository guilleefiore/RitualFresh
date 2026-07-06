import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../modules/auth/hooks/useAuth.js';

export function ProtectedRoute({ allowedRoles = [] }) {
  const location = useLocation();
  const { user, role, isAuthReady } = useAuth();

  if (!isAuthReady) {
    return null;
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(role)) {
    return (
      <main className="screen screen--centered">
        <section className="card">
          <p className="eyebrow">Acceso restringido</p>
          <h1>No autorizado</h1>
          <p className="muted">No posee permisos para acceder a esta funcionalidad.</p>
        </section>
      </main>
    );
  }

  return <Outlet />;
}
