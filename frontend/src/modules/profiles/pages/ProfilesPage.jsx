import { useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import gsap from 'gsap';
import '../../auth/styles/auth.css';
import '../styles/profile.css';
import { useAuth } from '../../auth/hooks/useAuth.js';
import { createMyProfile, getMyProfile, updateMyProfile, uploadPhoto } from '../services/profileService.js';
import {
  FiBarChart2,
  FiBriefcase,
  FiCalendar,
  FiCamera,
  FiClock,
  FiDollarSign,
  FiFileText,
  FiHash,
  FiHome,
  FiLock,
  FiMail,
  FiMap,
  FiMapPin,
  FiPhone,
  FiSave,
  FiUser,
} from 'react-icons/fi';
import { FormField } from '../../../shared/components/FormField.jsx';

const SPECIALTY_OPTIONS = [
  'Limpieza general',
  'Limpieza profunda',
  'Mantenimiento del hogar',
  'Organización y orden',
  'Lavado y planchado',
  'Cuidado de plantas',
  'Pequeñas reparaciones',
  'Otros',
];

const WEEK_DAYS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];

const DEFAULT_FROM_TIME = '09:00';
const DEFAULT_TO_TIME = '17:00';

export function ProfilesPage() {
  const { user, role, refreshSession } = useAuth();
  const [profile, setProfile] = useState(null);
  const [formData, setFormData] = useState(() => getInitialFormData(role));
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [hasProfile, setHasProfile] = useState(false);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const [touched, setTouched] = useState({});
  const [submitted, setSubmitted] = useState(false);

  const pageMeta = useMemo(() => getPageMeta(role), [role]);
  const completion = useMemo(() => getProfileCompletion(role, formData), [role, formData]);
  const fieldErrors = useMemo(() => getFieldErrors(role, formData), [role, formData]);
  const shouldShowError = (name) => {
    return !!submitted;
  };

  const handleBlur = (event) => {
    const { name } = event.target;
    if (name) {
      setTouched((prev) => ({ ...prev, [name]: true }));
    }
  };

  const scrollToFirstError = () => {
    const firstErrorField = document.querySelector('.form-field__error');
    if (firstErrorField) {
      firstErrorField.closest('.profile-section')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  };

  const cardRef = useRef(null);
  const sectionsRef = useRef([]);
  const progressSpanRef = useRef(null);
  const feedbackRef = useRef(null);

  useEffect(() => {
    if (isLoading) return;
    const ctx = gsap.context(() => {
      gsap.from(cardRef.current, { y: 24, opacity: 0, duration: 0.5, ease: 'power2.out' });
      gsap.from(sectionsRef.current.filter(Boolean), {
        y: 16,
        opacity: 0,
        duration: 0.4,
        stagger: 0.08,
        ease: 'power2.out',
      });
    });
    return () => ctx.revert();
  }, [isLoading]);

  useEffect(() => {
    if (feedbackRef.current) {
      gsap.from(feedbackRef.current, { y: -8, opacity: 0, duration: 0.3, ease: 'power2.out' });
    }
  }, [errorMessage, successMessage]);

  useEffect(() => {
    setFormData(getInitialFormData(role));
    setTouched({});
    setSubmitted(false);
  }, [role]);

  useEffect(() => {
    let cancelled = false;

    async function loadProfile() {
      setIsLoading(true);
      setErrorMessage('');
      setSuccessMessage('');
      setTouched({});
      setSubmitted(false);

      try {
        const currentProfile = await getMyProfile();
        if (cancelled) return;

        setProfile(currentProfile);
        setFormData(mapProfileToFormData(role, currentProfile));
        setHasProfile(true);
        setTouched({});
        setSubmitted(false);
      } catch (error) {
        if (cancelled) return;

        if (error.message === 'El usuario no posee un perfil creado.') {
          setProfile(null);
          setFormData(getInitialFormData(role));
          setHasProfile(false);
          setTouched({});
          setSubmitted(false);
        } else {
          setErrorMessage(error.message);
        }
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }

    loadProfile();
    return () => { cancelled = true; };
  }, [role]);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  function updateWorkerSpecialties(nextSpecialties) {
    setFormData((current) => ({ ...current, offeredServices: nextSpecialties.join(', ') }));
  }

  function updateWorkerAvailability(nextAvailability) {
    setFormData((current) => ({ ...current, availability: serializeAvailability(nextAvailability) }));
  }

  async function handleFileChange(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    setErrorMessage('');
    setUploadingPhoto(true);
    setTouched((prev) => ({ ...prev, photoUrl: true }));

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
    setSubmitted(true);

    const errors = getFieldErrors(role, formData);
    if (Object.keys(errors).length > 0) {
      setErrorMessage('Revisá los campos antes de guardar.');
      setTimeout(scrollToFirstError, 100);
      return;
    }

    setIsSubmitting(true);

    try {
      const payload = buildPayload(role, formData);
      const response = hasProfile ? await updateMyProfile(role, payload) : await createMyProfile(role, payload);
      setProfile(response.profile);
      setFormData(mapProfileToFormData(role, response.profile));
      setHasProfile(true);
      await refreshSession();
      setSuccessMessage(response.message);
      setSubmitted(false);
      setTouched({});
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="profile-page">
      <section className="profile-card" ref={cardRef}>
        <header className="profile-card__header">
          <div className="profile-card__header-row">
            <h1>{pageMeta.title}</h1>
          </div>
          <p className="profile-card__subtitle">{pageMeta.description}</p>

          {!isLoading && (
            <div className="profile-progress" data-complete={completion >= 100}>
              <div className="profile-progress__row">
                <span>Progreso</span>
                <strong>{completion}%</strong>
              </div>
              <div className="profile-progress__bar" aria-hidden="true">
                <span ref={progressSpanRef} style={{ width: `${completion}%` }} />
              </div>
              <p className="profile-progress__hint">
                {completion >= 100 ? 'Listo para guardar' : 'Completá los campos pendientes para continuar'}
              </p>
            </div>
          )}
        </header>

        {isLoading ? <p className="muted">Cargando información del perfil...</p> : null}
        {!isLoading && errorMessage ? <p className="feedback feedback--error" ref={feedbackRef}>{errorMessage}</p> : null}
        {!isLoading && successMessage ? <p className="feedback feedback--success" ref={feedbackRef}>{successMessage}</p> : null}

        {!isLoading && !errorMessage ? (
          <form className="profile-form" onSubmit={handleSubmit}>
            <section className="profile-section profile-section--avatar" ref={(el) => (sectionsRef.current[0] = el)}>
              <div className="profile-section__header">
                <h3>Foto de perfil *</h3>
              </div>
              <PhotoField
                photoUrl={formData.photoUrl}
                uploading={uploadingPhoto}
                onChange={handleFileChange}
                error={shouldShowError('photoUrl') ? fieldErrors.photoUrl : null}
              />
            </section>

            <section className="profile-section" ref={(el) => (sectionsRef.current[1] = el)}>
              <div className="profile-section__header">
                <h3>Datos personales</h3>
              </div>

              <div className="profile-account-grid">
                <div className="profile-account-chip">
                  <FiUser />
                  <div>
                    <span>Nombre completo</span>
                    <strong>{formData.firstName} {formData.lastName}</strong>
                  </div>
                </div>
                <div className="profile-account-chip">
                  <FiMail />
                  <div>
                    <span>Correo electrónico</span>
                    <strong>{user?.email}</strong>
                  </div>
                </div>
                <div className="profile-account-chip">
                  <FiBriefcase />
                  <div>
                    <span>Rol</span>
                    <strong>{formatRole(role)}</strong>
                  </div>
                </div>
              </div>
            </section>

            <div ref={(el) => (sectionsRef.current[2] = el)}>
              {renderFields(
                role,
                formData,
                handleChange,
                fieldErrors,
                updateWorkerSpecialties,
                updateWorkerAvailability,
                handleBlur,
                shouldShowError
              )}
            </div>

            <div className="profile-actions">
              <Link className="button button--ghost" to={role === 'WORKER' ? '/worker/home' : '/client/home'}>
                Cancelar
              </Link>
              <button className="button button--primary" type="submit" disabled={isSubmitting}>
                <FiSave />
                <span>
                  {isSubmitting
                    ? (role === 'WORKER' ? 'Guardando perfil...' : (hasProfile ? 'Guardando cambios...' : 'Creando perfil...'))
                    : (role === 'WORKER' ? 'Guardar perfil' : (hasProfile ? 'Guardar cambios' : 'Crear perfil'))}
                </span>
              </button>
            </div>
          </form>
        ) : null}
      </section>
    </main>
  );
}

function renderFields(
  role,
  formData,
  handleChange,
  fieldErrors,
  updateWorkerSpecialties,
  updateWorkerAvailability,
  handleBlur,
  shouldShowError
) {
  if (role === 'WORKER') {
    const selectedSpecialties = parseSpecialties(formData.offeredServices);
    const availability = parseAvailability(formData.availability);

    return (
      <>
      <section className="profile-section">
        <div className="profile-section__header">
          <h3>Datos personales y profesionales *</h3>
        </div>

        <div className="profile-grid profile-grid--two">
          <ProfileField
            required
            label="Nombre"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Ana"
            error={shouldShowError('firstName') ? fieldErrors.firstName : null}
          />
          <ProfileField
            required
            label="Apellido"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Pérez"
            error={shouldShowError('lastName') ? fieldErrors.lastName : null}
          />
        </div>

        <div className="profile-grid profile-grid--two">
          <ProfileField
            required
            label="Teléfono"
            name="contactPhone"
            value={formData.contactPhone}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="+54 261 555 1234"
            error={shouldShowError('contactPhone') ? fieldErrors.contactPhone : null}
          />
          <ProfileField
            required
            label="Ciudad/Zona"
            name="workArea"
            value={formData.workArea}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Mendoza, Mendoza"
            error={shouldShowError('workArea') ? fieldErrors.workArea : null}
          />
          <ProfileField
            required
            label="Experiencia"
            name="yearsOfExperience"
            type="number"
            min="0"
            value={formData.yearsOfExperience}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="3"
            error={shouldShowError('yearsOfExperience') ? fieldErrors.yearsOfExperience : null}
          />
        </div>

        <ProfileField
          required
          className="field--full"
          label="Descripción personal"
          name="description"
          value={formData.description}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Cuéntanos sobre tu experiencia, valores y forma de trabajar..."
          multiline
          rows={5}
          error={shouldShowError('description') ? fieldErrors.description : null}
        />
      </section>

      <section className="profile-section">
        <SpecialtySelector
          selected={selectedSpecialties}
          onChange={(nextSpecialties) => {
            updateWorkerSpecialties(nextSpecialties);
            handleBlur({ target: { name: 'offeredServices' } });
          }}
          error={shouldShowError('offeredServices') ? fieldErrors.offeredServices : null}
        />
      </section>

      <section className="profile-section">
        <AvailabilitySelector
          value={availability}
          onChange={(nextAvailability) => {
            updateWorkerAvailability(nextAvailability);
            handleBlur({ target: { name: 'availability' } });
          }}
          error={shouldShowError('availability') ? fieldErrors.availability : null}
        />
      </section>

      <section className="profile-section">
        <div className="profile-section__header">
          <h3>Precio *</h3>
        </div>
        <div className="profile-grid profile-grid--two">
          <ProfileField
            required
            label="Precio por hora"
            name="hourlyRate"
            type="number"
            min="0.01"
            step="0.01"
            value={formData.hourlyRate}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="6500"
            error={shouldShowError('hourlyRate') ? fieldErrors.hourlyRate : null}
          />
        </div>
      </section>
      </>
    );
  }

  return (
    <section className="profile-section">
      <div className="profile-section__header">
        <h3>Datos personales y de contacto *</h3>
      </div>

      <div className="profile-grid profile-grid--two">
        <ProfileField
          required
          label="Nombre"
          name="firstName"
          value={formData.firstName}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Ana"
          error={shouldShowError('firstName') ? fieldErrors.firstName : null}
        />
        <ProfileField
          required
          label="Apellido"
          name="lastName"
          value={formData.lastName}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Pérez"
          error={shouldShowError('lastName') ? fieldErrors.lastName : null}
        />
      </div>

      <div className="profile-grid profile-grid--two">
        <ProfileField
          required
          label="Teléfono"
          name="contactPhone"
          value={formData.contactPhone}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="+54 261 555 1234"
          error={shouldShowError('contactPhone') ? fieldErrors.contactPhone : null}
        />
        <ProfileField
          required
          label="Código postal"
          name="postalCode"
          value={formData.postalCode}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="5500"
          error={shouldShowError('postalCode') ? fieldErrors.postalCode : null}
        />
      </div>

      <div className="profile-grid profile-grid--two">
        <ProfileField
          required
          label="Calle"
          name="streetName"
          value={formData.streetName}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="San Martín"
          error={shouldShowError('streetName') ? fieldErrors.streetName : null}
        />
        <ProfileField
          required
          label="Número"
          name="streetNumber"
          value={formData.streetNumber}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="1234"
          error={shouldShowError('streetNumber') ? fieldErrors.streetNumber : null}
        />
      </div>

      <div className="profile-grid profile-grid--two">
        <ProfileField label="Piso" name="floor" value={formData.floor} onChange={handleChange} placeholder="Opcional" />
        <ProfileField label="Departamento" name="apartment" value={formData.apartment} onChange={handleChange} placeholder="Opcional" />
      </div>

      <div className="profile-grid profile-grid--two">
        <ProfileField
          required
          label="Ciudad"
          name="city"
          value={formData.city}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Mendoza"
          error={shouldShowError('city') ? fieldErrors.city : null}
        />
        <ProfileField
          required
          label="Provincia"
          name="province"
          value={formData.province}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Mendoza"
          error={shouldShowError('province') ? fieldErrors.province : null}
        />
      </div>

      <ProfileField
        required
        className="field--full"
        label="Preferencias de contratación"
        name="hiringPreferences"
        value={formData.hiringPreferences}
        onChange={handleChange}
        onBlur={handleBlur}
        placeholder="Frecuencia, horarios, servicios prioritarios..."
        multiline
        rows={4}
        error={shouldShowError('hiringPreferences') ? fieldErrors.hiringPreferences : null}
      />
    </section>
  );
}

function SpecialtySelector({ selected, onChange, error }) {
  function toggleSpecialty(specialty) {
    const nextSelected = selected.includes(specialty)
      ? selected.filter((item) => item !== specialty)
      : [...selected, specialty];

    onChange(nextSelected);
  }

  return (
    <section className="profile-selector field--full">
      <div className="profile-selector__header">
        <h3>Especialidades *</h3>
      </div>

      <div className="profile-chip-grid" role="group" aria-label="Especialidades">
        {SPECIALTY_OPTIONS.map((specialty) => (
          <button
            key={specialty}
            className={`profile-chip${selected.includes(specialty) ? ' profile-chip--selected' : ''}`}
            type="button"
            onClick={() => toggleSpecialty(specialty)}
          >
            {specialty}
          </button>
        ))}
      </div>

      {selected.length > 0 ? (
        <div className="profile-selected-chips" aria-label="Especialidades seleccionadas">
          {selected.map((specialty) => (
            <span key={specialty}>{specialty}</span>
          ))}
        </div>
      ) : null}

      {error ? <p className="profile-field-error">{error}</p> : null}
    </section>
  );
}

function AvailabilitySelector({ value, onChange, error }) {
  function toggleDay(day) {
    const days = value.days.includes(day)
      ? value.days.filter((item) => item !== day)
      : [...value.days, day];

    onChange({ ...value, days });
  }

  function handleTimeChange(event) {
    const { name, value: nextValue } = event.target;
    onChange({ ...value, [name]: nextValue });
  }

  const summary = getAvailabilitySummary(value);

  return (
    <section className="profile-selector field--full">
      <div className="profile-selector__header">
        <h3>Disponibilidad *</h3>
      </div>

      <div className="profile-day-grid" role="group" aria-label="Días disponibles">
        {WEEK_DAYS.map((day) => (
          <button
            key={day}
            className={`profile-day${value.days.includes(day) ? ' profile-day--selected' : ''}`}
            type="button"
            onClick={() => toggleDay(day)}
          >
            <span aria-hidden="true">{value.days.includes(day) ? '✓' : ''}</span>
            {day}
          </button>
        ))}
      </div>

      <div className="profile-time-grid">
        <label>
          <span>Desde</span>
          <input name="from" type="time" value={value.from} onChange={handleTimeChange} />
        </label>
        <label>
          <span>Hasta</span>
          <input name="to" type="time" value={value.to} onChange={handleTimeChange} />
        </label>
      </div>

      {summary ? (
        <p className="profile-availability-summary">
          <FiCalendar />
          <span>{summary}</span>
        </p>
      ) : null}

      {error ? <p className="profile-field-error">{error}</p> : null}
    </section>
  );
}

function PhotoField({ photoUrl, uploading, onChange, error }) {
  const [dragActive, setDragActive] = useState(false);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      onChange({ target: { files: e.dataTransfer.files } });
    }
  };

  return (
    <div className="photo-dropzone-container">
      <label
        className={`photo-dropzone${dragActive ? ' photo-dropzone--drag' : ''}${photoUrl ? ' photo-dropzone--has-photo' : ''}${error ? ' photo-dropzone--error' : ''}`}
        onDragEnter={handleDrag}
        onDragOver={handleDrag}
        onDragLeave={handleDrag}
        onDrop={handleDrop}
      >
        <input
          type="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={onChange}
          disabled={uploading}
          hidden
        />

        {uploading ? (
          <div className="photo-dropzone__uploading">
            <span className="photo-dropzone__spinner" />
            <p>Subiendo foto...</p>
          </div>
        ) : (
          <div className="photo-dropzone__content">
            {photoUrl ? (
              <div className="photo-dropzone__avatar-container">
                <img
                  src={photoUrl.startsWith('http') ? photoUrl : `http://localhost:8080${photoUrl}`}
                  alt="Foto de perfil"
                  className="photo-dropzone__avatar"
                />
              </div>
            ) : (
              <div className="photo-dropzone__icon-container">
                <FiCamera className="photo-dropzone__icon" />
              </div>
            )}

            <div className="photo-dropzone__text">
              <span className="photo-dropzone__title">
                {photoUrl ? 'Cambiar foto' : 'Subí tu foto de perfil'}
              </span>
              <span className="photo-dropzone__subtitle">
                {photoUrl
                  ? 'Hacé clic o arrastrá una nueva imagen.'
                  : 'Arrastrá una imagen aquí o hacé clic para seleccionarla.'}
              </span>
              <span className="photo-dropzone__hint">
                JPG, PNG o WEBP · Máx. 5 MB
              </span>
            </div>
          </div>
        )}
      </label>
      {error ? <p className="profile-field-error">{error}</p> : null}
    </div>
  );
}

function ProfileField({ className = '', label, ...props }) {
  return <FormField className={className} label={label} icon={getFieldIcon(props.name, props.type)} {...props} />;
}

function getFieldIcon(name, type) {
  if (type === 'email') return <FiMail />;
  if (type === 'password') return <FiLock />;
  if (type === 'tel') return <FiPhone />;
  if (type === 'number') {
    if (name === 'yearsOfExperience') return <FiBarChart2 />;
    if (name === 'hourlyRate') return <FiDollarSign />;
    return <FiHash />;
  }

  switch (name) {
    case 'firstName':
      return <FiUser />;
    case 'lastName':
      return <FiUser />;
    case 'description':
      return <FiFileText />;
    case 'offeredServices':
      return <FiBriefcase />;
    case 'workArea':
      return <FiMapPin />;
    case 'availability':
      return <FiClock />;
    case 'contactPhone':
      return <FiPhone />;
    case 'streetName':
      return <FiMapPin />;
    case 'streetNumber':
      return <FiHash />;
    case 'floor':
      return <FiHome />;
    case 'apartment':
      return <FiHome />;
    case 'postalCode':
      return <FiHash />;
    case 'city':
      return <FiMapPin />;
    case 'province':
      return <FiMap />;
    case 'hiringPreferences':
      return <FiFileText />;
    default:
      return null;
  }
}

function parseSpecialties(value) {
  return String(value || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

function parseAvailability(value) {
  const availability = String(value || '');
  const match = availability.match(/^(.+) de (\d{2}:\d{2}) a (\d{2}:\d{2})$/);

  if (!match) {
    return {
      days: [],
      from: DEFAULT_FROM_TIME,
      to: DEFAULT_TO_TIME,
    };
  }

  return {
    days: match[1].split(',').map((item) => item.trim()).filter(Boolean),
    from: match[2],
    to: match[3],
  };
}

function serializeAvailability(value) {
  if (!value.days.length) {
    return '';
  }

  return `${value.days.join(', ')} de ${value.from || DEFAULT_FROM_TIME} a ${value.to || DEFAULT_TO_TIME}`;
}

function getAvailabilitySummary(value) {
  if (!value.days.length) {
    return '';
  }

  return `Horario seleccionado: ${value.days.join(', ')} de ${value.from || DEFAULT_FROM_TIME} a ${value.to || DEFAULT_TO_TIME}`;
}

function getFieldErrors(role, formData) {
  const errors = {};

  if (!String(formData.photoUrl || '').trim()) {
    errors.photoUrl = 'Agregá una foto de perfil.';
  }

  if (!String(formData.firstName || '').trim()) errors.firstName = 'Completá este campo.';
  if (!String(formData.lastName || '').trim()) errors.lastName = 'Completá este campo.';

  if (role === 'WORKER') {
    if (!String(formData.workArea || '').trim()) {
      errors.workArea = 'Completá este campo.';
    }
    if (!String(formData.yearsOfExperience || '').trim()) {
      errors.yearsOfExperience = 'Completá este campo.';
    }

    const desc = String(formData.description || '').trim();
    if (!desc) {
      errors.description = 'Completá este campo.';
    } else if (desc.length < 30) {
      errors.description = 'La descripción debe tener al menos 30 caracteres.';
    }

    if (!String(formData.offeredServices || '').trim()) {
      errors.offeredServices = 'Seleccioná al menos una opción.';
    }
    if (!String(formData.availability || '').trim()) {
      errors.availability = 'Seleccioná al menos una opción.';
    }

    const rate = String(formData.hourlyRate || '').trim();
    if (!rate) {
      errors.hourlyRate = 'Completá este campo.';
    } else if (Number(rate) <= 0) {
      errors.hourlyRate = 'El precio debe ser mayor que 0.';
    }

    if (!String(formData.contactPhone || '').trim()) errors.contactPhone = 'Completá este campo.';

    return errors;
  }

  if (!String(formData.contactPhone || '').trim()) errors.contactPhone = 'Completá este campo.';
  if (!String(formData.streetName || '').trim()) errors.streetName = 'Completá este campo.';
  if (!String(formData.streetNumber || '').trim()) errors.streetNumber = 'Completá este campo.';
  if (!String(formData.postalCode || '').trim()) errors.postalCode = 'Completá este campo.';
  if (!String(formData.city || '').trim()) errors.city = 'Completá este campo.';
  if (!String(formData.province || '').trim()) errors.province = 'Completá este campo.';
  if (!String(formData.hiringPreferences || '').trim()) errors.hiringPreferences = 'Completá este campo.';

  return errors;
}

function getProfileCompletion(role, formData) {
  const fields = role === 'WORKER'
    ? [formData.firstName, formData.lastName, formData.photoUrl, formData.contactPhone, formData.description, formData.yearsOfExperience, formData.offeredServices, formData.workArea, formData.availability, formData.hourlyRate]
    : [formData.firstName, formData.lastName, formData.photoUrl, formData.contactPhone, formData.streetName, formData.streetNumber, formData.postalCode, formData.city, formData.province, formData.hiringPreferences];

  const filled = fields.filter((value) => String(value ?? '').trim()).length;
  return Math.round((filled / fields.length) * 100);
}

function formatRole(role) {
  if (role === 'CLIENT') return 'Cliente';
  if (role === 'WORKER') return 'Trabajador';
  if (role === 'ADMIN') return 'Administrador';
  return role || '-';
}

function getInitialFormData(role) {
  if (role === 'WORKER') {
    return {
      firstName: '',
      lastName: '',
      photoUrl: '',
      description: '',
      yearsOfExperience: '',
      offeredServices: '',
      workArea: '',
      availability: '',
      hourlyRate: '',
      contactPhone: '',
    };
  }

  return {
    firstName: '',
    lastName: '',
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
      firstName: profile.firstName || '',
      lastName: profile.lastName || '',
      photoUrl: profile.photoUrl || '',
      description: profile.description || '',
      yearsOfExperience: profile.yearsOfExperience?.toString() || '',
      offeredServices: profile.offeredServices || '',
      workArea: profile.workArea || '',
      availability: profile.availability || '',
      hourlyRate: profile.hourlyRate?.toString() || '',
      contactPhone: profile.contactPhone || '',
    };
  }

  return {
    firstName: profile.firstName || '',
    lastName: profile.lastName || '',
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
      firstName: formData.firstName,
      lastName: formData.lastName,
      photoUrl: formData.photoUrl,
      contactPhone: formData.contactPhone,
      description: formData.description,
      yearsOfExperience: toNullableInteger(formData.yearsOfExperience),
      offeredServices: formData.offeredServices,
      workArea: formData.workArea,
      availability: formData.availability,
      hourlyRate: toNullableNumber(formData.hourlyRate),
    };
  }

  return {
    firstName: formData.firstName,
    lastName: formData.lastName,
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
      title: 'Completa tu perfil profesional',
    description: 'Completá tus datos profesionales para que los clientes conozcan cómo trabajás y cuándo estás disponible.',
      createTitle: 'Crear perfil de trabajador',
      editTitle: 'Editar perfil de trabajador',
    };
  }

  return {
    title: 'Completá tu perfil de cliente',
    description: 'Cargá tus datos de contacto y domicilio para agilizar futuras contrataciones.',
    createTitle: 'Crear perfil de cliente',
    editTitle: 'Editar perfil de cliente',
  };
}
