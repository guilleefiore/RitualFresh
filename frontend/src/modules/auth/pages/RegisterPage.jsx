import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthShell } from './components/AuthShell.jsx';
import { FiLock, FiMail, FiUser } from 'react-icons/fi';
import { FormField } from '../../../shared/components/FormField.jsx';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_UPPERCASE_PATTERN = /[A-Z]/;
const PASSWORD_LOWERCASE_PATTERN = /[a-z]/;
const PASSWORD_DIGIT_PATTERN = /\d/;

const INITIAL_FORM = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
};

export function RegisterPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(INITIAL_FORM);
  const [errorMessage, setErrorMessage] = useState('');
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

    if (!formData.fullName.trim() || !formData.email.trim() || !formData.password.trim() || !formData.confirmPassword.trim()) {
      setErrorMessage('Debe completar todos los campos obligatorios.');
      return;
    }

    if (!EMAIL_PATTERN.test(formData.email.trim())) {
      setErrorMessage('Ingresá un correo electrónico válido.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setErrorMessage('Las contraseñas no coinciden.');
      return;
    }

    if (!isStrongPassword(formData.password)) {
      setErrorMessage('La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.');
      return;
    }

    setIsSubmitting(true);
    navigate('/register/role', {
      state: {
        fullName: formData.fullName.trim(),
        email: formData.email.trim(),
        password: formData.password,
        confirmPassword: formData.confirmPassword,
      },
    });
    setIsSubmitting(false);
  }

  return (
    <AuthShell
      title="Crear cuenta"
      description="Accede a todas las funciones como cliente o trabajador profesional"
      footer={
        <>
          <span>¿Ya tienes cuenta?</span>
          <Link to="/login">Iniciar sesión</Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={handleSubmit}>
        <FormField
          className="field--full"
          label="Nombre completo"
          icon={<FiUser />}
          name="fullName"
          value={formData.fullName}
          onChange={handleChange}
          autoComplete="name"
          placeholder="Ingrese su nombre completo"
        />

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
          label="Confirmar contraseña"
          icon={<FiLock />}
          name="confirmPassword"
          type="password"
          value={formData.confirmPassword}
          onChange={handleChange}
          autoComplete="new-password"
          placeholder="Confirmar contraseña"
        />

        {errorMessage ? <p className="feedback feedback--error field--full">{errorMessage}</p> : null}

        <button className="button button--primary field--full" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Continuando...' : 'Continuar'}
        </button>
      </form>
    </AuthShell>
  );
}
