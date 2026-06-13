import { useState } from 'react';
import { Button, Field } from '../../../shared/components/ui.jsx';

export default function ForgotPasswordForm({ onSubmit }) {
  const [mail, setMail] = useState('');

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit?.({ mail });
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <Field label="Correo electrónico" icon="mail">
        <input type="email" value={mail} onChange={(event) => setMail(event.target.value)} />
      </Field>
      <Button icon="send">Enviar enlace</Button>
    </form>
  );
}
