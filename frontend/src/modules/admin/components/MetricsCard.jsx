import '../styles/metricsCard.css';

export function MetricsCard({ metrics, isLoading, error }) {
  if (isLoading) {
    return <div className="metrics-container">Cargando métricas...</div>;
  }

  if (error) {
    return <div className="metrics-container error-state">Error: {error}</div>;
  }

  if (!metrics) {
    return <div className="metrics-container">No hay datos disponibles</div>;
  }

  return (
    <div className="metrics-container">
      <h2>Estadísticas de Usuarios</h2>
      
      <div className="metrics-grid">
        <div className="metric-card">
          <h3>Total de usuarios</h3>
          <p className="metric-value">{metrics.totalUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Clientes</h3>
          <p className="metric-value">{metrics.clientUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Trabajadores</h3>
          <p className="metric-value">{metrics.workerUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Administradores</h3>
          <p className="metric-value">{metrics.adminUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Activos</h3>
          <p className="metric-value metric-active">{metrics.activeUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Pendientes de validación</h3>
          <p className="metric-value metric-pending">{metrics.pendingValidationUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Suspendidos</h3>
          <p className="metric-value metric-suspended">{metrics.suspendedUsers}</p>
        </div>

        <div className="metric-card">
          <h3>Eliminados</h3>
          <p className="metric-value metric-deleted">{metrics.deletedUsers}</p>
        </div>
      </div>
    </div>
  );
}
