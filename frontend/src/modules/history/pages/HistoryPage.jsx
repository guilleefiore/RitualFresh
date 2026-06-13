import { historyRows, kpis } from '../../../shared/mocks/ritualFreshData.js';
import { Badge, Button, KpiCard, SectionTitle, StatusTable } from '../../../shared/components/ui.jsx';

const chartItems = [
  ['Limpieza', '78%'],
  ['Mantenimiento', '42%'],
  ['Cancelaciones', '18%'],
  ['Reclamos', '9%'],
];

export default function HistorialView() {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="blue">M06 · M06-WFR-21</Badge>
          <h2>Historial, estadísticas y reportes</h2>
          <p>Seguimiento de servicios realizados, métricas y actividad reciente.</p>
        </div>
        <Button variant="outlined" icon="download">Exportar reporte</Button>
      </section>

      <section className="kpi-grid">
        {kpis.slice(0, 3).map((item) => (
          <KpiCard item={item} key={item.label} />
        ))}
      </section>

      <section className="analytics-grid">
        <article className="content-card">
          <SectionTitle eyebrow="Reporte visual" title="Actividad por módulo" />
          <div className="bar-chart">
            {chartItems.map(([label, value]) => (
              <div className="bar-row" key={label}>
                <span>{label}</span>
                <div><i style={{ width: value }} /></div>
                <strong>{value}</strong>
              </div>
            ))}
          </div>
        </article>

        <article className="content-card">
          <SectionTitle eyebrow="Ranking" title="Mejor reputación" />
          <div className="ranking-list">
            {['Sofía Benítez', 'Equipo Andes Clean', 'Martín Aguilar'].map((name, index) => (
              <div className="ranking-row" key={name}>
                <span>{index + 1}</span>
                <strong>{name}</strong>
                <Badge tone={index === 0 ? 'amber' : 'blue'}>{(4.9 - index * 0.1).toFixed(1)}</Badge>
              </div>
            ))}
          </div>
        </article>
      </section>

      <section className="content-card">
        <SectionTitle eyebrow="Trazabilidad" title="Historial de servicios" />
        <StatusTable rows={historyRows} />
      </section>
    </div>
  );
}
