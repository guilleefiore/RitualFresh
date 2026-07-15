import { Link } from 'react-router-dom';
import { FiArrowRight, FiRefreshCw } from 'react-icons/fi';
import { useState } from 'react';
import { MetricsCard } from '../components/MetricsCard.jsx';
import { UsersList } from '../components/UsersList.jsx';
import { useAdminMetrics, useAdminUsers } from '../hooks/useAdminData.js';

export function AdminDashboard() {
  const [refreshKey, setRefreshKey] = useState(0);
  const { metrics, isLoading: metricsLoading, error: metricsError } = useAdminMetrics(refreshKey);
  const { result, isLoading: usersLoading, error: usersError } = useAdminUsers({ size: 5 }, refreshKey);

  return (
    <main className="admin-page admin-dashboard">
      <header className="admin-page-header">
        <div>
          <p className="admin-eyebrow">Resumen operativo</p>
          <h1>Panel de administración</h1>
          <p>Supervisá el estado de las cuentas y accedé rápidamente a las gestiones pendientes.</p>
        </div>
        <button className="admin-button admin-button--secondary" type="button" onClick={() => setRefreshKey((value) => value + 1)}>
          <FiRefreshCw aria-hidden="true" />
          <span>Actualizar</span>
        </button>
      </header>

      <MetricsCard metrics={metrics} isLoading={metricsLoading} error={metricsError} />

      <section className="admin-panel admin-recent-users">
        <div className="admin-panel__header">
          <div>
            <p className="admin-eyebrow">Actividad reciente</p>
            <h2>Últimos usuarios registrados</h2>
          </div>
          <Link className="admin-text-link" to="/admin/users">
            <span>Ver todos</span>
            <FiArrowRight aria-hidden="true" />
          </Link>
        </div>

        {usersLoading ? <UsersSkeleton rows={3} /> : null}
        {usersError ? <div className="admin-state admin-state--error">{usersError}</div> : null}
        {!usersLoading && !usersError ? <UsersList users={result.content} emptyMessage="Todavía no hay clientes ni trabajadores registrados." /> : null}
      </section>
    </main>
  );
}

function UsersSkeleton({ rows }) {
  return <div className="admin-list-skeleton" aria-label="Cargando usuarios">{Array.from({ length: rows }, (_, index) => <span key={index} />)}</div>;
}
