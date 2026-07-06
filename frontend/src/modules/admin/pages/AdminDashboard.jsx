import { useState } from 'react';
import { UsersList } from '../components/UsersList.jsx';
import { MetricsCard } from '../components/MetricsCard.jsx';
import { useAdminUsers, useAdminMetrics } from '../hooks/useAdminData.js';

export function AdminDashboard() {
  const [refetchTrigger, setRefetchTrigger] = useState(0);
  const { users, isLoading: usersLoading, error: usersError } = useAdminUsers(refetchTrigger);
  const { metrics, isLoading: metricsLoading, error: metricsError } = useAdminMetrics(refetchTrigger);

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
