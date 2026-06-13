import { workers } from '../../../shared/mocks/mockData.js';
import { Badge, Button, Field, MaterialIcon, SectionTitle, WorkerCard } from '../../../shared/components/ui.jsx';

export default function SearchPage({ onSelectWorker }) {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="blue">M03 · US03-M03-RF01</Badge>
          <h2>Búsqueda y selección de prestadores</h2>
          <p>Buscador con filtros por categoría, ubicación, precio, disponibilidad y reputación.</p>
        </div>
        <Button icon="tune">Guardar filtros</Button>
      </section>

      <section className="search-strip">
        <Field label="Servicio" icon="cleaning_services">
          <input defaultValue="Limpieza profunda" />
        </Field>
        <Field label="Zona" icon="location_on">
          <input defaultValue="Ciudad de Mendoza" />
        </Field>
        <Field label="Fecha" icon="event">
          <input defaultValue="10/06/2026" />
        </Field>
        <Button icon="search" className="search-strip-button">Buscar</Button>
      </section>

      <div className="search-layout">
        <aside className="filters-card">
          <SectionTitle eyebrow="Filtros visibles" title="Refinar resultados" />
          <Field label="Categoría" icon="category">
            <select defaultValue="limpieza">
              <option value="limpieza">Limpieza del hogar</option>
              <option value="mantenimiento">Mantenimiento</option>
              <option value="empresa">Empresa prestadora</option>
            </select>
          </Field>
          <Field label="Precio máximo" icon="payments">
            <input defaultValue="$20.000" />
          </Field>
          <Field label="Disponibilidad" icon="schedule">
            <select defaultValue="semana">
              <option value="hoy">Disponible hoy</option>
              <option value="semana">Esta semana</option>
              <option value="mes">Este mes</option>
            </select>
          </Field>
          <div className="filter-checks">
            <label><input type="checkbox" defaultChecked /> Perfil verificado</label>
            <label><input type="checkbox" defaultChecked /> Calificación mayor a 4.5</label>
            <label><input type="checkbox" /> Disponible para servicio recurrente</label>
          </div>
          <div className="range-card">
            <div>
              <span>Distancia</span>
              <strong>Hasta 8 km</strong>
            </div>
            <div className="range-track"><span /></div>
          </div>
        </aside>

        <section className="results-column">
          <SectionTitle
            eyebrow="Resultados"
            title="Prestadores recomendados"
            text="Ordenados por reputación, disponibilidad y coincidencia con la solicitud."
          />
          {workers.map((worker) => (
            <WorkerCard worker={worker} onSelect={onSelectWorker} key={worker.id} />
          ))}
        </section>

        <aside className="map-preview">
          <div className="map-toolbar">
            <Badge tone="teal">M10</Badge>
            <Button variant="ghost" icon="my_location">Centrar</Button>
          </div>
          <div className="map-grid">
            <span className="map-pin primary"><MaterialIcon name="home" /></span>
            <span className="map-pin secondary"><MaterialIcon name="person_pin_circle" /></span>
            <span className="map-pin amber"><MaterialIcon name="engineering" /></span>
          </div>
          <h3>Zona de cobertura</h3>
          <p>Vista simulada para validar ubicación manual y permisos del navegador.</p>
        </aside>
      </div>
    </div>
  );
}
