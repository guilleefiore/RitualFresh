# M01 - Gestión de Usuarios y Autenticación

## Objetivo

Definir la base funcional para registro, validación de cuenta, inicio de sesión y recuperación de contraseña.

## Estado actual de implementación backend

La base de `M01` ya se encuentra implementada en el backend con:

- registro público para `CLIENT` y `WORKER`
- validación de cuenta por token
- inicio de sesión con generación de `sessionToken`
- persistencia de sesiones opacas en `user_sessions`
- cierre de sesión
- recuperación y confirmación de cambio de contraseña
- autenticación y autorización centralizadas con Spring Security

## Historias incluidas

- `US01-M01-RF01`: Registro de usuario.
- `US04-M01-RF04`: Validación de cuenta.
- `US02-M01-RF02`: Inicio de sesión.
- `US03-M01-RF03`: Recuperación de contraseña.

## Reglas clave

- El correo electrónico debe ser único.
- El registro admite `CLIENT` o `WORKER`.
- La cuenta queda pendiente hasta validar el enlace/token.
- El inicio de sesión exige cuenta activa.
- La recuperación de contraseña requiere un correo existente.
- El registro público no permite crear usuarios `ADMIN`.
- Los endpoints protegidos requieren `Authorization: Bearer <sessionToken>`.

## Criterios de aceptación iniciales

- El backend expone endpoints para registrar, validar, iniciar sesión, cerrar sesión y recuperar contraseña.
- El backend mantiene sesiones opacas persistidas y valida acceso con Spring Security.
- Los errores de autenticación y autorización se devuelven en formato JSON consistente.
- El frontend puede consumir el flujo enviando `Authorization: Bearer <sessionToken>` en endpoints protegidos.

## Endpoints actuales del módulo

- `POST /api/users/register`
- `GET /api/users/validation`
- `POST /api/users/login`
- `POST /api/users/logout`
- `POST /api/users/password-reset`
- `POST /api/users/password-reset/confirm`
