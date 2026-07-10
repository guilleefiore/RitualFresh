# Guía técnica de implementación

## Propósito

Este documento centraliza criterios técnicos para implementar RitualFresh de forma ordenada, consistente y mantenible.

## Principios generales

- Mantener código simple y legible.
- Evitar duplicación innecesaria.
- Separar responsabilidades.
- Priorizar trazabilidad con historias de usuario.
- No implementar funcionalidades fuera del alcance definido.
- Antes de programar un módulo, revisar contexto, reglas de negocio, historias y pantallas relacionadas.

## Backend

### Stack

- Java 21.
- Spring Boot 3.5.x.
- Maven.
- PostgreSQL.
- Hibernate ORM.
- Jakarta Persistence.
- Spring Security.
- SpringDoc OpenAPI.
- JUnit 5.

### Estructura

```txt
backend/src/main/java/com/ritualfresh/
├── admin/
├── auth/
├── notifications/
├── profiles/
└── shared/
    ├── exception/
    ├── config/
    └── security/
```

Cada módulo puede contener, como mínimo, `controller`, `service`, `repository`, `dto` y `model`.

### Seguridad actual

- La autenticación se resuelve con Spring Security.
- El sistema no utiliza JWT ni `HttpSession`.
- El frontend utiliza cookie `HttpOnly` y `fetch(..., { credentials: 'include' })` como mecanismo principal de sesión.
- La cookie de sesión expone `Secure`/`SameSite` por configuración (`RITUALFRESH_SESSION_COOKIE_SECURE`, `RITUALFRESH_SESSION_COOKIE_SAME_SITE`) y CSRF puede activarse con `RITUALFRESH_CSRF_ENABLED`.
- El backend mantiene compatibilidad técnica con `Authorization: Bearer <sessionToken>` para pruebas y debugging.
- El `sessionToken` se persiste en `user_sessions` y se valida mediante un filtro de seguridad.
- Las respuestas `401` y `403` se devuelven en JSON consistente con el resto de la API.
- El sistema integra login social con Google mediante Spring Security OAuth2 Client:
  - el flujo se inicia desde el frontend redirigiendo a `/oauth2/authorization/google`;
  - tras la autenticación externa, `GoogleOAuth2SuccessHandler` extrae el perfil (email, nombre, apellido),
    crea o autentica el usuario local y establece la cookie `HttpOnly` de sesión;
  - los usuarios nuevos (nunca antes vistos) son redirigidos a `/choose-role` para seleccionar CLIENT o WORKER;
  - la selección de rol se bloquea cuando ya existe un perfil asociado;
  - los usuarios existentes son redirigidos al home según su rol (`/client/home`, `/worker/home`, `/admin/home`);
  - los endpoints `/oauth2/**` y `/login/oauth2/**` son públicos (permitAll);
  - las credenciales se configuran mediante `RITUALFRESH_GOOGLE_CLIENT_ID` y `RITUALFRESH_GOOGLE_CLIENT_SECRET`.
- El sistema soporta subida de archivos para fotos de perfil:
  - `StorageService` guarda archivos en `/app/uploads` (volumen Docker `ritualfresh_uploads`);
  - `FileUploadController` expone `POST /api/upload` (multipart, valida tipo imagen);
  - `WebConfig` sirve `/uploads/**` como recursos estáticos;
  - el frontend usa un componente `PhotoField` que permite seleccionar archivo y envía multipart.

Estructura transversal actual:

```txt
shared/
├── config/
│   ├── CorsConfig.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── controller/
│   └── FileUploadController.java
├── exception/
├── security/
│   ├── AuthenticatedUserPrincipal.java
│   ├── GoogleOAuth2SuccessHandler.java
│   ├── RestAccessDeniedHandler.java
│   ├── RestAuthenticationEntryPoint.java
│   └── SessionAuthenticationFilter.java
└── service/
    └── StorageService.java
```

## Reglas de implementación backend

- No exponer entidades JPA directamente en controllers.
- Usar DTOs para request y response.
- Validar entradas con Jakarta Validation.
- Centralizar errores con manejo global de excepciones.
- Usar enums para estados de solicitud, contratación, pago, notificación y reclamo.
- Mantener métodos pequeños y con responsabilidad clara.
- Evitar lógica de negocio dentro de controllers.
- Para endpoints protegidos, preferir autorización declarativa con `@PreAuthorize`.
- No mezclar lógica de pagos con lógica de contratación fuera de servicios coordinadores.
- Los webhooks deben ser idempotentes.
- Las operaciones críticas deben registrar eventos o logs.

### Reglas actuales de seguridad

- Endpoints públicos:
  - `POST /api/users/register`
  - `POST /api/users/login`
  - `GET /api/users/validation`
  - `POST /api/users/validation/resend`
  - `POST /api/users/password-reset`
  - `POST /api/users/password-reset/confirm`
  - `GET /oauth2/authorization/google` (inicia flujo OAuth2)
  - `GET /login/oauth2/code/google` (callback OAuth2)
  - `/uploads/**` (archivos estáticos subidos, ej. fotos de perfil)
- Endpoints administrativos:
  - `/api/admin/**` requiere `ROLE_ADMIN`
- Endpoints de perfil cliente:
  - `/api/profiles/clientes/**` requiere `ROLE_CLIENT`
- Endpoints de perfil trabajador:
  - `/api/profiles/trabajadores/**` requiere `ROLE_WORKER`
- Endpoints autenticados generales:
  - `POST /api/users/logout`
  - `DELETE /api/users/me`
  - `GET /api/profiles/me`

## Frontend

### Stack

- React 19.
- Bootstrap 5.3.x.
- Google Maps API.
- Node.js 22 LTS.

### Estructura sugerida

```txt
frontend/src/
├── app/
│   ├── App.jsx
│   ├── providers.jsx
│   └── router.jsx
├── modules/
│   ├── auth/
│   ├── admin/
│   └── profiles/
├── shared/
│   ├── guards/
│   └── services/
├── styles/
│   ├── globals.css
│   └── variables.css
└── main.jsx
```

### Estado actual del frontend

- `app/router.jsx` centraliza las rutas públicas y protegidas.
- `modules/auth` contiene login, registro, validación, recuperación y pantallas home mínimas por rol.
- `modules/admin` concentra el dashboard administrativo, el listado embebido de usuarios, el detalle y el cambio de estado.
- `modules/profiles` ya implementa un flujo vertical mínimo para `M02`.

### Flujo actual del módulo `profiles`

- Ruta protegida: `/profiles`.
- Roles habilitados: `CLIENT` y `WORKER`.
- Comportamiento:
  - al ingresar, la pantalla consulta `GET /api/profiles/me`;
  - si el backend responde que el usuario aún no tiene perfil, se muestra formulario de alta;
  - si el perfil ya existe, se cargan los datos actuales y la pantalla pasa a modo edición.
- Servicios frontend involucrados:
  - `GET /api/profiles/me`
  - `POST /api/profiles/clientes`
  - `PUT /api/profiles/clientes/me`
  - `POST /api/profiles/trabajadores`
  - `PUT /api/profiles/trabajadores/me`
- Entrada de navegación actual:
  - `CLIENT`: desde `/client/home` mediante el botón `Ir a mi perfil`;
  - `WORKER`: desde `/worker/home` mediante el botón `Ir a mi perfil`.

## Reglas de implementación frontend

- Separar páginas de componentes reutilizables.
- Centralizar llamadas HTTP en services.
- Mantener validaciones visuales coherentes con backend.
- Implementar estados de carga, error, vacío y éxito.
- Respetar guía visual.
- No duplicar lógica de filtros o formateo.
- Mantener nombres de rutas y componentes consistentes con módulos.
- Si un módulo depende del rol autenticado, resolver la variante visual dentro de la misma pantalla sólo cuando comparta el mismo caso de uso, como ocurre actualmente con `/profiles`.
- No permitir cambios de rol una vez creado el perfil; mantener `getMyProfile` alineado con el rol autenticado.

## Convenciones de nombres

- Paquetes Java: minúsculas.
- Clases Java: PascalCase.
- Métodos y atributos Java: camelCase.
- Componentes React: PascalCase.
- Hooks React: useNombre.
- Archivos de documentación: MAYÚSCULAS_CON_GUIONES o nombres descriptivos.

## Flujo recomendado para implementar una historia

1. Leer la historia de usuario.
2. Revisar reglas de negocio asociadas.
3. Revisar pantalla o reporte relacionado.
4. Identificar entidades afectadas.
5. Definir endpoints necesarios.
6. Implementar backend.
7. Implementar frontend.
8. Agregar pruebas.
9. Probar manualmente el flujo completo.
10. Documentar evidencia si corresponde.

## Criterios de calidad

- Código compilable.
- Tests ejecutables.
- Endpoints documentados.
- Errores controlados.
- Validaciones claras.
- Seguridad centralizada aplicada.
- Sin cambios innecesarios en módulos no relacionados.
