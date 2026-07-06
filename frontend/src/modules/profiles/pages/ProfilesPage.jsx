import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import '../../auth/styles/auth.css';
import '../styles/profile.css';
import { useAuth } from '../../auth/hooks/useAuth.js';
import { createMyProfile, getMyProfile, updateMyProfile, uploadPhoto } from '../services/profileService.js';

export function ProfilesPage() {
  const { user, role } = useAuth();
  const [profile, setProfile] = useState(null);
  const [formData, setFormData] = useState(() => getInitialFormData(role));
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [hasProfile, setHasProfile] = useState(false);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);

  const pageMeta = useMemo(() => getPageMeta(role), [role]);

  useEffect(() => {
    setFormData(getInitialFormData(role));
  }, [role]);

  useEffect(() => {
    let cancelled = false;

    async function loadProfile() {
      setIsLoading(true);
      setErrorMessage('');
      setSuccessMessage('');

      try {
        const currentProfile = await getMyProfile();
        if (cancelled) {
          return;
        }

        setProfile(currentProfile);
        setFormData(mapProfileToFormData(role, currentProfile));
        setHasProfile(true);
      } catch (error) {
        if (cancelled) {
          return;
        }

        if (error.message === 'El usuario no posee un perfil creado.') {
          setProfile(null);
          setFormData(getInitialFormData(role));
          setHasProfile(false);
        } else {
          setErrorMessage(error.message);
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    loadProfile();

    return () => {
      cancelled = true;
    };
  }, [role]);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleFileChange(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    setErrorMessage('');
    setUploadingPhoto(true);

    try {
      const result = await uploadPhoto(file);
      setFormData((current) => ({ ...current, photoUrl: result.url }));
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setUploadingPhoto(false);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');
    setIsSubmitting(true);

    try {
      const payload = buildPayload(role, formData);
      const response = hasProfile ? await updateMyProfile(role, payload) : await createMyProfile(role, payload);
      setProfile(response.profile);
      setFormData(mapProfileToFormData(role, response.profile));
      setHasProfile(true);
      setSuccessMessage(response.message);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="screen profile-screen">
      <section className="profile-hero">
        <h1>{pageMeta.title}</h1>
        <p className="muted">{pageMeta.description}</p>

        <div className="profile-summary-card">
          <p className="profile-summary-card__label">Cuenta activa</p>
          <h2>
            {user?.firstName} {user?.lastName}
          </h2>
          <p>{user?.email}</p>
          <p className="muted">
            {hasProfile ? 'Ya existe un perfil cargado y puede editarse.' : 'Todavía no hay un perfil creado para esta cuenta.'}
          </p>
          <Link className="button button--ghost" to={role === 'WORKER' ? '/worker/home' : '/client/home'}>
            Volver al inicio
          </Link>
        </div>
      </section>

      <section className="card profile-card">
        <div className="profile-card__header">
          <div>
            <p className="eyebrow">{hasProfile ? 'Edición' : 'Alta inicial'}</p>
            <h2>{hasProfile ? pageMeta.editTitle : pageMeta.createTitle}</h2>
          </div>
          <span className={`profile-badge ${hasProfile ? 'profile-badge--ready' : 'profile-badge--draft'}`}>
            {hasProfile ? 'Perfil cargado' : 'Pendiente'}
          </span>
        </div>

        {isLoading ? <p className="muted">Cargando información del perfil...</p> : null}
        {!isLoading && errorMessage ? <p className="feedback feedback--error">{errorMessage}</p> : null}
        {!isLoading && successMessage ? <p className="feedback feedback--success">{successMessage}</p> : null}

        {!isLoading && !errorMessage ? (
          <form className="auth-form auth-form--grid" onSubmit={handleSubmit}>
            {renderFields(role, formData, handleChange, uploadingPhoto, handleFileChange)}

            <div className="field field--full">
              <button className="button button--primary" type="submit" disabled={isSubmitting}>
                {isSubmitting
                  ? (hasProfile ? 'Guardando cambios...' : 'Creando perfil...')
                  : (hasProfile ? 'Guardar cambios' : 'Crear perfil')}
              </button>
            </div>
          </form>
        ) : null}

        {profile ? (
          <section className="profile-details">
            <h3>Resumen actual</h3>
            <dl className="profile-details__grid">
              {getProfileSummary(role, profile).map((item) => (
                <div key={item.label}>
                  <dt>{item.label}</dt>
                  <dd>{item.value || '-'}</dd>
                </div>
              ))}
            </dl>
          </section>
        ) : null}
      </section>
    </main>
  );
}

function renderFields(role, formData, handleChange, uploadingPhoto, handleFileChange) {
  if (role === 'WORKER') {
    return (
      <>
        <PhotoField photoUrl={formData.photoUrl} uploading={uploadingPhoto} onChange={handleFileChange} />
        <Field required label="Años de experiencia" name="yearsOfExperience" type="number" min="0" value={formData.yearsOfExperience} onChange={handleChange} placeholder="3" />
        <Field required className="field--full" label="Descripción profesional" name="description" value={formData.description} onChange={handleChange} placeholder="Experiencia, estilo de trabajo y fortalezas." />
        <Field required className="field--full" label="Servicios ofrecidos" name="offeredServices" value={formData.offeredServices} onChange={handleChange} placeholder="Limpieza profunda, planchado, cocina, niñera..." />
        <Field required label="Zona de trabajo" name="workArea" value={formData.workArea} onChange={handleChange} placeholder="Godoy Cruz, Ciudad, Guaymallén" />
        <Field required label="Disponibilidad" name="availability" value={formData.availability} onChange={handleChange} placeholder="Lunes a viernes, mañana y tarde" />
        <Field required label="Precio por hora" name="hourlyRate" type="number" min="0.01" step="0.01" value={formData.hourlyRate} onChange={handleChange} placeholder="6500" />
      </>
    );
  }

  return (
    <>
      <PhotoField photoUrl={formData.photoUrl} uploading={uploadingPhoto} onChange={handleFileChange} />
      <Field required label="Teléfono" name="contactPhone" value={formData.contactPhone} onChange={handleChange} placeholder="+54 261 555 1234" />
      <Field required label="Calle" name="streetName" value={formData.streetName} onChange={handleChange} placeholder="San Martín" />
      <Field required label="Número" name="streetNumber" value={formData.streetNumber} onChange={handleChange} placeholder="1234" />
      <Field label="Piso" name="floor" value={formData.floor} onChange={handleChange} placeholder="Opcional" />
      <Field label="Departamento" name="apartment" value={formData.apartment} onChange={handleChange} placeholder="Opcional" />
      <Field required label="Código postal" name="postalCode" value={formData.postalCode} onChange={handleChange} placeholder="5500" />
      <Field required label="Ciudad" name="city" value={formData.city} onChange={handleChange} placeholder="Mendoza" />
      <Field required label="Provincia" name="province" value={formData.province} onChange={handleChange} placeholder="Mendoza" />
      <Field required className="field--full" label="Preferencias de contratación" name="hiringPreferences" value={formData.hiringPreferences} onChange={handleChange} placeholder="Frecuencia, horarios, servicios prioritarios..." />
    </>
  );
}

function PhotoField({ photoUrl, uploading, onChange }) {
  return (
    <div className="field field--full">
      <span>Foto de perfil</span>
      <div className="photo-field">
        {photoUrl ? (
          <div className="photo-field__preview">
            <img src={photoUrl.startsWith('http') ? photoUrl : `http://localhost:8080${photoUrl}`} alt="Foto de perfil" />
          </div>
        ) : null}
        <label className="button button--secondary photo-field__upload">
          {uploading ? 'Subiendo...' : (photoUrl ? 'Cambiar foto' : 'Seleccionar foto')}
          <input type="file" accept="image/*" onChange={onChange} disabled={uploading} hidden />
        </label>
      </div>
    </div>
  );
}

function Field({ className = '', label, ...props }) {
  const classes = className ? `field ${className}` : 'field';

  return (
    <label className={classes}>
      <span>{label}</span>
      <input {...props} />
    </label>
  );
}

function getInitialFormData(role) {
  if (role === 'WORKER') {
    return {
      photoUrl: '',
      description: '',
      yearsOfExperience: '',
      offeredServices: '',
      workArea: '',
      availability: '',
      hourlyRate: '',
    };
  }

  return {
    photoUrl: '',
    contactPhone: '',
    streetName: '',
    streetNumber: '',
    floor: '',
    apartment: '',
    postalCode: '',
    city: '',
    province: '',
    hiringPreferences: '',
  };
}

function mapProfileToFormData(role, profile) {
  if (role === 'WORKER') {
    return {
      photoUrl: profile.photoUrl || '',
      description: profile.description || '',
      yearsOfExperience: profile.yearsOfExperience?.toString() || '',
      offeredServices: profile.offeredServices || '',
      workArea: profile.workArea || '',
      availability: profile.availability || '',
      hourlyRate: profile.hourlyRate?.toString() || '',
    };
  }

  return {
    photoUrl: profile.photoUrl || '',
    contactPhone: profile.contactPhone || '',
    streetName: profile.streetName || '',
    streetNumber: profile.streetNumber || '',
    floor: profile.floor || '',
    apartment: profile.apartment || '',
    postalCode: profile.postalCode || '',
    city: profile.city || '',
    province: profile.province || '',
    hiringPreferences: profile.hiringPreferences || '',
  };
}

function buildPayload(role, formData) {
  if (role === 'WORKER') {
    return {
      photoUrl: formData.photoUrl,
      description: formData.description,
      yearsOfExperience: toNullableInteger(formData.yearsOfExperience),
      offeredServices: formData.offeredServices,
      workArea: formData.workArea,
      availability: formData.availability,
      hourlyRate: toNullableNumber(formData.hourlyRate),
    };
  }

  return {
    photoUrl: formData.photoUrl,
    contactPhone: formData.contactPhone,
    streetName: formData.streetName,
    streetNumber: formData.streetNumber,
    floor: formData.floor,
    apartment: formData.apartment,
    postalCode: formData.postalCode,
    city: formData.city,
    province: formData.province,
    hiringPreferences: formData.hiringPreferences,
  };
}

function toNullableInteger(value) {
  if (value === '') {
    return null;
  }

  return Number.parseInt(value, 10);
}

function toNullableNumber(value) {
  if (value === '') {
    return null;
  }

  return Number(value);
}

function getPageMeta(role) {
  if (role === 'WORKER') {
    return {
      title: 'Mi perfil profesional',
      description: 'Complete o actualice su presentación, experiencia, disponibilidad y precio orientativo.',
      createTitle: 'Crear perfil de trabajador',
      editTitle: 'Editar perfil de trabajador',
    };
  }

  return {
    title: 'Mi perfil de cliente',
    description: 'Cargue sus datos de contacto y domicilio para facilitar futuras contrataciones.',
    createTitle: 'Crear perfil de cliente',
    editTitle: 'Editar perfil de cliente',
  };
}

function getProfileSummary(role, profile) {
  if (role === 'WORKER') {
    return [
      { label: 'Servicios', value: profile.offeredServices },
      { label: 'Experiencia', value: formatYears(profile.yearsOfExperience) },
      { label: 'Zona', value: profile.workArea },
      { label: 'Disponibilidad', value: profile.availability },
      { label: 'Precio por hora', value: formatCurrency(profile.hourlyRate) },
      { label: 'Ranking', value: profile.rankingPosition?.toString() || '-' },
    ];
  }

  return [
    { label: 'Teléfono', value: profile.contactPhone },
    { label: 'Dirección', value: formatAddress(profile) },
    { label: 'Ciudad', value: `${profile.city || '-'}, ${profile.province || '-'}` },
    { label: 'Código postal', value: profile.postalCode },
    { label: 'Preferencias', value: profile.hiringPreferences },
    { label: 'Calificación', value: profile.clientRating?.toString() || '-' },
  ];
}

function formatAddress(profile) {
  return [profile.streetName, profile.streetNumber, profile.floor, profile.apartment].filter(Boolean).join(' ');
}

function formatYears(value) {
  if (value === null || value === undefined) {
    return '-';
  }

  return `${value} año${value === 1 ? '' : 's'}`;
}

function formatCurrency(value) {
  if (value === null || value === undefined || value === '') {
    return '-';
  }

  const amount = Number(value);
  if (Number.isNaN(amount)) {
    return String(value);
  }

  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    maximumFractionDigits: 0,
  }).format(amount);
}
