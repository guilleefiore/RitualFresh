import { notifications } from '../../../shared/mocks/ritualFreshData.js';
import { Badge, Button, MaterialIcon, SectionTitle } from '../../../shared/components/ui.jsx';

export default function NotificacionesView({ onNotify }) {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="red">M08 · US18-M08-RF01</Badge>
          <h2>Panel de notificaciones</h2>
          <p>Alertas automáticas sobre solicitudes, mensajes, pagos y calificaciones.</p>
        </div>
        <Button icon="done_all" onClick={() => onNotify('Todas las notificaciones fueron marcadas como leídas.')}>
          Marcar como leídas
        </Button>
      </section>

      <section className="notifications-layout">
        <article className="content-card">
          <SectionTitle eyebrow="Bandeja" title="Notificaciones recientes" />
          <div className="notification-list">
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
                {notification.unread ? <Badge tone="red">Nueva</Badge> : <Badge tone="blue">Leída</Badge>}
              </article>
            ))}
          </div>
        </article>

        <aside className="content-card settings-card">
          <SectionTitle eyebrow="Preferencias" title="Canales activos" />
          <label><input type="checkbox" defaultChecked /> Alertas dentro de la plataforma</label>
          <label><input type="checkbox" defaultChecked /> Avisos por correo</label>
          <label><input type="checkbox" /> Recordatorios de pago</label>
          <label><input type="checkbox" defaultChecked /> Mensajes nuevos</label>
          <div className="alert-card compact-alert">
            <MaterialIcon name="privacy_tip" />
            <p>Las confirmaciones usan componentes propios de RitualFresh, sin popups nativos del navegador.</p>
          </div>
        </aside>
      </section>
    </div>
  );
}
