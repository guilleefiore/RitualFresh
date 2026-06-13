import { Badge, Button, Field, MaterialIcon, SectionTitle } from '../../../shared/components/ui.jsx';

export default function PerfilView({ role, onNotify }) {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="teal">M02 · M02-WFR-21</Badge>
          <h2>Gestión de perfiles</h2>
          <p>Edición y visualización de datos para mantener perfiles confiables y comparables.</p>
        </div>
        <Button icon="save" onClick={() => onNotify('Perfil guardado en estado de revisión.')}>
          Guardar perfil
        </Button>
      </section>

      <section className="profile-grid">
        <article className="content-card profile-form-card">
          <SectionTitle
            eyebrow="Perfil del trabajador"
            title="Completar información profesional"
            text={`Vista adaptada al rol activo: ${role}.`}
          />
          <div className="progress-box">
            <div>
              <strong>Completitud del perfil</strong>
              <span>82%</span>
            </div>
            <div className="progress-track"><span style={{ width: '82%' }} /></div>
          </div>

          <div className="form-grid">
            <Field label="Nombre visible" icon="person">
              <input defaultValue="Sofía Benítez" />
            </Field>
            <Field label="Zona de trabajo" icon="location_on">
              <input defaultValue="Ciudad de Mendoza y alrededores" />
            </Field>
            <Field label="Especialidad principal" icon="cleaning_services">
              <select defaultValue="limpieza">
                <option value="limpieza">Limpieza integral</option>
                <option value="mantenimiento">Mantenimiento del hogar</option>
              </select>
            </Field>
            <Field label="Precio orientativo" icon="payments">
              <input defaultValue="$9.500 por servicio" />
            </Field>
            <Field label="Experiencia" icon="workspace_premium" message="Este dato ayuda a ordenar resultados de búsqueda.">
              <textarea defaultValue="Más de 5 años en limpieza profunda, mantenimiento periódico y organización de hogares." />
            </Field>
          </div>
        </article>

        <aside className="content-card profile-preview-card">
          <div className="profile-cover">
            <div className="avatar avatar-teal large">SB</div>
            <Badge tone="teal">Perfil verificado</Badge>
          </div>
          <h3>Sofía Benítez</h3>
          <p>Limpieza integral del hogar · Ciudad de Mendoza</p>
          <div className="profile-metrics">
            <span><strong>4.9</strong> reputación</span>
            <span><strong>128</strong> servicios</span>
            <span><strong>12 min</strong> respuesta</span>
          </div>
          <div className="chip-row">
            <span className="chip">Limpieza profunda</span>
            <span className="chip">Baños</span>
            <span className="chip">Cocina</span>
          </div>
          <div className="validation-box">
            <MaterialIcon name="shield" />
            <div>
              <strong>Validación pendiente de documento</strong>
              <p>El administrador puede revisar datos sin exponer información sensible.</p>
            </div>
          </div>
        </aside>
      </section>
    </div>
  );
}
