import { Badge, Button, Field, MaterialIcon, SectionTitle } from '../../../shared/components/ui.jsx';

export default function PaymentsPage({ onNotify }) {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="teal">M09/M10 · pagos externos y geolocalización</Badge>
          <h2>Checkout externo y ubicación</h2>
          <p>La aplicación registra trazabilidad sin gestionar directamente datos financieros sensibles.</p>
        </div>
        <Button icon="open_in_new" onClick={() => onNotify('Checkout externo simulado listo para redirección.')}>
          Abrir checkout
        </Button>
      </section>

      <section className="payments-grid">
        <article className="content-card payment-card">
          <SectionTitle eyebrow="M09" title="Resumen de pago" />
          <div className="payment-status">
            <MaterialIcon name="account_balance_wallet" />
            <div>
              <strong>$9.500</strong>
              <span>Pago pendiente por plataforma externa</span>
            </div>
          </div>
          <div className="summary-lines">
            <span>Servicio <strong>Limpieza profunda</strong></span>
            <span>Prestador <strong>Sofía Benítez</strong></span>
            <span>Estado <strong>Checkout generado</strong></span>
            <span>Reembolso <strong>Según reglas de cancelación</strong></span>
          </div>
          <div className="alert-card">
            <MaterialIcon name="lock" />
            <p>No se almacenan datos de tarjeta ni credenciales financieras en RitualFresh.</p>
          </div>
        </article>

        <article className="content-card location-card">
          <SectionTitle eyebrow="M10" title="Ubicación del servicio" />
          <div className="map-preview large-map">
            <div className="map-grid">
              <span className="map-pin primary"><MaterialIcon name="home" /></span>
              <span className="map-pin secondary"><MaterialIcon name="person_pin_circle" /></span>
              <span className="map-pin amber"><MaterialIcon name="pin_drop" /></span>
            </div>
          </div>
          <div className="form-grid">
            <Field label="Dirección manual" icon="edit_location">
              <input defaultValue="Godoy Cruz 245, Mendoza" />
            </Field>
            <Field label="Permiso del navegador" icon="my_location">
              <select defaultValue="manual">
                <option value="manual">Selección manual activa</option>
                <option value="browser">Solicitar permiso del navegador</option>
              </select>
            </Field>
          </div>
        </article>
      </section>
    </div>
  );
}
