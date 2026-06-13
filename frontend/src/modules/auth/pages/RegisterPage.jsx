import { Badge, SectionTitle } from '../../../shared/components/ui.jsx';
import RegisterForm from '../components/RegisterForm.jsx';

export default function RegisterPage({ onNotify }) {
  return (
    <section className="auth-page">
      <Badge tone="teal">M01 · Registro</Badge>
      <SectionTitle
        eyebrow="Alta de cuenta"
        title="Crear usuario"
        text="Registro de cliente o trabajador con validaciones visibles."
      />
      <RegisterForm onSubmit={() => onNotify?.('Registro enviado para crear la cuenta.')} />
    </section>
  );
}
