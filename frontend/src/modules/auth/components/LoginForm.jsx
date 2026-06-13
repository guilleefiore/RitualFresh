import { useState } from 'react';
import { Button, Field } from '../../../shared/components/ui.jsx';

export default function LoginForm({ onSubmit }) {
  const [form, setForm] = useState({ email: '', password: '' });

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit?.(form);
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <Field label="Correo electrónico" icon="mail">
        <input
          type="email"
          value={form.email}
          onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
        />
      </Field>
      <Field label="Contraseña" icon="lock">
        <input
          type="password"
          value={form.password}
          onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
        />
      </Field>
      <Button icon="login">Ingresar</Button>
    </form>
  );
}
