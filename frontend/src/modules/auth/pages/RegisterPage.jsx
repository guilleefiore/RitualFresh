import { useState } from 'react';
import { Link } from 'react-router-dom';
import { registerUser } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';

const INITIAL_FORM = {
  firstName: '',
  lastName: '',
  documentNumber: '',
  phoneNumber: '',
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

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    const requiredFields = [
      formData.firstName,
      formData.lastName,
      formData.documentNumber,
      formData.phoneNumber,
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

    setIsSubmitting(true);

    try {
      const response = await registerUser({
        ...formData,
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        documentNumber: formData.documentNumber.trim(),
        phoneNumber: formData.phoneNumber.trim(),
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
          <input name="firstName" value={formData.firstName} onChange={handleChange} />
        </label>

        <label className="field">
          <span>Apellido</span>
          <input name="lastName" value={formData.lastName} onChange={handleChange} />
        </label>

        <label className="field">
          <span>DNI</span>
          <input name="documentNumber" value={formData.documentNumber} onChange={handleChange} />
        </label>

        <label className="field">
          <span>Teléfono</span>
          <input name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} />
        </label>

        <label className="field field--full">
          <span>Email</span>
          <input name="email" type="email" value={formData.email} onChange={handleChange} />
        </label>

        <label className="field">
          <span>Contraseña</span>
          <input name="password" type="password" value={formData.password} onChange={handleChange} />
        </label>

        <label className="field">
          <span>Confirmar contraseña</span>
          <input
            name="confirmPassword"
            type="password"
            value={formData.confirmPassword}
            onChange={handleChange}
          />
        </label>

        <label className="field field--full">
          <span>Rol</span>
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
