import { useState } from 'react';
import { Button, Field } from '../../../shared/components/ui.jsx';

export default function ForgotPasswordForm({ onSubmit }) {
  const [email, setEmail] = useState('');

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit?.({ email });
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <Field label="Correo electrónico" icon="mail">
        <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} />
      </Field>
      <Button icon="send">Enviar enlace</Button>
    </form>
  );
}
