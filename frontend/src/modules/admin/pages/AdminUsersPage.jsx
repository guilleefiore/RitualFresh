import { useEffect, useState } from 'react';
import { FiChevronLeft, FiChevronRight, FiSearch, FiSliders, FiX } from 'react-icons/fi';
import { UsersList } from '../components/UsersList.jsx';
import { useAdminUsers } from '../hooks/useAdminData.js';

const PAGE_SIZE = 20;

export function AdminUsersPage() {
  const [searchInput, setSearchInput] = useState('');
  const [query, setQuery] = useState('');
  const [role, setRole] = useState('');
  const [status, setStatus] = useState('');
  const [order, setOrder] = useState('createdAt:desc');
  const [page, setPage] = useState(0);

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setQuery(searchInput.trim());
      setPage(0);
    }, 350);
    return () => window.clearTimeout(timer);
  }, [searchInput]);

  const [sort, direction] = order.split(':');
  const { result, isLoading, error } = useAdminUsers({
    query,
    role,
    status,
    page,
    size: PAGE_SIZE,
    sort,
    direction,
  });

  const hasFilters = Boolean(searchInput || role || status || order !== 'createdAt:desc');

  function clearFilters() {
    setSearchInput('');
    setQuery('');
    setRole('');
    setStatus('');
    setOrder('createdAt:desc');
    setPage(0);
  }

  return (
    <main className="admin-page">
      <header className="admin-page-header">
        <div>
          <p className="admin-eyebrow">Gestión de cuentas</p>
          <h1>Usuarios</h1>
          <p>Buscá clientes y trabajadores, revisá su información y administrá el estado de cada cuenta.</p>
        </div>
      </header>

      <section className="admin-panel admin-users-panel" aria-labelledby="users-list-title">
        <div className="admin-panel__header admin-panel__header--users">
          <div>
            <h2 id="users-list-title">Directorio de usuarios</h2>
            <p>{isLoading ? 'Actualizando resultados...' : `${result.totalElements} ${result.totalElements === 1 ? 'usuario encontrado' : 'usuarios encontrados'}`}</p>
          </div>
          {hasFilters ? (
            <button className="admin-text-button" type="button" onClick={clearFilters}>
              <FiX aria-hidden="true" /> Limpiar filtros
            </button>
          ) : null}
        </div>

        <div className="admin-toolbar">
          <label className="admin-search">
            <span className="sr-only">Buscar por nombre o correo electrónico</span>
            <FiSearch aria-hidden="true" />
            <input
              type="search"
              value={searchInput}
              onChange={(event) => setSearchInput(event.target.value)}
              placeholder="Buscar por nombre o correo"
            />
          </label>

          <div className="admin-filter-group">
            <FiSliders aria-hidden="true" />
            <label>
              <span className="sr-only">Filtrar por rol</span>
              <select value={role} onChange={(event) => { setRole(event.target.value); setPage(0); }}>
                <option value="">Todos los roles</option>
                <option value="CLIENT">Clientes</option>
                <option value="WORKER">Trabajadores</option>
              </select>
            </label>
            <label>
              <span className="sr-only">Filtrar por estado</span>
              <select value={status} onChange={(event) => { setStatus(event.target.value); setPage(0); }}>
                <option value="">Todos los estados</option>
                <option value="ACTIVE">Activos</option>
                <option value="PENDING_VALIDATION">Pendientes</option>
                <option value="SUSPENDED">Suspendidos</option>
                <option value="DELETED">Eliminados</option>
              </select>
            </label>
            <label>
              <span className="sr-only">Ordenar usuarios</span>
              <select value={order} onChange={(event) => { setOrder(event.target.value); setPage(0); }}>
                <option value="createdAt:desc">Más recientes</option>
                <option value="createdAt:asc">Más antiguos</option>
                <option value="email:asc">Correo A-Z</option>
                <option value="lastName:asc">Apellido A-Z</option>
              </select>
            </label>
          </div>
        </div>

        {isLoading ? <UsersLoading /> : null}
        {error ? <div className="admin-state admin-state--error" role="alert">{error}</div> : null}
        {!isLoading && !error ? <UsersList users={result.content} /> : null}

        {!isLoading && !error && result.totalPages > 1 ? (
          <nav className="admin-pagination" aria-label="Paginación de usuarios">
            <button type="button" onClick={() => setPage((value) => value - 1)} disabled={result.page === 0}>
              <FiChevronLeft aria-hidden="true" /> Anterior
            </button>
            <span>Página <strong>{result.page + 1}</strong> de {result.totalPages}</span>
            <button type="button" onClick={() => setPage((value) => value + 1)} disabled={result.page + 1 >= result.totalPages}>
              Siguiente <FiChevronRight aria-hidden="true" />
            </button>
          </nav>
        ) : null}
      </section>
    </main>
  );
}

function UsersLoading() {
  return (
    <div className="admin-list-skeleton" aria-label="Cargando usuarios">
      {Array.from({ length: 5 }, (_, index) => <span key={index} />)}
    </div>
  );
}
