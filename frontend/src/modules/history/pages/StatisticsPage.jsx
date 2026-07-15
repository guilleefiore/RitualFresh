import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/hooks/useAuth.js';
import { CategoryBars } from '../components/CategoryBars.jsx';
import { FrequentWorkersTable } from '../components/FrequentWorkersTable.jsx';
import { TimeSeriesChart } from '../components/TimeSeriesChart.jsx';
import { getMyStatistics } from '../services/historyService.js';
import { formatAmount } from '../utils/historyFormatters.js';
import '../../auth/styles/auth.css';
import '../styles/history.css';

const PERIODS = [
  { value: 'LAST_7_DAYS', label: 'Últimos 7 días' },
  { value: 'LAST_30_DAYS', label: 'Últimos 30 días' },
  { value: 'LAST_365_DAYS', label: 'Últimos 365 días' },
];

export function StatisticsPage() {
  const { role } = useAuth();
  const [period, setPeriod] = useState('LAST_30_DAYS');
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadStatistics() {
      setLoading(true);
      setErrorMessage('');
      try {
        const response = await getMyStatistics(role, period);
        if (!ignore) setStatistics(response);
      } catch (error) {
        if (!ignore) setErrorMessage(error.message);
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    if (role === 'CLIENT' || role === 'WORKER') loadStatistics();
    return () => {
      ignore = true;
    };
  }, [role, period]);

  const homePath = role === 'WORKER' ? '/worker/home' : '/client/home';
  const roleLabel = role === 'WORKER' ? 'Trabajador' : 'Cliente';

  return (
    <main className="history-screen statistics-screen">
      <header className="history-topbar">
        <div>
          <p className="eyebrow">M06 · {roleLabel}</p>
          <h1>Estadísticas de actividad</h1>
          <p>Una lectura clara de tus servicios dentro del período seleccionado.</p>
        </div>
        <nav className="history-topbar__actions" aria-label="Navegación de estadísticas">
          <Link className="button button--ghost" to={homePath}>Volver al inicio</Link>
          <Link className="button button--primary" to="/history">Ver historial</Link>
        </nav>
      </header>

      <section className="period-selector" aria-labelledby="period-title">
        <div>
          <h2 id="period-title">Período</h2>
          <p>Las ventanas son móviles e incluyen el día de hoy.</p>
        </div>
        <div className="period-selector__options" role="group" aria-label="Seleccionar período">
          {PERIODS.map((option) => (
            <button
              className={period === option.value ? 'period-button period-button--active' : 'period-button'}
              type="button"
              key={option.value}
              onClick={() => setPeriod(option.value)}
              aria-pressed={period === option.value}
            >
              {option.label}
            </button>
          ))}
        </div>
      </section>

      {errorMessage ? <p className="feedback feedback--error history-feedback">{errorMessage}</p> : null}
      {loading ? <div className="statistics-loading" role="status">Calculando estadísticas...</div> : null}
      {!loading && statistics && role === 'WORKER' ? <WorkerDashboard statistics={statistics} /> : null}
      {!loading && statistics && role === 'CLIENT' ? <ClientDashboard statistics={statistics} /> : null}
    </main>
  );
}

function WorkerDashboard({ statistics }) {
  if (statistics.completedJobs === 0) {
    return <StatisticsEmpty message="No tenés trabajos completados en este período." />;
  }

  return (
    <div className="statistics-dashboard">
      <section className="metric-strip" aria-label="Resumen del trabajador">
        <MetricCard label="Trabajos completados" value={statistics.completedJobs} helper="Servicios finalizados" />
        <MetricCard
          label="Calificación promedio"
          value={statistics.averageRating === null ? 'Sin calificaciones' : `${Number(statistics.averageRating).toFixed(2)} / 5`}
          helper="Sólo trabajos calificados"
        />
      </section>
      <TimeSeriesChart
        title="Trabajos completados en el tiempo"
        description="Cantidad de servicios completados en cada segmento del período."
        buckets={statistics.completedJobsTimeline}
        valueKey="count"
        formatValue={(value) => `${value} ${value === 1 ? 'trabajo' : 'trabajos'}`}
      />
    </div>
  );
}

function ClientDashboard({ statistics }) {
  if (statistics.hiredServices === 0) {
    return <StatisticsEmpty message="No tenés servicios pendientes o completados en este período." />;
  }

  return (
    <div className="statistics-dashboard">
      <section className="metric-strip metric-strip--client" aria-label="Resumen del cliente">
        <MetricCard
          label="Servicios contratados"
          value={statistics.hiredServices}
          helper={`${statistics.pendingServices} pendientes · ${statistics.completedServices} completados`}
        />
        <MetricCard label="Gasto total" value={formatAmount(statistics.totalSpentArs)} helper="Sólo servicios completados" />
        <MetricCard label="Categorías utilizadas" value={statistics.categories.length} helper="Con actividad completada" />
      </section>

      {statistics.completedServices > 0 ? (
        <>
          <TimeSeriesChart
            title="Gasto a lo largo del tiempo"
            description="Importes disponibles de servicios completados, agrupados dentro del período."
            buckets={statistics.spendingTimeline}
            valueKey="amountArs"
            formatValue={(value) => formatAmount(value)}
            tone="secondary"
          />
          <div className="statistics-grid">
            <CategoryBars categories={statistics.categories} />
            <FrequentWorkersTable workers={statistics.frequentWorkers} />
          </div>
        </>
      ) : (
        <StatisticsEmpty message="Tus servicios están pendientes; las métricas de gasto aparecerán cuando se completen." compact />
      )}
    </div>
  );
}

function MetricCard({ label, value, helper }) {
  return (
    <article className="metric-card">
      <p>{label}</p>
      <strong>{value}</strong>
      <span>{helper}</span>
    </article>
  );
}

function StatisticsEmpty({ message, compact = false }) {
  return (
    <section className={`statistics-empty ${compact ? 'statistics-empty--compact' : ''}`}>
      <span className="statistics-empty__pulse" aria-hidden="true" />
      <h2>Sin actividad efectiva</h2>
      <p>{message}</p>
      <Link className="button button--ghost" to="/history">Consultar historial</Link>
    </section>
  );
}
