import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.js';
import { AuthShell } from './components/AuthShell.jsx';

function getHomePath(role) {
  if (role === 'CLIENT') return '/client/home';
  if (role === 'WORKER') return '/worker/home';
  if (role === 'ADMIN') return '/admin/home';
  return '/login';
}

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated, role } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState(location.state?.message || '');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (isAuthenticated && role) {
      navigate(getHomePath(role), { replace: true });
    }
  }, [isAuthenticated, role, navigate]);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (!formData.email.trim() || !formData.password.trim()) {
      setErrorMessage('Debe completar email y contraseña.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await login({
        email: formData.email.trim(),
        password: formData.password,
      });

      setSuccessMessage(response.message);
      navigate(getHomePath(response.user.role), { replace: true });
    } catch (error) {
      if (error.message === 'La cuenta no se encuentra activa.') {
        setErrorMessage('La cuenta está suspendida o eliminada. No puede iniciar sesión.');
      } else if (error.message === 'La sesion expiro. Debe iniciar sesion nuevamente.') {
        setErrorMessage('La sesión venció. Inicie sesión nuevamente.');
      } else {
        setErrorMessage(error.message);
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      title="Ingresar a RitualFresh"
      footer={
        <>
          <span>¿No tenés cuenta?</span>
          <Link to="/register">Crear cuenta</Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={handleSubmit}>
        <label className="field">
          <span>Email</span>
          <input
            name="email"
            type="email"
            autoComplete="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="ana@example.com"
          />
        </label>

        <label className="field">
          <span>Contraseña</span>
          <input
            name="password"
            type="password"
            autoComplete="current-password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Ingrese su contraseña"
          />
        </label>

        {successMessage ? <p className="feedback feedback--success">{successMessage}</p> : null}
        {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

        {errorMessage === 'Debe validar su cuenta antes de iniciar sesion.' ? (
          <p className="inline-help">
            Su cuenta todavía no fue validada. <Link to="/validation/resend">Reenviar enlace</Link>
          </p>
        ) : null}

        <button className="button button--primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Ingresando...' : 'Iniciar sesión'}
        </button>
      </form>

      <nav className="auth-links" aria-label="Accesos de autenticación">
        <Link to="/password-reset">Olvidé mi contraseña</Link>
      </nav>
    </AuthShell>
  );
}
