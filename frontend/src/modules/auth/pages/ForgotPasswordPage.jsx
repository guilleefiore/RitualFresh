import { Badge, SectionTitle } from '../../../shared/components/ui.jsx';
import ForgotPasswordForm from '../components/ForgotPasswordForm.jsx';

export default function ForgotPasswordPage({ onNotify }) {
  return (
    <section className="auth-page">
      <Badge tone="amber">M01 · Recuperación</Badge>
      <SectionTitle
        eyebrow="Acceso"
        title="Recuperar contraseña"
        text="Flujo de soporte para restablecer la credencial mediante correo."
      />
      <ForgotPasswordForm onSubmit={() => onNotify?.('Solicitud de recuperación enviada.')} />
    </section>
  );
}
