import { Navigate, Route, Routes } from 'react-router-dom';
import { LoginPage } from '../modules/auth/pages/LoginPage.jsx';
import { RegisterPage } from '../modules/auth/pages/RegisterPage.jsx';
import { PasswordResetPage } from '../modules/auth/pages/PasswordResetPage.jsx';
import { ConfirmPasswordResetPage } from '../modules/auth/pages/ConfirmPasswordResetPage.jsx';
import { AccountValidationPage } from '../modules/auth/pages/AccountValidationPage.jsx';
import { ResendValidationPage } from '../modules/auth/pages/ResendValidationPage.jsx';
import { ClientHomePage } from '../modules/auth/pages/ClientHomePage.jsx';
import { WorkerHomePage } from '../modules/auth/pages/WorkerHomePage.jsx';
import { AdminHomePage } from '../modules/auth/pages/AdminHomePage.jsx';
import { AdminUserDetailsPage } from '../modules/admin/pages/AdminUserDetailsPage.jsx';
import { ProtectedRoute } from '../shared/guards/ProtectedRoute.jsx';
import { useLocation } from 'react-router-dom';

function LegacyResetPasswordRedirect() {
  const location = useLocation();
  return <Navigate to={`/password-reset/confirm${location.search}`} replace />;
}

// Central route map. Keep it thin; feature routes should live with their module.
export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/password-reset" element={<PasswordResetPage />} />
      <Route path="/password-reset/confirm" element={<ConfirmPasswordResetPage />} />
      <Route path="/reset-password" element={<LegacyResetPasswordRedirect />} />
      <Route path="/validation" element={<AccountValidationPage />} />
      <Route path="/validation/resend" element={<ResendValidationPage />} />

      <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
        <Route path="/client/home" element={<ClientHomePage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['WORKER']} />}>
        <Route path="/worker/home" element={<WorkerHomePage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route path="/admin/home" element={<AdminHomePage />} />
        <Route path="/admin/users/:userId" element={<AdminUserDetailsPage />} />
      </Route>

      <Route
        path="*"
        element={
          <main className="screen screen--centered">
            <section className="card">
              <p className="eyebrow">RitualFresh</p>
              <h1>Ruta no encontrada</h1>
              <p>La ruta solicitada no existe en el módulo de autenticación.</p>
            </section>
          </main>
        }
      />
    </Routes>
  );
}
