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

### Secciones del formulario (en orden)

1. **Foto de perfil** (obligatoria) — preview circular de 140px, con validación y mensaje de error
2. **Datos personales** — chips informativos con nombre, email y rol (solo lectura, desde autenticación)
3. **Datos profesionales** (WORKER) / **Datos de contacto y domicilio** (CLIENT)
4. **Especialidades** (WORKER) — selector de chips
5. **Disponibilidad** (WORKER) — selector de días + horarios
6. **Precio** (WORKER) — precio por hora
7. **Footer** — Cancelar (link al home) + Guardar

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
- preferencias de contratación.

Endpoints utilizados:

- `GET /api/profiles/me`
- `POST /api/profiles/clientes`
- `PUT /api/profiles/clientes/me`

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

## Limitación actual conocida

La sesión frontend todavía no se rehidrata automáticamente desde la cookie al refrescar la página completa. Para pruebas manuales, puede ser necesario volver a iniciar sesión luego de un reload duro del navegador.
