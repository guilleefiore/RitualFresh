import '../../styles/auth.css';

export function AuthShell({ eyebrow, title, description, children, footer }) {
  return (
    <main className="screen auth-screen">
      <section className="auth-hero">
        {eyebrow ? <p className="eyebrow">{eyebrow}</p> : null}
        <h1>{title}</h1>
        {description ? <p className="muted">{description}</p> : null}
      </section>

      <section className="card auth-card">
        {children}
        {footer ? <footer className="auth-footer">{footer}</footer> : null}
      </section>
    </main>
  );
}
