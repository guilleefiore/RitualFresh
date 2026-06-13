import { useState } from 'react';
import { Button, Field } from '../../../shared/components/ui.jsx';

export default function RegisterForm({ onSubmit }) {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit?.(form);
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <Field label="Nombre" icon="person">
        <input value={form.firstName} onChange={(event) => setForm((current) => ({ ...current, firstName: event.target.value }))} />
      </Field>
      <Field label="Apellido" icon="badge">
        <input value={form.lastName} onChange={(event) => setForm((current) => ({ ...current, lastName: event.target.value }))} />
      </Field>
      <Field label="Correo electrónico" icon="mail">
        <input type="email" value={form.email} onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))} />
      </Field>
      <Field label="Contraseña" icon="lock">
        <input type="password" value={form.password} onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))} />
      </Field>
      <Field label="Confirmación" icon="verified_user">
        <input
          type="password"
          value={form.confirmPassword}
          onChange={(event) => setForm((current) => ({ ...current, confirmPassword: event.target.value }))}
        />
      </Field>
      <Button icon="person_add">Crear cuenta</Button>
    </form>
  );
}
