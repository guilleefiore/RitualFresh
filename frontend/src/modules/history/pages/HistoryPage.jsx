import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/hooks/useAuth.js';
import { HistoryStatusBadge } from '../components/HistoryStatusBadge.jsx';
import { getServiceHistory } from '../services/historyService.js';
import { formatAmount } from '../utils/historyFormatters.js';
import '../../auth/styles/auth.css';
import '../styles/history.css';

const EMPTY_FILTERS = { status: '', from: '', to: '' };

export function HistoryPage() {
  const { role } = useAuth();
  const [draftFilters, setDraftFilters] = useState(EMPTY_FILTERS);
  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [requestVersion, setRequestVersion] = useState(0);
  const [page, setPage] = useState(0);
  const [records, setRecords] = useState([]);
  const [pagination, setPagination] = useState({ totalElements: 0, hasNext: false });
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadHistory() {
      setLoading(true);
      setErrorMessage('');
      try {
        const response = await getServiceHistory({ ...filters, page, size: 20 });
        if (!ignore) {
          setRecords((current) => page === 0 ? response.content : [...current, ...response.content]);
          setPagination({ totalElements: response.totalElements, hasNext: response.hasNext });
        }
      } catch (error) {
        if (!ignore) setErrorMessage(error.message);
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    loadHistory();
    return () => {
      ignore = true;
    };
  }, [filters, page, requestVersion]);

  function updateDraftFilter(event) {
    const { name, value } = event.target;
    setDraftFilters((current) => ({ ...current, [name]: value }));
  }

  function applyFilters(event) {
    event.preventDefault();
    if (draftFilters.from && draftFilters.to && draftFilters.from > draftFilters.to) {
      setErrorMessage('La fecha desde no puede ser posterior a la fecha hasta.');
      return;
    }
    setRecords([]);
    setSelectedRecord(null);
    setPage(0);
    setFilters({ ...draftFilters });
    setRequestVersion((value) => value + 1);
  }

  function clearFilters() {
    setDraftFilters(EMPTY_FILTERS);
    setFilters(EMPTY_FILTERS);
    setRecords([]);
    setSelectedRecord(null);
    setPage(0);
    setRequestVersion((value) => value + 1);
  }

  const hasActiveFilters = Boolean(filters.status || filters.from || filters.to);
  const homePath = role === 'WORKER' ? '/worker/home' : '/client/home';

  return (
    <main className="history-screen">
      <header className="history-topbar">
        <div>
          <p className="eyebrow">M06 · Actividad</p>
          <h1>Historial de servicios</h1>
          <p>Revisá cada servicio pactado y abrí su ficha para consultar todos los datos.</p>
        </div>
        <nav className="history-topbar__actions" aria-label="Navegación del historial">
          <Link className="button button--ghost" to={homePath}>Volver al inicio</Link>
          <Link className="button button--primary" to="/statistics">Ver estadísticas</Link>
        </nav>
      </header>

      <form className="history-filters" onSubmit={applyFilters}>
        <label>
          <span>Estado</span>
          <select name="status" value={draftFilters.status} onChange={updateDraftFilter}>
            <option value="">Todos los estados</option>
            <option value="PENDING">Pendiente</option>
            <option value="COMPLETED">Completado</option>
            <option value="CANCELLED">Cancelado</option>
          </select>
        </label>
        <label>
          <span>Desde</span>
          <input name="from" type="date" value={draftFilters.from} onChange={updateDraftFilter} />
        </label>
        <label>
          <span>Hasta</span>
          <input name="to" type="date" value={draftFilters.to} onChange={updateDraftFilter} />
        </label>
        <div className="history-filters__actions">
          <button className="button button--primary" type="submit">Aplicar filtros</button>
          <button className="button button--ghost" type="button" onClick={clearFilters}>Limpiar</button>
        </div>
      </form>

      {errorMessage ? <p className="feedback feedback--error history-feedback">{errorMessage}</p> : null}

      <div className="history-layout">
        <section className="history-results" aria-live="polite" aria-busy={loading}>
          <div className="history-results__heading">
            <div>
              <p className="eyebrow">Registro cronológico</p>
              <h2>{pagination.totalElements} {pagination.totalElements === 1 ? 'servicio' : 'servicios'}</h2>
            </div>
            {hasActiveFilters ? <span className="history-filter-indicator">Filtros activos</span> : null}
          </div>

          {loading && page === 0 ? <HistoryLoading /> : null}
          {!loading && records.length === 0 ? (
            <HistoryEmpty filtered={hasActiveFilters} onClear={clearFilters} />
          ) : null}

          {records.length > 0 ? (
            <ol className="history-timeline">
              {records.map((record) => (
                <li key={record.id} className={`history-timeline__item history-timeline__item--${record.status.toLowerCase()}`}>
                  <span className="history-timeline__dot" aria-hidden="true" />
                  <button
                    className={`history-record ${selectedRecord?.id === record.id ? 'history-record--selected' : ''}`}
                    type="button"
                    onClick={() => setSelectedRecord(record)}
                    aria-label={`Abrir detalle de ${record.serviceName} con ${record.counterpartName}`}
                  >
                    <span className="history-record__date">{formatDateTime(record.scheduledAt)}</span>
                    <span className="history-record__main">
                      <strong>{record.serviceName}</strong>
                      <span>{record.counterpartName} · {record.category}</span>
                    </span>
                    <HistoryStatusBadge status={record.status} />
                    <span className="history-record__amount">{formatAmount(record.amountArs)}</span>
                  </button>
                </li>
              ))}
            </ol>
          ) : null}

          {pagination.hasNext ? (
            <button
              className="button button--ghost history-load-more"
              type="button"
              onClick={() => setPage((current) => current + 1)}
              disabled={loading}
            >
              {loading ? 'Cargando...' : 'Cargar más servicios'}
            </button>
          ) : null}
        </section>

        <HistoryDetail record={selectedRecord} onClose={() => setSelectedRecord(null)} />
      </div>
    </main>
  );
}

function HistoryDetail({ record, onClose }) {
  if (!record) {
    return (
      <aside className="history-detail history-detail--empty" aria-label="Detalle del servicio">
        <span className="history-detail__line" aria-hidden="true" />
        <p className="eyebrow">Ficha del servicio</p>
        <h2>Seleccioná un registro</h2>
        <p>La ficha reúne fecha, contraparte, categoría, estado e importe sin salir del historial.</p>
      </aside>
    );
  }

  return (
    <aside className="history-detail history-detail--open" aria-label={`Detalle de ${record.serviceName}`}>
      <button className="history-detail__close" type="button" onClick={onClose} aria-label="Cerrar detalle">×</button>
      <p className="eyebrow">Ficha del servicio</p>
      <h2>{record.serviceName}</h2>
      <HistoryStatusBadge status={record.status} />
      <dl>
        <div><dt>Fecha pactada</dt><dd>{formatDateTime(record.scheduledAt)}</dd></div>
        <div><dt>Contraparte</dt><dd>{record.counterpartName}</dd></div>
        <div><dt>Categoría</dt><dd>{record.category}</dd></div>
        <div><dt>Estado</dt><dd><HistoryStatusBadge status={record.status} /></dd></div>
        <div><dt>Importe</dt><dd>{formatAmount(record.amountArs)}</dd></div>
      </dl>
    </aside>
  );
}

function HistoryEmpty({ filtered, onClear }) {
  return (
    <div className="history-empty">
      <span className="history-empty__mark" aria-hidden="true" />
      <h3>{filtered ? 'No hay resultados para estos filtros' : 'Todavía no hay servicios en tu historial'}</h3>
      <p>{filtered
        ? 'Probá ampliar el rango de fechas o consultar todos los estados.'
        : 'Cuando se registre una contratación, su recorrido aparecerá en esta línea temporal.'}</p>
      {filtered ? <button className="button button--ghost" type="button" onClick={onClear}>Quitar filtros</button> : null}
    </div>
  );
}

function HistoryLoading() {
  return <div className="history-loading" role="status">Cargando historial...</div>;
}

function formatDateTime(value) {
  return new Intl.DateTimeFormat('es-AR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}
