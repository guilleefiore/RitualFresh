import { conversations, messages } from '../../../shared/mocks/ritualFreshData.js';
import { Badge, Button, MaterialIcon, SectionTitle } from '../../../shared/components/ui.jsx';

export default function ChatView({ onNotify }) {
  return (
    <div className="module-layout">
      <section className="module-header">
        <div>
          <Badge tone="blue">M05 · US15-M05-RF01</Badge>
          <h2>Chat y comunicación</h2>
          <p>Mensajería interna con historial para coordinar detalles de la contratación.</p>
        </div>
        <Badge tone="teal">Conversación activa</Badge>
      </section>

      <section className="chat-shell">
        <aside className="chat-list">
          <SectionTitle eyebrow="Mensajes" title="Conversaciones" />
          {conversations.map((conversation) => (
            <button className={`conversation ${conversation.active ? 'active' : ''}`} key={conversation.name} type="button">
              <span className="avatar avatar-blue">{conversation.name.slice(0, 2).toUpperCase()}</span>
              <span>
                <strong>{conversation.name}</strong>
                <small>{conversation.preview}</small>
              </span>
              <em>{conversation.time}</em>
            </button>
          ))}
        </aside>

        <article className="chat-panel">
          <header className="chat-header">
            <div>
              <h3>Sofía Benítez</h3>
              <p>Servicio RF-2026-014 · Limpieza profunda</p>
            </div>
            <Badge tone="teal">En línea</Badge>
          </header>

          <div className="message-stack">
            {messages.map((message) => (
              <div className={`message-bubble ${message.from}`} key={`${message.time}-${message.text}`}>
                <p>{message.text}</p>
                <span>{message.time}</span>
              </div>
            ))}
          </div>

          <div className="quick-replies">
            <button type="button">Confirmar horario</button>
            <button type="button">Enviar dirección</button>
            <button type="button">Solicitar reprogramación</button>
          </div>

          <footer className="composer">
            <MaterialIcon name="attach_file" />
            <input placeholder="Escribir mensaje dentro de RitualFresh" />
            <Button icon="send" onClick={() => onNotify('Mensaje enviado en la conversación activa.')}>
              Enviar
            </Button>
          </footer>
        </article>
      </section>
    </div>
  );
}
