import { useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { validateAccount } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';

export function AccountValidationPage() {
  const [searchParams] = useSearchParams();
  const token = useMemo(() => searchParams.get('token') || '', [searchParams]);
  const [status, setStatus] = useState('idle');
  const [message, setMessage] = useState('');

  useEffect(() => {
    // Evita actualizar estado si el usuario sale de la pantalla antes de terminar.
    let active = true;

    async function runValidation() {
      if (!token) {
        setStatus('error');
        setMessage('Falta el token de validación en la URL.');
        return;
      }

      setStatus('loading');

      try {
        const response = await validateAccount(token);
        if (!active) return;
        setStatus('success');
        setMessage(response.message);
      } catch (error) {
        if (!active) return;
        setStatus('error');
        setMessage(error.message);
      }
    }

    runValidation();

    return () => {
      active = false;
    };
  }, [token]);

  return (
    <AuthShell
      title="Validación de cuenta"
      footer={
        <div className="auth-footer">
          {status === 'success' ? <Link to="/login">Ir al inicio de sesión</Link> : null}
          {(status === 'error' || !token) ? <Link to="/validation/resend">Reenviar validación</Link> : null}
        </div>
      }
    >
      <section className="status-panel" aria-live="polite">
        <p className="status-panel__lead">
          {token
            ? 'Estamos verificando el enlace recibido por correo electrónico.'
            : 'Abrí esta página desde el enlace enviado a tu correo electrónico.'}
        </p>

        {status === 'loading' ? <p>Validando cuenta...</p> : null}
        {message ? (
          <p className={status === 'success' ? 'feedback feedback--success' : 'feedback feedback--error'}>
            {message}
          </p>
        ) : null}

        {status === 'success' ? (
          <p className="inline-help">La cuenta ya quedó activa. Ahora podés iniciar sesión.</p>
        ) : null}

        {status === 'error' && token ? (
          <p className="inline-help">
            El enlace puede haber vencido o ya no ser válido. Podés solicitar uno nuevo.
          </p>
        ) : null}
      </section>
    </AuthShell>
  );
}
