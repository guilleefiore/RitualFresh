import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthPage } from '../modules/auth/pages/AuthPage.jsx';
import { ProfilesPage } from '../modules/profiles/pages/ProfilesPage.jsx';

// Central route map. Keep it thin; feature routes should live with their module.
export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/auth" replace />} />
      <Route path="/auth/*" element={<AuthPage />} />
      <Route path="/profiles/*" element={<ProfilesPage />} />
      <Route
        path="*"
        element={
          <main className="screen screen--centered">
            <section className="card">
              <p className="eyebrow">RitualFresh</p>
              <h1>Ruta no encontrada</h1>
              <p>El frontend modular todavía está en armado base.</p>
            </section>
          </main>
        }
      />
    </Routes>
  );
}
