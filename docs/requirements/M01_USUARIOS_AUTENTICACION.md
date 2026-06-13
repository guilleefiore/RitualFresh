# M01 - Gestión de Usuarios y Autenticación

## Objetivo

Definir la base funcional para registro, validación de cuenta, inicio de sesión y recuperación de contraseña.

## Historias incluidas

- `US01-M01-RF01`: Registro de usuario.
- `US04-M01-RF04`: Validación de cuenta.
- `US02-M01-RF02`: Inicio de sesión.
- `US03-M01-RF03`: Recuperación de contraseña.

## Reglas clave

- El correo electrónico debe ser único.
- El registro admite `CLIENTE` o `TRABAJADOR`.
- La cuenta queda pendiente hasta validar el enlace/token.
- El inicio de sesión exige cuenta activa.
- La recuperación de contraseña requiere un correo existente.

## Criterios de aceptación iniciales

- El backend expone endpoints para registrar, validar e iniciar sesión.
- El frontend permite completar el flujo con formularios separados.
- Los errores de validación se muestran sin perder lo ingresado.
- El modelo de datos de usuarios y sesiones sigue sujeto a revisión antes de consolidarse.
