import { Navigate, Route, Routes } from 'react-router-dom';
import { LoginPage } from '../modules/auth/pages/LoginPage.jsx';
import { RegisterPage } from '../modules/auth/pages/RegisterPage.jsx';
import { ChooseRolePage } from '../modules/auth/pages/ChooseRolePage.jsx';
import { PasswordResetPage } from '../modules/auth/pages/PasswordResetPage.jsx';
import { ConfirmPasswordResetPage } from '../modules/auth/pages/ConfirmPasswordResetPage.jsx';
import { AccountValidationPage } from '../modules/auth/pages/AccountValidationPage.jsx';
import { ResendValidationPage } from '../modules/auth/pages/ResendValidationPage.jsx';
import { ClientHomePage } from '../modules/auth/pages/ClientHomePage.jsx';
import { WorkerHomePage } from '../modules/auth/pages/WorkerHomePage.jsx';
import { AdminLayout } from '../modules/admin/components/AdminLayout.jsx';
import { AdminDashboard } from '../modules/admin/pages/AdminDashboard.jsx';
import { AdminUserDetailsPage } from '../modules/admin/pages/AdminUserDetailsPage.jsx';
import { AdminUsersPage } from '../modules/admin/pages/AdminUsersPage.jsx';
import { ChatPage } from '../modules/chat/pages/ChatPage.jsx';
import { ProfilesPage } from '../modules/profiles/pages/ProfilesPage.jsx';
import { HistoryPage } from '../modules/history/pages/HistoryPage.jsx';
import { StatisticsPage } from '../modules/history/pages/StatisticsPage.jsx';
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

      <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
        <Route path="/choose-role" element={<ChooseRolePage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['CLIENT', 'WORKER']} />}>
        <Route path="/profiles" element={<ProfilesPage />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/history" element={<HistoryPage />} />
        <Route path="/statistics" element={<StatisticsPage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Navigate to="home" replace />} />
          <Route path="home" element={<AdminDashboard />} />
          <Route path="users" element={<AdminUsersPage />} />
          <Route path="users/:userId" element={<AdminUserDetailsPage />} />
        </Route>
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
