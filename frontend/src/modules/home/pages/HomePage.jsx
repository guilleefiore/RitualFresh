import {
  kpis,
  notifications,
  serviceRequests,
  workers,
} from '../../../shared/mocks/mockData.js';
import { Badge, Button, KpiCard, MaterialIcon, SectionTitle, WorkerCard } from '../../../shared/components/ui.jsx';

export default function HomePage({ role, onNavigate, onSelectWorker }) {
  return (
    <div className="page-grid">
      <section className="hero-panel">
        <div className="hero-copy">
          <Badge tone="teal">Rol activo: {role}</Badge>
          <h2>Gestión clara de servicios domésticos, perfiles y contrataciones.</h2>
          <p>
            Interfaz SaaS para centralizar búsqueda, solicitudes, comunicación, historial,
            calificaciones, pagos externos y ubicación sin gestionar datos financieros sensibles.
          </p>
          <div className="hero-actions">
            <Button icon="manage_search" onClick={() => onNavigate('search')}>
              Buscar trabajador
            </Button>
            <Button variant="outlined" icon="assignment_turned_in" onClick={() => onNavigate('contracts')}>
              Ver solicitud activa
            </Button>
          </div>
        </div>
        <div className="hero-visual" aria-label="Resumen visual del flujo RitualFresh">
          <div className="flow-card active">
            <MaterialIcon name="person_search" />
            <strong>Búsqueda</strong>
            <span>M03</span>
          </div>
          <div className="flow-line" />
          <div className="flow-card">
            <MaterialIcon name="assignment_add" />
            <strong>Solicitud</strong>
            <span>M04</span>
          </div>
          <div className="flow-line" />
          <div className="flow-card success">
            <MaterialIcon name="paid" />
            <strong>Pago externo</strong>
            <span>M09</span>
          </div>
        </div>
      </section>

      <section className="kpi-grid">
        {kpis.map((item) => (
          <KpiCard item={item} key={item.label} />
        ))}
      </section>

      <section className="content-card wide">
        <SectionTitle
          eyebrow="Contrataciones recientes"
          title="Seguimiento operativo"
          text="Estados visibles para cliente, trabajador y administración."
          action={<Button variant="ghost" icon="download">Exportar</Button>}
        />
        <div className="request-list">
          {serviceRequests.map((request) => (
            <article className="request-row" key={request.code}>
              <div>
                <Badge tone="blue">{request.code}</Badge>
                <h3>{request.service}</h3>
                <p>{request.worker} · {request.date}</p>
              </div>
              <div className="request-row-side">
                <Badge tone={request.status === 'Confirmado' ? 'teal' : 'amber'}>{request.status}</Badge>
                <span>{request.payment}</span>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className="content-card">
        <SectionTitle eyebrow="Prestadores destacados" title="Ranking por reputación" />
        <WorkerCard worker={workers[0]} onSelect={onSelectWorker} />
      </section>

      <section className="content-card">
        <SectionTitle eyebrow="Alertas" title="Actividad reciente" />
        <div className="notification-list compact">
          {notifications.map((notification) => (
            <article className={`notification-item ${notification.unread ? 'unread' : ''}`} key={notification.title}>
              <span className={`notification-icon tone-${notification.tone}`}>
                <MaterialIcon name={notification.icon} />
              </span>
              <div>
                <strong>{notification.title}</strong>
                <p>{notification.text}</p>
                <small>{notification.time}</small>
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}
