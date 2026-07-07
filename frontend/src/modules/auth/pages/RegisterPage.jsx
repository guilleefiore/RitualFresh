import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FcGoogle } from 'react-icons/fc';
import { FiLock, FiMail } from 'react-icons/fi';
import { registerUser, startGoogleLogin } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';
import { FormField } from '../../../shared/components/FormField.jsx';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_UPPERCASE_PATTERN = /[A-Z]/;
const PASSWORD_LOWERCASE_PATTERN = /[a-z]/;
const PASSWORD_DIGIT_PATTERN = /\d/;

const INITIAL_FORM = {
  email: '',
  password: '',
  confirmPassword: '',
  role: 'CLIENT',
  acceptTerms: false,
};

export function RegisterPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(INITIAL_FORM);
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  function handleChange(event) {
    const { name, type, value, checked } = event.target;
    setFormData((current) => ({ ...current, [name]: type === 'checkbox' ? checked : value }));
  }

  function isStrongPassword(password) {
    const trimmedPassword = password.trim();
    return (
      trimmedPassword.length >= 8 &&
      PASSWORD_UPPERCASE_PATTERN.test(trimmedPassword) &&
      PASSWORD_LOWERCASE_PATTERN.test(trimmedPassword) &&
      PASSWORD_DIGIT_PATTERN.test(trimmedPassword)
    );
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');

    if (!formData.email.trim() || !formData.password.trim() || !formData.confirmPassword.trim()) {
      setErrorMessage('Debe completar todos los campos obligatorios.');
      return;
    }

    if (!EMAIL_PATTERN.test(formData.email.trim())) {
      setErrorMessage('Ingresá un correo electrónico válido.');
      return;
    }

    if (!formData.acceptTerms) {
      setErrorMessage('Debe aceptar los términos y condiciones.');
      return;
    }

    if (!isStrongPassword(formData.password)) {
      setErrorMessage('La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setErrorMessage('Las contraseñas no coinciden.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await registerUser({
        email: formData.email.trim(),
        password: formData.password,
        confirmPassword: formData.confirmPassword,
        role: formData.role,
      });

      navigate('/login', { replace: true, state: { message: response.message } });
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      title="Crear cuenta"
      description="Sumate a RitualFresh y elegí cómo querés participar."
      footer={
        <>
          <span>¿Ya tienes cuenta?</span>
          <Link to="/login">Iniciar sesión</Link>
        </>
      }
    >
      <form className="auth-form auth-form--register" onSubmit={handleSubmit}>
        <FormField
          className="field--full"
          label="Correo electrónico"
          icon={<FiMail />}
          name="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
          autoComplete="email"
          placeholder="usuario@ejemplo.com"
        />

        <FormField
          className="field--full"
          label="Contraseña"
          icon={<FiLock />}
          name="password"
          type="password"
          value={formData.password}
          onChange={handleChange}
          autoComplete="new-password"
          placeholder="Crea una contraseña segura"
        />

        <FormField
          className="field--full"
          label="Repetir contraseña"
          icon={<FiLock />}
          name="confirmPassword"
          type="password"
          value={formData.confirmPassword}
          onChange={handleChange}
          autoComplete="new-password"
          placeholder="Repetí la contraseña"
        />

        <label className="field field--full">
          <span>Tipo de cuenta</span>
          <select name="role" value={formData.role} onChange={handleChange}>
            <option value="CLIENT">Cliente</option>
            <option value="WORKER">Trabajador</option>
          </select>
        </label>

        <label className="checkbox checkbox--terms field--full">
          <input
            name="acceptTerms"
            type="checkbox"
            checked={formData.acceptTerms}
            onChange={handleChange}
          />
          <span>
            Acepto los <span className="checkbox__emphasis">Términos y condiciones</span> y la{' '}
            <span className="checkbox__emphasis">Política de privacidad</span>.
          </span>
        </label>

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
