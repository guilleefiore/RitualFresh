import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { FcGoogle } from 'react-icons/fc';
import { FiLock, FiMail } from 'react-icons/fi';
import { useAuth } from '../hooks/useAuth.js';
import { startGoogleLogin } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';
import { FormField } from '../../../shared/components/FormField.jsx';

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
  const [rememberMe, setRememberMe] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.has('oauth') && params.get('oauth') === 'error') {
      setErrorMessage('No se pudo iniciar sesion con Google. Verifique que las credenciales de OAuth2 estén configuradas correctamente.');
    } else if (location.state?.message) {
      setSuccessMessage(location.state.message);
    }
  }, [location]);

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
      title="Bienvenido nuevamente"
      description="Inicia sesión para acceder a tu cuenta"
      footer={
        <>
          <span>¿No tenés cuenta?</span>
          <Link to="/register">Crear cuenta</Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={handleSubmit}>
        <FormField
          label="Correo electrónico"
          icon={<FiMail />}
          name="email"
          type="email"
          autoComplete="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="ana@example.com"
        />

        <FormField
          label="Contraseña"
          icon={<FiLock />}
          name="password"
          type="password"
          autoComplete="current-password"
          value={formData.password}
          onChange={handleChange}
          placeholder="Ingrese su contraseña"
        />

        {successMessage ? <p className="feedback feedback--success">{successMessage}</p> : null}
        {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

        {errorMessage === 'Debe validar su cuenta antes de iniciar sesion.' ? (
          <p className="inline-help">
            Su cuenta todavía no fue validada. <Link to="/validation/resend">Reenviar enlace</Link>
          </p>
        ) : null}

        <div className="auth-form__options">
          <label className="checkbox">
            <input
              name="rememberMe"
              type="checkbox"
              checked={rememberMe}
              onChange={(event) => setRememberMe(event.target.checked)}
            />
            <span>Recordarme</span>
          </label>

          <Link className="auth-form__forgot" to="/password-reset">
            ¿Olvidaste tu contraseña?
          </Link>
        </div>

        <button className="button button--primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Ingresando...' : 'Iniciar sesión'}
        </button>

        <div className="auth-divider" aria-hidden="true">
          <span />
          <span>o continuar con</span>
          <span />
        </div>

        <div className="social-buttons">
          <button className="button button--social" type="button" onClick={startGoogleLogin}>
            <FcGoogle className="social-button__icon" />
            <span>Google</span>
          </button>
        </div>
      </form>
    </AuthShell>
  );
}
