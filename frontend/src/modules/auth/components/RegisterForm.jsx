import { useState } from 'react';
import { Button, Field } from '../../../shared/components/ui.jsx';

export default function RegisterForm({ onSubmit }) {
  const [form, setForm] = useState({
    nombre: '',
    apellido: '',
    mail: '',
    contrasena: '',
    confirmacionContrasena: '',
  });

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit?.(form);
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <Field label="Nombre" icon="person">
        <input value={form.nombre} onChange={(event) => setForm((current) => ({ ...current, nombre: event.target.value }))} />
      </Field>
      <Field label="Apellido" icon="badge">
        <input value={form.apellido} onChange={(event) => setForm((current) => ({ ...current, apellido: event.target.value }))} />
      </Field>
      <Field label="Correo electrónico" icon="mail">
        <input type="email" value={form.mail} onChange={(event) => setForm((current) => ({ ...current, mail: event.target.value }))} />
      </Field>
      <Field label="Contraseña" icon="lock">
        <input type="password" value={form.contrasena} onChange={(event) => setForm((current) => ({ ...current, contrasena: event.target.value }))} />
      </Field>
      <Field label="Confirmación" icon="verified_user">
        <input
          type="password"
          value={form.confirmacionContrasena}
          onChange={(event) => setForm((current) => ({ ...current, confirmacionContrasena: event.target.value }))}
        />
      </Field>
      <Button icon="person_add">Crear cuenta</Button>
    </form>
  );
}
