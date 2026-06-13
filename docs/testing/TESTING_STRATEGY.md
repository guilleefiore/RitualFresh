# Estrategia de pruebas

## Objetivo

Validar que RitualFresh cumpla sus requerimientos funcionales, reglas de negocio, seguridad básica e integración entre módulos.

## Tipos de pruebas

### Pruebas unitarias

Aplican a servicios, reglas de negocio, cálculos y validaciones.

Casos mínimos:

- Registro con correo existente.
- Validación de campos obligatorios.
- Cálculo de ranking.
- Cancelación con 48 horas o más.
- Cancelación con menos de 48 horas.
- Cálculo de reembolso parcial.
- Suma de strikes.
- Suspensión por 3 strikes.
- Marcado de notificaciones como leídas.
- Validación de calificación única por contratación.

### Pruebas de integración

Validan interacción entre componentes.

Casos mínimos:

- Registro + login.
- Perfil + búsqueda.
- Búsqueda + solicitud.
- Solicitud + aceptación + pago.
- Webhook de pago aprobado + confirmación de contratación.
- Contratación finalizada + calificación.
- Cancelación + reembolso + notificación.
- Chat + persistencia de mensajes.

### Pruebas de API

Validan endpoints REST.

Herramientas sugeridas:

- Postman.
- REST Client.
- Swagger/OpenAPI.

#### Prueba manual con Postman

Para la validación local del backend y de la persistencia en PostgreSQL se recomienda un flujo manual simple en Postman:

1. `POST /api/users/register`
   - Verifica registro de usuario y generación del `accountValidationToken`.
2. `GET /api/users/validation?token=...`
   - Verifica activación de la cuenta.
3. `POST /api/users/login`
   - Verifica autenticación y generación del `sessionToken`.
4. `GET /api/profiles/me`
   - Verifica acceso autenticado con `Authorization: Bearer <sessionToken>`.
5. `POST /api/profiles/clientes` o `POST /api/profiles/trabajadores`
   - Verifica persistencia real del perfil asociado al usuario autenticado.

Configuración mínima sugerida:

- Base URL local: `http://localhost:8080`.
- Header común: `Content-Type: application/json`.
- Header para endpoints protegidos: `Authorization: Bearer <sessionToken>`.

La confirmación de persistencia se completa revisando las tablas creadas por Hibernate en PostgreSQL, por ejemplo `users`, `user_sessions`, `client_profiles` y `worker_profiles`.

Si se documenta evidencia manual, conviene registrar:

- request utilizado;
- token obtenido;
- respuesta esperada;
- respuesta observada;
- estado final de la prueba.

### Pruebas de seguridad

Casos mínimos:

- Acceso a endpoint privado sin autenticación.
- Acceso con rol incorrecto.
- Intento de modificar información de otro usuario.
- Validación de token inválido o expirado.
- Protección de datos financieros sensibles.

### Pruebas frontend

Casos mínimos:

- Formularios con errores.
- Formularios válidos.
- Estados vacíos.
- Estados de carga.
- Modales de confirmación.
- Badges de notificaciones.
- Filtros de búsqueda.

## Evidencia de pruebas

Cada prueba relevante debe documentarse con:

- Código o caso de prueba.
- Resultado esperado.
- Resultado obtenido.
- Captura o evidencia si corresponde.
- Estado: aprobado, observado o pendiente.

## Criterio de aceptación general

Una funcionalidad se considera lista cuando:

- Cumple sus criterios de aceptación.
- Tiene validaciones básicas.
- Maneja errores esperados.
- Respeta permisos por rol.
- Tiene pruebas asociadas.
- Se encuentra documentada si corresponde.
