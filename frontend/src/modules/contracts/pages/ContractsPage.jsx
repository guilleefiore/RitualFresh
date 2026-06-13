import { Badge, Button, Field, MaterialIcon, SectionTitle } from '../../../shared/components/ui.jsx';

export default function ContratacionView({ selectedWorker, onOpenConfirm }) {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="amber">M04 · US12-M04-RF04</Badge>
          <h2>Contratación del servicio</h2>
          <p>Solicitud, aceptación, seguimiento, finalización y cancelación con reglas visibles.</p>
        </div>
        <Badge tone="blue">Solicitud en borrador</Badge>
      </section>

      <section className="contract-layout">
        <article className="content-card">
          <SectionTitle eyebrow="Stepper de contratación" title="Nueva solicitud" />
          <div className="stepper">
            <div className="step active"><span>1</span><strong>Datos</strong></div>
            <div className="step active"><span>2</span><strong>Prestador</strong></div>
            <div className="step"><span>3</span><strong>Pago externo</strong></div>
            <div className="step"><span>4</span><strong>Confirmación</strong></div>
          </div>

          <div className="form-grid contract-form">
            <Field label="Tipo de servicio" icon="home_services">
              <select defaultValue="limpieza">
                <option value="limpieza">Limpieza profunda del hogar</option>
                <option value="mantenimiento">Mantenimiento del hogar</option>
              </select>
            </Field>
            <Field label="Prestador seleccionado" icon="badge">
              <input defaultValue={selectedWorker.name} />
            </Field>
            <Field label="Fecha y horario" icon="event">
              <input defaultValue="10/06/2026 · 16:00" />
            </Field>
            <Field label="Dirección" icon="location_on">
              <input defaultValue="Godoy Cruz 245, Mendoza" />
            </Field>
            <Field label="Detalle del trabajo" icon="notes">
              <textarea defaultValue="Limpieza de cocina, baño, ventanas y superficies. El cliente indica que cuenta con ascensor y permite ingreso con documento." />
            </Field>
          </div>

          <div className="alert-card">
            <MaterialIcon name="info" />
            <div>
              <strong>Reglas de cancelación visibles</strong>
              <p>Las cancelaciones quedan registradas y pueden contemplar reembolsos o penalizaciones según anticipación.</p>
            </div>
          </div>
        </article>

        <aside className="content-card summary-card">
          <SectionTitle eyebrow="Resumen" title="Solicitud a enviar" />
          <div className="selected-worker-box">
            <div className={`avatar avatar-${selectedWorker.accent}`}>{selectedWorker.initials}</div>
            <div>
              <h3>{selectedWorker.name}</h3>
              <p>{selectedWorker.specialty}</p>
            </div>
          </div>
          <div className="summary-lines">
            <span>Precio orientativo <strong>{selectedWorker.price}</strong></span>
            <span>Disponibilidad <strong>{selectedWorker.availability}</strong></span>
            <span>Estado <strong>Pago pendiente</strong></span>
            <span>Canal de pago <strong>Checkout externo</strong></span>
          </div>
          <Button icon="send" onClick={onOpenConfirm}>
            Enviar solicitud
          </Button>
          <Button variant="danger" icon="cancel">
            Cancelar borrador
          </Button>
        </aside>
      </section>
    </div>
  );
}
