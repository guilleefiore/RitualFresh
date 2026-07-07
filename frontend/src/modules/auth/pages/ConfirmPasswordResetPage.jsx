import { useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { confirmPasswordReset } from '../services/authService.js';
import { AuthShell } from './components/AuthShell.jsx';
import { FiLock } from 'react-icons/fi';
import { FormField } from '../../../shared/components/FormField.jsx';

export function ConfirmPasswordResetPage() {
  const [searchParams] = useSearchParams();
  const resetToken = useMemo(() => searchParams.get('token') || searchParams.get('resetToken') || '', [searchParams]);
  const [formData, setFormData] = useState({
    password: '',
    confirmPassword: '',
  });
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

    if (!resetToken) {
      setErrorMessage('Falta el token de recuperación en la URL.');
      return;
    }

    if (!formData.password.trim() || !formData.confirmPassword.trim()) {
      setErrorMessage('Debe completar la nueva contraseña y su confirmación.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setErrorMessage('Las contraseñas no coinciden.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await confirmPasswordReset({
        resetToken,
        password: formData.password,
        confirmPassword: formData.confirmPassword,
      });
      setSuccessMessage(response.message);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      title="Confirmar recuperación"
      footer={<Link to="/login">Volver al inicio de sesión</Link>}
    >
      <form className="auth-form" onSubmit={handleSubmit}>
        <FormField
          label="Nueva contraseña"
          icon={<FiLock />}
          name="password"
          type="password"
          value={formData.password}
          onChange={handleChange}
          placeholder="Ingresar nueva contraseña"
        />

        <FormField
          label="Confirmar nueva contraseña"
          icon={<FiLock />}
          name="confirmPassword"
          type="password"
          value={formData.confirmPassword}
          onChange={handleChange}
          placeholder="Repetí nueva contraseña"
        />

        {successMessage ? <p className="feedback feedback--success">{successMessage}</p> : null}
        {errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}

        <button className="button button--primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : 'Actualizar contraseña'}
        </button>
      </form>
    </AuthShell>
  );
}
