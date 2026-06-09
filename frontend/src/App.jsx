import { useEffect, useMemo, useState } from 'react';
import { apiRequest } from './services/api.js';

const STORAGE_KEY = 'ritualfresh.session';

const initialRegisterForm = {
  nombre: '',
  apellido: '',
  dni: '',
  telefono: '',
  mail: '',
  contrasena: '',
  confirmacionContrasena: '',
  rol: 'CLIENTE'
};

const initialLoginForm = {
  mail: '',
  contrasena: ''
};

const initialRecoveryForm = {
  mail: '',
  tokenRecuperacion: '',
  contrasena: '',
  confirmacionContrasena: ''
};

const initialClienteProfile = {
  urlFotoPerfil: '',
  telefonoContacto: '',
  nombreCalle: '',
  numeroDomicilio: '',
  piso: '',
  departamentoDomicilio: '',
  codigoPostal: '',
  localidad: '',
  provincia: '',
  preferenciasContratacion: ''
};

const initialTrabajadorProfile = {
  urlFotoPerfil: '',
  descripcion: '',
  aniosExperiencia: '',
  serviciosOfrecidos: '',
  zonaTrabajo: '',
  disponibilidad: '',
  precioHoraOrientativo: ''
};

const views = [
  { id: 'registro', label: 'Registro', module: 'M01-WFR-01' },
  { id: 'validacion', label: 'Validacion', module: 'M01-WFR-04' },
  { id: 'login', label: 'Login', module: 'M01-WFR-02' },
  { id: 'recuperacion', label: 'Recuperacion', module: 'M01-WFR-03' },
  { id: 'perfil', label: 'Mi perfil', module: 'M02-WFR' }
];

function App() {
  const [activeView, setActiveView] = useState('registro');
  const [session, setSession] = useState(() => readSession());
  const [registerForm, setRegisterForm] = useState(initialRegisterForm);
  const [loginForm, setLoginForm] = useState(initialLoginForm);
  const [validationToken, setValidationToken] = useState('');
  const [recoveryForm, setRecoveryForm] = useState(initialRecoveryForm);
  const [profile, setProfile] = useState(null);
  const [profileForm, setProfileForm] = useState(initialClienteProfile);
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);

  const currentView = useMemo(
    () => views.find((view) => view.id === activeView),
    [activeView]
  );

  useEffect(() => {
    if (!session?.tokenSesion) {
      setProfile(null);
      return;
    }

    loadProfile(session.tokenSesion);
  }, [session?.tokenSesion]);

  useEffect(() => {
    if (!session?.usuario) {
      return;
    }

    if (profile) {
      setProfileForm(mapProfileToForm(profile, session.usuario.rol));
      return;
    }

    setProfileForm(session.usuario.rol === 'TRABAJADOR' ? initialTrabajadorProfile : initialClienteProfile);
  }, [profile, session?.usuario]);

  async function runAction(action, successMessage) {
    setLoading(true);
    setStatus(null);

    try {
      const result = await action();
      setStatus({ type: 'success', message: successMessage, data: result });
      return result;
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
      return null;
    } finally {
      setLoading(false);
    }
  }

  async function handleRegister(event) {
    event.preventDefault();

    const result = await runAction(
      () => apiRequest('/api/usuarios/registro', { method: 'POST', body: registerForm }),
      'Usuario registrado. La cuenta queda pendiente de validacion.'
    );

    if (result?.tokenValidacionCuenta) {
      setValidationToken(result.tokenValidacionCuenta);
      setActiveView('validacion');
    }
  }

  async function handleValidate(event) {
    event.preventDefault();

    await runAction(
      () => apiRequest(`/api/usuarios/validacion?token=${encodeURIComponent(validationToken)}`),
      'Cuenta validada correctamente. Ya podes iniciar sesion.'
    );
  }

  async function handleLogin(event) {
    event.preventDefault();

    const result = await runAction(
      () => apiRequest('/api/usuarios/login', { method: 'POST', body: loginForm }),
      'Inicio de sesion exitoso.'
    );

    if (result?.tokenSesion) {
      saveSession(result);
      setSession(result);
      setActiveView('perfil');
    }
  }

  async function handleLogout() {
    if (!session?.tokenSesion) {
      clearSession();
      setSession(null);
      setActiveView('login');
      return;
    }

    await runAction(
      () => apiRequest('/api/usuarios/logout', { method: 'POST', token: session.tokenSesion }),
      'Sesion cerrada correctamente.'
    );

    clearSession();
    setSession(null);
    setProfile(null);
    setActiveView('login');
  }

  async function handleRecoveryRequest(event) {
    event.preventDefault();

    const result = await runAction(
      () => apiRequest('/api/usuarios/recuperacion-contrasena', {
        method: 'POST',
        body: { mail: recoveryForm.mail }
      }),
      'Se genero el enlace de recuperacion.'
    );

    if (result?.tokenRecuperacion) {
      setRecoveryForm((current) => ({
        ...current,
        tokenRecuperacion: result.tokenRecuperacion
      }));
    }
  }

  async function handleRecoveryConfirm(event) {
    event.preventDefault();

    await runAction(
      () => apiRequest('/api/usuarios/recuperacion-contrasena/confirmacion', {
        method: 'POST',
        body: {
          tokenRecuperacion: recoveryForm.tokenRecuperacion,
          contrasena: recoveryForm.contrasena,
          confirmacionContrasena: recoveryForm.confirmacionContrasena
        }
      }),
      'Contrasena actualizada correctamente.'
    );
  }

  async function loadProfile(token) {
    try {
      const result = await apiRequest('/api/perfiles/me', { token });
      setProfile(result);
    } catch (error) {
      setProfile(null);
    }
  }

  async function handleProfileSubmit(event) {
    event.preventDefault();

    if (!session?.tokenSesion || !session?.usuario?.rol) {
      setStatus({ type: 'error', message: 'Debe iniciar sesion para gestionar el perfil.' });
      return;
    }

    const role = session.usuario.rol;
    const isWorker = role === 'TRABAJADOR';
    const path = isWorker
      ? profile ? '/api/perfiles/trabajadores/me' : '/api/perfiles/trabajadores'
      : profile ? '/api/perfiles/clientes/me' : '/api/perfiles/clientes';
    const method = profile ? 'PUT' : 'POST';
    const body = isWorker ? mapWorkerProfileBody(profileForm) : profileForm;

    const result = await runAction(
      () => apiRequest(path, { method, body, token: session.tokenSesion }),
      profile ? 'Perfil actualizado correctamente.' : 'Perfil creado correctamente.'
    );

    if (result?.perfil) {
      setProfile(result.perfil);
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-lockup">
          <div className="brand-mark" aria-hidden="true">RF</div>
          <div>
            <h1>RitualFresh</h1>
            <span>Gestion de usuarios y perfiles</span>
          </div>
        </div>
        <SessionSummary session={session} onLogout={handleLogout} />
      </header>

      <main className="workspace">
        <aside className="sidebar" aria-label="Modulos">
          <div className="module-label">M01 / M02</div>
          <div className="nav-group">
            {views.map((view) => (
              <button
                type="button"
                key={view.id}
                className={`nav-action ${activeView === view.id ? 'active' : ''}`}
                onClick={() => {
                  setActiveView(view.id);
                  setStatus(null);
                }}
              >
                <span>{view.label}</span>
                <small>{view.module}</small>
              </button>
            ))}
          </div>
        </aside>

        <section className="content">
          <div className="content-header">
            <div>
              <span className="eyebrow">{currentView?.module}</span>
              <h2>{currentView?.label}</h2>
            </div>
          </div>

          {status && <StatusMessage status={status} />}

          {activeView === 'registro' && (
            <RegisterForm
              form={registerForm}
              onChange={setRegisterForm}
              onSubmit={handleRegister}
              loading={loading}
            />
          )}

          {activeView === 'validacion' && (
            <ValidationForm
              token={validationToken}
              setToken={setValidationToken}
              onSubmit={handleValidate}
              loading={loading}
            />
          )}

          {activeView === 'login' && (
            <LoginForm
              form={loginForm}
              onChange={setLoginForm}
              onSubmit={handleLogin}
              loading={loading}
            />
          )}

          {activeView === 'recuperacion' && (
            <RecoveryForm
              form={recoveryForm}
              onChange={setRecoveryForm}
              onRequest={handleRecoveryRequest}
              onConfirm={handleRecoveryConfirm}
              loading={loading}
            />
          )}

          {activeView === 'perfil' && (
            <ProfilePanel
              session={session}
              profile={profile}
              form={profileForm}
              onChange={setProfileForm}
              onSubmit={handleProfileSubmit}
              loading={loading}
              goLogin={() => setActiveView('login')}
            />
          )}
        </section>
      </main>
    </div>
  );
}

function RegisterForm({ form, onChange, onSubmit, loading }) {
  return (
    <form className="form-grid" onSubmit={onSubmit}>
      <Field label="Nombre" value={form.nombre} onChange={(value) => onChange({ ...form, nombre: value })} required />
      <Field label="Apellido" value={form.apellido} onChange={(value) => onChange({ ...form, apellido: value })} required />
      <Field label="DNI" value={form.dni} onChange={(value) => onChange({ ...form, dni: value })} required />
      <Field label="Telefono" value={form.telefono} onChange={(value) => onChange({ ...form, telefono: value })} required />
      <Field label="Correo electronico" type="email" value={form.mail} onChange={(value) => onChange({ ...form, mail: value })} required />
      <SelectField
        label="Rol"
        value={form.rol}
        onChange={(value) => onChange({ ...form, rol: value })}
        options={[
          { value: 'CLIENTE', label: 'Cliente' },
          { value: 'TRABAJADOR', label: 'Trabajador' }
        ]}
      />
      <Field label="Contrasena" type="password" value={form.contrasena} onChange={(value) => onChange({ ...form, contrasena: value })} required />
      <Field label="Confirmar contrasena" type="password" value={form.confirmacionContrasena} onChange={(value) => onChange({ ...form, confirmacionContrasena: value })} required />
      <FormActions label="Registrar usuario" loading={loading} />
    </form>
  );
}

function ValidationForm({ token, setToken, onSubmit, loading }) {
  return (
    <form className="form-grid single-column" onSubmit={onSubmit}>
      <Field label="Token de validacion" value={token} onChange={setToken} required />
      <FormActions label="Validar cuenta" loading={loading} />
    </form>
  );
}

function LoginForm({ form, onChange, onSubmit, loading }) {
  return (
    <form className="form-grid single-column" onSubmit={onSubmit}>
      <Field label="Correo electronico" type="email" value={form.mail} onChange={(value) => onChange({ ...form, mail: value })} required />
      <Field label="Contrasena" type="password" value={form.contrasena} onChange={(value) => onChange({ ...form, contrasena: value })} required />
      <FormActions label="Iniciar sesion" loading={loading} />
    </form>
  );
}

function RecoveryForm({ form, onChange, onRequest, onConfirm, loading }) {
  return (
    <div className="split-layout">
      <form className="form-grid single-column" onSubmit={onRequest}>
        <h3>Solicitar enlace</h3>
        <Field label="Correo electronico" type="email" value={form.mail} onChange={(value) => onChange({ ...form, mail: value })} required />
        <FormActions label="Solicitar recuperacion" loading={loading} />
      </form>

      <form className="form-grid single-column" onSubmit={onConfirm}>
        <h3>Cambiar contrasena</h3>
        <Field label="Token de recuperacion" value={form.tokenRecuperacion} onChange={(value) => onChange({ ...form, tokenRecuperacion: value })} required />
        <Field label="Nueva contrasena" type="password" value={form.contrasena} onChange={(value) => onChange({ ...form, contrasena: value })} required />
        <Field label="Confirmar nueva contrasena" type="password" value={form.confirmacionContrasena} onChange={(value) => onChange({ ...form, confirmacionContrasena: value })} required />
        <FormActions label="Actualizar contrasena" loading={loading} />
      </form>
    </div>
  );
}

function ProfilePanel({ session, profile, form, onChange, onSubmit, loading, goLogin }) {
  if (!session?.usuario) {
    return (
      <div className="empty-state">
        <h3>No hay sesion activa</h3>
        <button type="button" className="btn btn-primary" onClick={goLogin}>Ir a login</button>
      </div>
    );
  }

  const isWorker = session.usuario.rol === 'TRABAJADOR';

  return (
    <div className="profile-layout">
      <div className="profile-summary">
        <span className="eyebrow">{isWorker ? 'US01-M02-RF01' : 'US02-M02-RF02'}</span>
        <h3>{profile ? 'Perfil cargado' : 'Perfil pendiente'}</h3>
        <dl>
          <dt>Usuario</dt>
          <dd>{session.usuario.nombre} {session.usuario.apellido}</dd>
          <dt>Rol</dt>
          <dd>{session.usuario.rol}</dd>
          <dt>Correo</dt>
          <dd>{session.usuario.mail}</dd>
        </dl>
      </div>

      <form className="form-grid" onSubmit={onSubmit}>
        {isWorker ? (
          <WorkerProfileFields form={form} onChange={onChange} />
        ) : (
          <ClientProfileFields form={form} onChange={onChange} />
        )}
        <FormActions label={profile ? 'Actualizar perfil' : 'Crear perfil'} loading={loading} />
      </form>
    </div>
  );
}

function ClientProfileFields({ form, onChange }) {
  return (
    <>
      <Field label="URL foto de perfil" value={form.urlFotoPerfil} onChange={(value) => onChange({ ...form, urlFotoPerfil: value })} />
      <Field label="Telefono de contacto" value={form.telefonoContacto} onChange={(value) => onChange({ ...form, telefonoContacto: value })} required />
      <Field label="Calle" value={form.nombreCalle} onChange={(value) => onChange({ ...form, nombreCalle: value })} required />
      <Field label="Numero" value={form.numeroDomicilio} onChange={(value) => onChange({ ...form, numeroDomicilio: value })} required />
      <Field label="Piso" value={form.piso} onChange={(value) => onChange({ ...form, piso: value })} />
      <Field label="Departamento" value={form.departamentoDomicilio} onChange={(value) => onChange({ ...form, departamentoDomicilio: value })} />
      <Field label="Codigo postal" value={form.codigoPostal} onChange={(value) => onChange({ ...form, codigoPostal: value })} required />
      <Field label="Localidad" value={form.localidad} onChange={(value) => onChange({ ...form, localidad: value })} required />
      <Field label="Provincia" value={form.provincia} onChange={(value) => onChange({ ...form, provincia: value })} required />
      <TextAreaField label="Preferencias de contratacion" value={form.preferenciasContratacion} onChange={(value) => onChange({ ...form, preferenciasContratacion: value })} required />
    </>
  );
}

function WorkerProfileFields({ form, onChange }) {
  return (
    <>
      <Field label="URL foto de perfil" value={form.urlFotoPerfil} onChange={(value) => onChange({ ...form, urlFotoPerfil: value })} />
      <TextAreaField label="Descripcion" value={form.descripcion} onChange={(value) => onChange({ ...form, descripcion: value })} required />
      <Field label="Anios de experiencia" type="number" min="0" value={form.aniosExperiencia} onChange={(value) => onChange({ ...form, aniosExperiencia: value })} required />
      <TextAreaField label="Servicios ofrecidos" value={form.serviciosOfrecidos} onChange={(value) => onChange({ ...form, serviciosOfrecidos: value })} required />
      <Field label="Zona de trabajo" value={form.zonaTrabajo} onChange={(value) => onChange({ ...form, zonaTrabajo: value })} required />
      <Field label="Disponibilidad" value={form.disponibilidad} onChange={(value) => onChange({ ...form, disponibilidad: value })} required />
      <Field label="Precio por hora orientativo" type="number" min="1" step="0.01" value={form.precioHoraOrientativo} onChange={(value) => onChange({ ...form, precioHoraOrientativo: value })} required />
    </>
  );
}

function Field({ label, value, onChange, type = 'text', required = false, min, step }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input
        className="form-control"
        type={type}
        value={value}
        min={min}
        step={step}
        required={required}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  );
}

function TextAreaField({ label, value, onChange, required = false }) {
  return (
    <label className="field field-wide">
      <span>{label}</span>
      <textarea
        className="form-control"
        rows="4"
        value={value}
        required={required}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  );
}

function SelectField({ label, value, onChange, options }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select className="form-select" value={value} onChange={(event) => onChange(event.target.value)}>
        {options.map((option) => (
          <option key={option.value} value={option.value}>{option.label}</option>
        ))}
      </select>
    </label>
  );
}

function FormActions({ label, loading }) {
  return (
    <div className="form-actions">
      <button type="submit" className="btn btn-primary" disabled={loading}>
        {loading ? 'Procesando...' : label}
      </button>
    </div>
  );
}

function StatusMessage({ status }) {
  return (
    <div className={`status-message ${status.type}`}>
      <strong>{status.type === 'error' ? 'Error' : 'OK'}</strong>
      <span>{status.message}</span>
      {status.data?.tokenValidacionCuenta && (
        <code>{status.data.tokenValidacionCuenta}</code>
      )}
      {status.data?.tokenRecuperacion && (
        <code>{status.data.tokenRecuperacion}</code>
      )}
    </div>
  );
}

function SessionSummary({ session, onLogout }) {
  if (!session?.usuario) {
    return <span className="session-pill">Sin sesion</span>;
  }

  return (
    <div className="session-box">
      <span>{session.usuario.nombre} {session.usuario.apellido}</span>
      <strong>{session.usuario.rol}</strong>
      <button type="button" className="btn btn-outline-light btn-sm" onClick={onLogout}>Salir</button>
    </div>
  );
}

function readSession() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function saveSession(session) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

function clearSession() {
  localStorage.removeItem(STORAGE_KEY);
}

function mapProfileToForm(profile, role) {
  if (role === 'TRABAJADOR') {
    return {
      urlFotoPerfil: profile.urlFotoPerfil ?? '',
      descripcion: profile.descripcion ?? '',
      aniosExperiencia: profile.aniosExperiencia?.toString() ?? '',
      serviciosOfrecidos: profile.serviciosOfrecidos ?? '',
      zonaTrabajo: profile.zonaTrabajo ?? '',
      disponibilidad: profile.disponibilidad ?? '',
      precioHoraOrientativo: profile.precioHoraOrientativo?.toString() ?? ''
    };
  }

  return {
    urlFotoPerfil: profile.urlFotoPerfil ?? '',
    telefonoContacto: profile.telefonoContacto ?? '',
    nombreCalle: profile.nombreCalle ?? '',
    numeroDomicilio: profile.numeroDomicilio ?? '',
    piso: profile.piso ?? '',
    departamentoDomicilio: profile.departamentoDomicilio ?? '',
    codigoPostal: profile.codigoPostal ?? '',
    localidad: profile.localidad ?? '',
    provincia: profile.provincia ?? '',
    preferenciasContratacion: profile.preferenciasContratacion ?? ''
  };
}

function mapWorkerProfileBody(form) {
  return {
    ...form,
    aniosExperiencia: Number(form.aniosExperiencia),
    precioHoraOrientativo: Number(form.precioHoraOrientativo)
  };
}

export default App;
