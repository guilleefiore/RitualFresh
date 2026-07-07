import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { FcGoogle } from 'react-icons/fc';
import { registerUser, startGoogleLogin } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';

function ClientIcon() {
  return (
    <svg aria-hidden="true" className="account-type__icon" viewBox="0 0 24 24" focusable="false">
      <path fill="currentColor" d="M12 12.2a4.2 4.2 0 1 0-4.2-4.2 4.2 4.2 0 0 0 4.2 4.2Zm0 2c-4.2 0-7.6 2.1-7.6 4.7V21h15.2v-2.1c0-2.6-3.4-4.7-7.6-4.7Z" />
    </svg>
  );
}

function WorkerIcon() {
  return (
    <svg aria-hidden="true" className="account-type__icon" viewBox="0 0 24 24" focusable="false">
      <path fill="currentColor" d="M9 3h6v2h4a1 1 0 0 1 1 1v3H4V6a1 1 0 0 1 1-1h4V3Zm10 7v8a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1v-8h14ZM10 12v2h4v-2h-4Z" />
    </svg>
  );
}

function splitFullName(fullName) {
  const parts = fullName.trim().split(/\s+/);

  if (parts.length < 2) {
    return null;
  }

  return {
    firstName: parts[0],
    lastName: parts.slice(1).join(' '),
  };
}

export function RegisterRolePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const registerData = location.state;
  const [role, setRole] = useState('CLIENT');
  const [acceptTerms, setAcceptTerms] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!registerData?.fullName || !registerData?.email || !registerData?.password || !registerData?.confirmPassword) {
      navigate('/register', { replace: true });
    }
  }, [registerData, navigate]);

  if (!registerData?.fullName || !registerData?.email || !registerData?.password || !registerData?.confirmPassword) {
    return null;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (!acceptTerms) {
      setErrorMessage('Debe aceptar los términos y condiciones.');
      return;
    }

    const fullName = splitFullName(registerData.fullName);
    if (!fullName) {
      setErrorMessage('Ingresá tu nombre completo en la pantalla anterior.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await registerUser({
        firstName: fullName.firstName.trim(),
        lastName: fullName.lastName.trim(),
        email: registerData.email,
        password: registerData.password,
        confirmPassword: registerData.confirmPassword,
        role,
      });

      setSuccessMessage(response.message);
      navigate('/login', { replace: true, state: { message: response.message } });
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      eyebrow="Registro"
      title="Elegí tu tipo de cuenta"
      description="Seleccioná cómo querés usar RitualFresh y aceptá los términos para finalizar."
      footer={
        <>
          <span>¿Ya tienes cuenta?</span>
          <Link to="/login">Iniciar sesión</Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={handleSubmit}>
        <fieldset className="account-type field--full">
          <legend>Tipo de cuenta</legend>
          <div className="account-type__grid">
            <button
              className={`account-type__card${role === 'CLIENT' ? ' account-type__card--selected' : ''}`}
              type="button"
              onClick={() => setRole('CLIENT')}
            >
              <ClientIcon />
              <span className="account-type__title">Cliente</span>
              <span className="account-type__description">Solicitar servicios para el hogar</span>
            </button>

            <button
              className={`account-type__card${role === 'WORKER' ? ' account-type__card--selected' : ''}`}
              type="button"
              onClick={() => setRole('WORKER')}
            >
              <WorkerIcon />
              <span className="account-type__title">Trabajador</span>
              <span className="account-type__description">Ofrecer servicios profesionales</span>
            </button>
          </div>
        </fieldset>

        <label className="checkbox checkbox--terms field--full">
          <input
            type="checkbox"
            checked={acceptTerms}
            onChange={(event) => setAcceptTerms(event.target.checked)}
          />
          <span>
            Acepto los <span className="checkbox__emphasis">Términos y condiciones</span> y la{' '}
            <span className="checkbox__emphasis">Política de privacidad</span> de RitualFresh.
          </span>
        </label>

        {successMessage ? <p className="feedback feedback--success field--full">{successMessage}</p> : null}
        {errorMessage ? <p className="feedback feedback--error field--full">{errorMessage}</p> : null}

        <button className="button button--primary field--full" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Registrando...' : 'Registrarse'}
        </button>

        <div className="auth-divider field--full" aria-hidden="true">
          <span />
          <span>o registrarse con</span>
          <span />
        </div>

        <div className="social-buttons field--full">
          <button className="button button--social" type="button" onClick={startGoogleLogin}>
            <FcGoogle className="social-button__icon" />
            <span>Google</span>
          </button>
        </div>
      </form>
    </AuthShell>
  );
}
