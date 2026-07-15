# Módulo Profiles

El módulo `profiles` implementa la gestión del perfil propio para usuarios `CLIENT` y `WORKER` dentro de `M02`.

## Objetivo

Resolver un flujo vertical mínimo que permita:

- consultar el perfil autenticado;
- crear el perfil si todavía no existe;
- editar el perfil si ya fue creado.

## Estructura actual

```txt
profiles/
├── pages/
│   └── ProfilesPage.jsx
├── services/
│   └── profileService.js
└── styles/
    └── profile.css
```

## Ruta frontend

- `/profiles`

La ruta está protegida y sólo admite usuarios con rol `CLIENT` o `WORKER`.

## Layout

Una sola card centralizada (`profile-form-card`) con `max-width: 960px`, fondo blanco y `border-radius: 28px`. Centrada en la página con `display: flex` y padding vertical de 48px.

### Header de la card

- Título: "Completa tu perfil profesional" (WORKER) o "Completá tu perfil de cliente" (CLIENT)
- Subtítulo con la descripción del paso
- Barra de progreso que muestra el % de completitud, animada al cambiar

### Secciones del formulario

El encabezado, el progreso y la carga de foto son comunes a ambos roles. Debajo, los datos se presentan en paneles con una grilla responsive: 12 columnas en escritorio, dos columnas en tablet y una columna en móvil.

**CLIENT**

1. **Foto de perfil** (obligatoria) — carga con drag and drop, preview y validación.
2. **Información personal** — resumen de correo y rol de solo lectura; nombre, apellido y teléfono editables.
3. **Domicilio** — calle, número, piso, departamento, ciudad, provincia y código postal con anchos acordes a cada dato.
4. **Preferencias de contratación** — descripción de frecuencia, horarios y servicios buscados.
5. **Acciones** — Cancelar y Crear perfil/Guardar cambios.

**WORKER**

1. **Foto de perfil** (obligatoria) — carga con drag and drop, preview y validación.
2. **Información personal** — resumen de correo y rol de solo lectura; nombre, apellido y teléfono editables.
3. **Presentación profesional** — zona de trabajo, experiencia y descripción personal.
4. **Servicios** — selector de especialidades.
5. **Disponibilidad** — selector de días y rango horario.
6. **Tarifa** — precio por hora orientativo.
7. **Acciones** — Cancelar y Guardar perfil.

## Comportamiento

### Carga inicial

Al ingresar a la pantalla:

1. se consulta `GET /api/profiles/me`;
2. si el usuario no posee perfil, se habilita el formulario de alta;
3. si el perfil ya existe, se precargan los datos y la pantalla pasa a modo edición.

### Variante cliente

Campos principales:

- foto (obligatoria);
- teléfono de contacto;
- calle y número;
- piso y departamento opcionales;
- código postal;
- ciudad;
- provincia;
- frecuencia del servicio;
- momentos preferidos;
- servicios de interés, con detalle obligatorio al seleccionar `Otro`;
- observaciones adicionales opcionales.

Endpoints utilizados:

- `GET /api/profiles/me`
- `POST /api/profiles/clientes`
- `PUT /api/profiles/clientes/me`

Preferencias estructuradas enviadas por el frontend:

- `serviceFrequency`: `ONE_TIME`, `WEEKLY`, `BIWEEKLY`, `MONTHLY` o `AS_NEEDED`;
- `preferredTimeSlots`: una o más opciones entre `MORNING`, `AFTERNOON` y `FLEXIBLE`;
- `serviceInterests`: uno o más servicios del catálogo;
- `otherServiceInterest`: obligatorio cuando `serviceInterests` contiene `OTHER`;
- `additionalNotes`: observaciones opcionales de hasta 500 caracteres.

### Variante trabajador

Campos principales:

- foto (obligatoria);
- descripción profesional;
- años de experiencia;
- servicios ofrecidos;
- zona de trabajo;
- disponibilidad;
- precio por hora orientativo.

Endpoints utilizados:

- `GET /api/profiles/me`
- `POST /api/profiles/trabajadores`
- `PUT /api/profiles/trabajadores/me`

## Validación

- Todos los campos marcados como obligatorios se validan al enviar el formulario.
- La foto de perfil es obligatoria y muestra error si no se seleccionó.
- El progreso se calcula sobre todos los campos obligatorios incluida la foto.

## Navegación actual

- `CLIENT`: acceso desde `/client/home` con el botón `Ir a mi perfil`.
- `WORKER`: acceso desde `/worker/home` con el botón `Ir a mi perfil`.

## Criterios mínimos de prueba manual

1. Iniciar sesión con usuario `CLIENT` o `WORKER`.
2. Entrar a `/profiles`.
3. Confirmar alta inicial cuando no existe perfil.
4. Intentar guardar sin foto — debe mostrar error de validación.
5. Guardar datos válidos con foto incluida.
6. Reingresar y confirmar precarga en modo edición.
7. Modificar un dato y validar persistencia.

## Estado de sesión

La sesión frontend se rehidrata desde la cookie mediante `GET /api/users/me` al iniciar la app. Si la cookie expiró o fue invalidada, el usuario vuelve a `/login`.
