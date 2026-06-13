export function MaterialIcon({ name, className = '', filled = false }) {
  return (
    <span className={`material-symbols-outlined ${filled ? 'filled' : ''} ${className}`}>
      {name}
    </span>
  );
}

export function Badge({ children, tone = 'blue' }) {
  return <span className={`badge badge-${tone}`}>{children}</span>;
}

export function Button({ children, icon, variant = 'primary', className = '', ...props }) {
  return (
    <button className={`btn btn-${variant} ${className}`} type="button" {...props}>
      {icon ? <MaterialIcon name={icon} /> : null}
      <span>{children}</span>
    </button>
  );
}

export function KpiCard({ item }) {
  return (
    <article className={`kpi-card tone-${item.tone}`}>
      <div className="kpi-icon">
        <MaterialIcon name={item.icon} />
      </div>
      <div>
        <p>{item.label}</p>
        <strong>{item.value}</strong>
        <span>{item.detail}</span>
      </div>
    </article>
  );
}

export function WorkerCard({ worker, onSelect }) {
  return (
    <article className="worker-card">
      <div className="worker-card-main">
        <div className={`avatar avatar-${worker.accent}`}>{worker.initials}</div>
        <div className="worker-copy">
          <div className="worker-title-row">
            <h3>{worker.name}</h3>
            {worker.verified ? <Badge tone="teal">Perfil verificado</Badge> : null}
          </div>
          <p>{worker.specialty}</p>
          <div className="meta-row">
            <span>
              <MaterialIcon name="location_on" />
              {worker.zone}
            </span>
            <span>
              <MaterialIcon name="schedule" />
              {worker.availability}
            </span>
            <span>
              <MaterialIcon name="forum" />
              Responde en {worker.response}
            </span>
          </div>
          <div className="chip-row">
            {worker.tags.map((tag) => (
              <span className="chip" key={tag}>
                {tag}
              </span>
            ))}
          </div>
        </div>
      </div>
      <div className="worker-side">
        <div className="rating-pill">
          <MaterialIcon name="star" filled />
          <strong>{worker.rating}</strong>
          <span>{worker.completed} servicios</span>
        </div>
        <div className="price-box">
          <span>Desde</span>
          <strong>{worker.price}</strong>
        </div>
        <Button icon="assignment_add" onClick={() => onSelect(worker)}>
          Solicitar
        </Button>
      </div>
    </article>
  );
}

export function Field({ label, icon, children, message }) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <span className="field-control">
        {icon ? <MaterialIcon name={icon} /> : null}
        {children}
      </span>
      {message ? <small>{message}</small> : null}
    </label>
  );
}

export function SectionTitle({ eyebrow, title, text, action }) {
  return (
    <div className="section-title">
      <div>
        {eyebrow ? <span className="eyebrow">{eyebrow}</span> : null}
        <h2>{title}</h2>
        {text ? <p>{text}</p> : null}
      </div>
      {action ? <div className="section-action">{action}</div> : null}
    </div>
  );
}

export function StatusTable({ rows }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Código</th>
            <th>Servicio</th>
            <th>Estado</th>
            <th>Pago</th>
            <th>Calificación</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row[0]}>
              {row.map((cell, index) => (
                <td key={`${row[0]}-${cell}`}>
                  {index === 2 ? <Badge tone={cell === 'Cancelado' ? 'red' : 'teal'}>{cell}</Badge> : cell}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function Snackbar({ message }) {
  if (!message) return null;

  return (
    <div className="snackbar" role="status">
      <MaterialIcon name="task_alt" />
      <span>{message}</span>
    </div>
  );
}

export function ConfirmModal({ open, worker, onClose, onConfirm }) {
  if (!open) return null;

  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal-card" role="dialog" aria-modal="true" aria-labelledby="confirm-title">
        <div className="modal-icon">
          <MaterialIcon name="event_available" />
        </div>
        <h2 id="confirm-title">Confirmar solicitud de contratación</h2>
        <p>
          Se enviará la solicitud a {worker?.name || 'la persona seleccionada'} con trazabilidad de
          estado, notificación interna y pago por checkout externo.
        </p>
        <div className="modal-summary">
          <span>Servicio</span>
          <strong>Limpieza y mantenimiento del hogar</strong>
          <span>Importe orientativo</span>
          <strong>{worker?.price || '$9.500'}</strong>
        </div>
        <div className="modal-actions">
          <Button variant="ghost" onClick={onClose}>
            Revisar
          </Button>
          <Button icon="send" onClick={onConfirm}>
            Enviar solicitud
          </Button>
        </div>
      </section>
    </div>
  );
}
