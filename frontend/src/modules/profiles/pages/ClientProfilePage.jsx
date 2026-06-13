import { Badge, SectionTitle } from '../../../shared/components/ui.jsx';

export default function ClientProfilePage() {
  return (
    <section className="module-layout">
      <Badge tone="teal">M02 · Perfil del cliente</Badge>
      <SectionTitle
        eyebrow="Perfil"
        title="Completar datos del cliente"
        text="Pantalla base para la información personal y la dirección del usuario."
      />
    </section>
  );
}
