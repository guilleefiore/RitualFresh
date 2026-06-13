import { useState } from 'react';
import { Button, Field } from '../../../shared/components/ui.jsx';

export default function LoginForm({ onSubmit }) {
  const [form, setForm] = useState({ mail: '', contrasena: '' });

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit?.(form);
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <Field label="Correo electrónico" icon="mail">
        <input
          type="email"
          value={form.mail}
          onChange={(event) => setForm((current) => ({ ...current, mail: event.target.value }))}
        />
      </Field>
      <Field label="Contraseña" icon="lock">
        <input
          type="password"
          value={form.contrasena}
          onChange={(event) => setForm((current) => ({ ...current, contrasena: event.target.value }))}
        />
      </Field>
      <Button icon="login">Ingresar</Button>
    </form>
  );
}
