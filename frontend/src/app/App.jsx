import { useEffect, useState } from 'react';
import { Badge, Button, ConfirmModal, MaterialIcon, Snackbar } from '../shared/components/ui.jsx';
import { navigationItems, roleOptions, viewComponents } from './router.jsx';
import { workers } from '../shared/mocks/ritualFreshData.js';

export default function App() {
  const [activeView, setActiveView] = useState('dashboard');
  const [role, setRole] = useState('Cliente');
  const [selectedWorker, setSelectedWorker] = useState(workers[0]);
  const [modalOpen, setModalOpen] = useState(false);
  const [snackbar, setSnackbar] = useState('');
  const ActiveView = viewComponents[activeView];

  useEffect(() => {
    if (!snackbar) return undefined;

    const timeout = window.setTimeout(() => setSnackbar(''), 3200);
    return () => window.clearTimeout(timeout);
  }, [snackbar]);

  function handleWorkerSelect(worker) {
    setSelectedWorker(worker);
    setActiveView('contratacion');
    setSnackbar(`Se seleccionó a ${worker.name} para iniciar la solicitud.`);
  }

  function handleConfirmRequest() {
    setModalOpen(false);
    setSnackbar('Solicitud enviada. Se notificó al prestador y quedó trazabilidad del estado.');
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <button className="brand" type="button" onClick={() => setActiveView('dashboard')}>
          <span className="brand-mark">RF</span>
          <span>
            <strong>RitualFresh</strong>
            <small>Servicios domésticos</small>
          </span>
        </button>

        <nav className="side-nav" aria-label="Navegación principal">
          {navigationItems.map((item) => (
            <button
              className={`side-nav-item ${activeView === item.id ? 'active' : ''}`}
              type="button"
              key={item.id}
              onClick={() => setActiveView(item.id)}
            >
              <MaterialIcon name={item.icon} />
              <span>{item.label}</span>
              <small>{item.module}</small>
            </button>
          ))}
        </nav>

        <div className="sidebar-card">
          <div className="sidebar-card-icon">
            <MaterialIcon name="verified_user" />
          </div>
          <strong>Perfil verificado</strong>
          <p>Cuenta validada para contratar servicios y recibir notificaciones.</p>
        </div>
      </aside>

      <div className="app-content">
        <header className="topbar">
          <div className="topbar-title">
            <Badge tone="blue">Producto académico UTN-FRM</Badge>
            <h1>Plataforma web de contratación</h1>
          </div>

          <div className="topbar-actions">
            <label className="global-search">
              <MaterialIcon name="search" />
              <input aria-label="Buscar en RitualFresh" placeholder="Buscar servicio, zona o trabajador" />
            </label>

            <div className="role-switch" aria-label="Selector de rol">
              {roleOptions.map((option) => (
                <button
                  className={role === option ? 'active' : ''}
                  key={option}
                  type="button"
                  onClick={() => setRole(option)}
                >
                  {option}
                </button>
              ))}
            </div>

            <button className="icon-button" type="button" aria-label="Notificaciones" onClick={() => setActiveView('notificaciones')}>
              <MaterialIcon name="notifications" />
              <span className="notification-dot">5</span>
            </button>

            <div className="user-pill">
              <span>GF</span>
              <div>
                <strong>Guillermina</strong>
                <small>{role}</small>
              </div>
            </div>
          </div>
        </header>

        <main className="main-scroll">
          <ActiveView
            role={role}
            selectedWorker={selectedWorker}
            onSelectWorker={handleWorkerSelect}
            onOpenConfirm={() => setModalOpen(true)}
            onNavigate={setActiveView}
            onNotify={setSnackbar}
          />
        </main>
      </div>

      <ConfirmModal
        open={modalOpen}
        worker={selectedWorker}
        onClose={() => setModalOpen(false)}
        onConfirm={handleConfirmRequest}
      />
      <Snackbar message={snackbar} />
    </div>
  );
}
