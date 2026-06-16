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
      eyebrow="Validación"
      title="Validar cuenta"
      footer={
        status === 'success' ? (
          <Link to="/login">Ir al inicio de sesión</Link>
        ) : (
          <Link to="/validation/resend">Reenviar validación</Link>
        )
      }
    >
      <section className="status-panel" aria-live="polite">
        <p className="status-panel__token">Token: {token || 'No informado'}</p>
        {status === 'loading' ? <p>Validando cuenta...</p> : null}
        {message ? (
          <p className={status === 'success' ? 'feedback feedback--success' : 'feedback feedback--error'}>
            {message}
          </p>
        ) : null}
      </section>
    </AuthShell>
  );
}
