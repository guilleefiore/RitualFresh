# M01 - Gestión de Usuarios y Autenticación

## Objetivo

Definir la base funcional para registro, validación de cuenta, inicio de sesión y recuperación de contraseña.

## Estado actual de implementación backend

La base de `M01` ya se encuentra implementada en el backend con:

- registro público para `CLIENT` y `WORKER`
- validación de cuenta por token
- reenvío público del enlace de validación
- inicio de sesión con generación de `sessionToken`
- inicio de sesión con Google mediante OAuth 2.0 (Spring Security OAuth2 Client)
- persistencia de sesiones opacas en `user_sessions`
- cookie `HttpOnly` como transporte principal de sesión
- cierre de sesión
- autoeliminación lógica de la propia cuenta autenticada
- recuperación y confirmación de cambio de contraseña
- autenticación y autorización centralizadas con Spring Security

## Historias incluidas

- `US01-M01-RF01`: Registro de usuario.
- `US04-M01-RF04`: Validación de cuenta.
- `US02-M01-RF02`: Inicio de sesión.
- `US03-M01-RF03`: Recuperación de contraseña.

## Reglas clave

- El correo electrónico debe ser único entre cuentas vigentes.
- El registro admite `CLIENT` o `WORKER`.
- El registro público solicita correo electrónico, contraseña, repetición de contraseña y rol; los datos de contacto detallados y de perfil se completan en el módulo de perfiles.
- La cuenta queda pendiente hasta validar el enlace/token.
- El inicio de sesión exige cuenta activa.
- La recuperación de contraseña requiere un correo existente.
- El registro público no permite crear usuarios `ADMIN`.
- Una cuenta `DELETED` puede volver a registrarse con el mismo correo: el backend reutiliza el registro existente y lo deja en `PENDING_VALIDATION`.
- Una cuenta `DELETED` que vuelve por Google OAuth se reactiva como cuenta activa y genera sesión.
- Una cuenta `SUSPENDED` no puede iniciar sesión ni re-registrarse con el mismo correo mientras conserve ese estado.
- El rol no puede cambiarse si el usuario ya posee un perfil creado; la selección del mismo rol es idempotente para el onboarding post-Google.
- Los endpoints protegidos utilizan cookie `HttpOnly` como mecanismo principal y aceptan `Authorization: Bearer <sessionToken>` como compatibilidad técnica.

## Criterios de aceptación iniciales

- El backend expone endpoints para registrar, validar, iniciar sesión, cerrar sesión y recuperar contraseña.
- El backend expone además reenvío de validación y autoeliminación lógica de la cuenta autenticada.
- El backend mantiene sesiones opacas persistidas y valida acceso con Spring Security.
- Los errores de autenticación y autorización se devuelven en formato JSON consistente.
- El frontend consume el flujo principalmente con cookie `HttpOnly` y `credentials: 'include'`.

## Endpoints actuales del módulo

- `POST /api/users/register`
- `GET /api/users/validation`
- `POST /api/users/validation/resend`
- `POST /api/users/login`
- `POST /api/users/logout`
- `DELETE /api/users/me`
- `PUT /api/users/me/role` (cambia el rol del usuario autenticado, usado post-Google OAuth)
- `POST /api/users/password-reset`
- `POST /api/users/password-reset/confirm`
- `GET /oauth2/authorization/google` (inicia flujo Google OAuth)
- `GET /login/oauth2/code/google` (callback de Google)

## Cambios recientes

- Registro simplificado: el formulario público de registro pide solo `email`, `password`, `confirmPassword` y `role`; los datos personales quedan para el módulo de perfiles.
- Re-registro de cuentas eliminadas: el estado `DELETED` no bloquea definitivamente el correo; el sistema reactiva la cuenta existente según el flujo usado.
- Login con Google para nuevos usuarios: tras la autenticación externa, el usuario es redirigido a `/choose-role` para seleccionar `CLIENT` o `WORKER` antes de acceder al home.
- Validación de cuenta: el correo de activación apunta al frontend en `/validation?token=...` y la pantalla de reenvío vive en `/validation/resend`.
- El endpoint `PUT /api/users/me/role` permite actualizar el rol del usuario autenticado sólo si todavía no creó perfil (body: `{"role": "WORKER"}` o `{"role": "CLIENT"}`).

## Criterios de aceptación iniciales

- El backend permite iniciar sesión con cuenta de Google mediante OAuth 2.0.
- Los usuarios nuevos que inician sesión con Google pueden seleccionar su rol antes de acceder al sistema.
