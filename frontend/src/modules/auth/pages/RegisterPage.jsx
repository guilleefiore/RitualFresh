import { useState } from 'react';
import { Link } from 'react-router-dom';
import { registerUser } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_UPPERCASE_PATTERN = /[A-Z]/;
const PASSWORD_LOWERCASE_PATTERN = /[a-z]/;
const PASSWORD_DIGIT_PATTERN = /\d/;

const INITIAL_FORM = {
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'CLIENT',
};

export function RegisterPage() {
  const [formData, setFormData] = useState(INITIAL_FORM);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
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
    setSuccessMessage('');

    const requiredFields = [
      formData.firstName,
      formData.lastName,
      formData.email,
      formData.password,
      formData.confirmPassword,
      formData.role,
    ];

    if (requiredFields.some((value) => !String(value).trim())) {
      setErrorMessage('Debe completar todos los campos obligatorios.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setErrorMessage('Las contraseñas no coinciden.');
      return;
    }

    if (!EMAIL_PATTERN.test(formData.email.trim())) {
      setErrorMessage('Ingresá un correo electrónico válido.');
      return;
    }

    if (!isStrongPassword(formData.password)) {
      setErrorMessage('La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await registerUser({
        ...formData,
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        email: formData.email.trim(),
      });

      setSuccessMessage(response.message);
      setFormData(INITIAL_FORM);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      eyebrow="Registro"
      title="Crear cuenta pública"
      footer={
        <>
          <span>¿Ya tenés cuenta?</span>
          <Link to="/login">Ir al inicio de sesión</Link>
        </>
      }
    >
      <form className="auth-form auth-form--grid" onSubmit={handleSubmit}>
        <label className="field">
          <span>Nombre</span>
          <input
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            autoComplete="given-name"
            placeholder="Nombre"
          />
        </label>

        <label className="field">
          <span>Apellido</span>
          <input
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            autoComplete="family-name"
            placeholder="Apellido"
          />
        </label>

        <label className="field field--full">
          <span>Email</span>
          <input
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            autoComplete="email"
            placeholder="tu@email.com"
          />
        </label>

        <label className="field">
          <span>Contraseña</span>
          <input
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            autoComplete="new-password"
            placeholder="Contraseña"
          />
        </label>

        <label className="field">
          <span>Confirmar contraseña</span>
          <input
            name="confirmPassword"
            type="password"
            value={formData.confirmPassword}
            onChange={handleChange}
            autoComplete="new-password"
            placeholder="Confirmar"
          />
        </label>

        <p className="inline-help field--full">
          La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.
        </p>

        <label className="field field--full">
          <span>Tipo de cuenta</span>
          <select name="role" value={formData.role} onChange={handleChange}>
            <option value="CLIENT">Cliente</option>
            <option value="WORKER">Trabajador</option>
          </select>
        </label>

        {successMessage ? <p className="feedback feedback--success field--full">{successMessage}</p> : null}
        {errorMessage ? <p className="feedback feedback--error field--full">{errorMessage}</p> : null}

        <button className="button button--primary field--full" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Registrando...' : 'Registrarme'}
        </button>
      </form>
    </AuthShell>
  );
}
