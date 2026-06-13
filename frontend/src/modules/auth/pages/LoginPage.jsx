import { Badge, SectionTitle } from '../../../shared/components/ui.jsx';
import LoginForm from '../components/LoginForm.jsx';

export default function LoginPage({ onNotify }) {
  return (
    <section className="auth-page">
      <Badge tone="blue">M01 · Inicio de sesión</Badge>
      <SectionTitle
        eyebrow="Acceso"
        title="Ingresar al sistema"
        text="Pantalla base para validar credenciales y acceder al flujo principal."
      />
      <LoginForm onSubmit={() => onNotify?.('Login enviado para validación con backend.')} />
    </section>
  );
}
