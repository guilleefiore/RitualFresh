import { useState } from 'react';
import { Link } from 'react-router-dom';
import { UsersList } from '../components/UsersList.jsx';
import { MetricsCard } from '../components/MetricsCard.jsx';
import { useAdminUsers, useAdminMetrics } from '../hooks/useAdminData.js';

export function AdminDashboard() {
  const { users, isLoading: usersLoading, error: usersError } = useAdminUsers();
  const { metrics, isLoading: metricsLoading, error: metricsError } = useAdminMetrics();
  const [refetchTrigger, setRefetchTrigger] = useState(0);

  const handleUserUpdated = () => {
    setRefetchTrigger((prev) => prev + 1);
  };

  return (
    <main className="admin-dashboard">
      <section className="admin-header">
        <h1>Panel de Administración</h1>
        <p>Gestión de usuarios y métricas de la plataforma</p>
      </section>

      <MetricsCard
        metrics={metrics}
        isLoading={metricsLoading}
        error={metricsError}
      />

      <section className="admin-users-section">
        <div className="section-header">
          <h2>Gestión de Usuarios</h2>
          <Link to="/admin/users" className="btn btn-primary">
            Ver todos los usuarios
          </Link>
        </div>

        {usersLoading && <p>Cargando usuarios...</p>}
        {usersError && <p className="error-message">Error: {usersError}</p>}
        {!usersLoading && !usersError && (
          <UsersList users={users} onUserUpdated={handleUserUpdated} />
        )}
      </section>
    </main>
  );
}
