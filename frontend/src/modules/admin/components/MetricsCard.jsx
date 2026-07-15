import { FiAlertCircle, FiClock, FiShield, FiUserCheck, FiUsers } from 'react-icons/fi';
import '../styles/metricsCard.css';

export function MetricsCard({ metrics, isLoading, error }) {
  if (isLoading) {
    return <div className="metrics-grid" aria-label="Cargando métricas">{[1, 2, 3, 4].map((item) => <div key={item} className="metric-card metric-card--skeleton" />)}</div>;
  }

  if (error) {
    return <div className="admin-state admin-state--error"><FiAlertCircle /><span>{error}</span></div>;
  }

  if (!metrics) return null;

  const primaryMetrics = [
    { label: 'Total de usuarios', value: metrics.totalUsers, icon: <FiUsers />, tone: 'primary' },
    { label: 'Cuentas activas', value: metrics.activeUsers, icon: <FiUserCheck />, tone: 'success' },
    { label: 'Pendientes', value: metrics.pendingValidationUsers, icon: <FiClock />, tone: 'warning' },
    { label: 'Suspendidas', value: metrics.suspendedUsers, icon: <FiShield />, tone: 'danger' },
  ];

  return (
    <>
      <div className="metrics-grid">
        {primaryMetrics.map((metric) => (
          <article key={metric.label} className={`metric-card metric-card--${metric.tone}`}>
            <span className="metric-card__icon" aria-hidden="true">{metric.icon}</span>
            <div>
              <p>{metric.label}</p>
              <strong>{metric.value}</strong>
            </div>
          </article>
        ))}
      </div>

      <div className="admin-breakdowns">
        <BreakdownPanel
          title="Usuarios por rol"
          total={metrics.totalUsers}
          items={[
            { label: 'Clientes', value: metrics.clientUsers },
            { label: 'Trabajadores', value: metrics.workerUsers },
            { label: 'Administradores', value: metrics.adminUsers },
          ]}
        />
        <BreakdownPanel
          title="Estado de las cuentas"
          total={metrics.totalUsers}
          items={[
            { label: 'Activas', value: metrics.activeUsers },
            { label: 'Pendientes', value: metrics.pendingValidationUsers },
            { label: 'Suspendidas', value: metrics.suspendedUsers },
            { label: 'Eliminadas', value: metrics.deletedUsers },
          ]}
        />
      </div>
    </>
  );
}

function BreakdownPanel({ title, total, items }) {
  return (
    <section className="breakdown-panel">
      <h2>{title}</h2>
      <div className="breakdown-list">
        {items.map((item) => {
          const percentage = total > 0 ? Math.round((item.value / total) * 100) : 0;
          return (
            <div key={item.label} className="breakdown-item">
              <div className="breakdown-item__row">
                <span>{item.label}</span>
                <strong>{item.value} <small>{percentage}%</small></strong>
              </div>
              <div className="breakdown-item__track" aria-hidden="true"><span style={{ width: `${percentage}%` }} /></div>
            </div>
          );
        })}
      </div>
    </section>
  );
}
