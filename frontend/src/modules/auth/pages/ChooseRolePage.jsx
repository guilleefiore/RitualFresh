import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.js';
import { updateUserRole } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';

export function ChooseRolePage() {
  const navigate = useNavigate();
  const { user, refreshSession } = useAuth();
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleChooseRole(newRole) {
    setErrorMessage('');
    setIsSubmitting(true);

    try {
      await updateUserRole(newRole);
      await refreshSession();
      navigate(newRole === 'WORKER' ? '/worker/home' : '/client/home', { replace: true });
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <AuthShell eyebrow="Bienvenido" title="¿Qué tipo de cuenta querés?" footer={null}>
      <div className="auth-form">
        <p className="muted" style={{ marginBottom: '1.5rem' }}>
          Elegí el tipo de cuenta con la que querés usar RitualFresh. Podrás completar tu perfil después.
        </p>

        {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

        <button
          className="button button--primary"
          type="button"
          disabled={isSubmitting}
          onClick={() => handleChooseRole('CLIENT')}
          style={{ marginBottom: '0.75rem' }}
        >
          {isSubmitting ? 'Guardando...' : 'Soy Cliente'}
        </button>

        <button
          className="button button--secondary"
          type="button"
          disabled={isSubmitting}
          onClick={() => handleChooseRole('WORKER')}
        >
          {isSubmitting ? 'Guardando...' : 'Soy Trabajador'}
        </button>
      </div>
    </AuthShell>
  );
}
