import { useState } from 'react';
import { Link } from 'react-router-dom';
import { requestPasswordReset } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';

export function PasswordResetPage() {
  const [email, setEmail] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (!email.trim()) {
      setErrorMessage('Debe completar el email.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await requestPasswordReset({ email: email.trim() });
      setSuccessMessage(response.message);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      eyebrow="Recuperación"
      title="Recuperar contraseña"
      footer={<Link to="/login">Volver al inicio de sesión</Link>}
    >
      <form className="auth-form" onSubmit={handleSubmit}>
        <label className="field">
          <span>Email</span>
          <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} />
        </label>

        {successMessage ? <p className="feedback feedback--success">{successMessage}</p> : null}
        {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

        <button className="button button--primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Enviando...' : 'Enviar enlace de recuperación'}
        </button>
      </form>
    </AuthShell>
  );
}
