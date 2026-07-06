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
- Login + acceso autenticado reutilizando cookie `HttpOnly` o `Authorization: Bearer <sessionToken>` en modo compatibilidad.
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

#### Prueba manual con Postman o curl

Para la validación local del backend y de la persistencia en PostgreSQL se recomienda un flujo manual simple con Mailtrap como bandeja de prueba de correos.

##### Prerrequisitos

- Base de datos levantada con `docker compose up -d postgres` o stack completo con `docker compose up --build`.
- Archivo `.env` completo con credenciales válidas de Mailtrap.
- Sandbox de Mailtrap accesible para revisar correos enviados.

##### Flujo manual validado para `M01`

1. `POST /api/users/register`
   - Request ejemplo:
     ```json
      {
        "firstName": "Guillermina",
        "lastName": "Fiore",
        "email": "guillermina.test@example.com",
        "password": "clave123",
        "confirmPassword": "clave123",
        "role": "CLIENT"
      }
     ```
   - Verifica registro de usuario con cuenta en estado pendiente.
   - Verifica que la API ya no exponga el token de validación en la respuesta.
2. Mailtrap: correo `RitualFresh - Validacion de cuenta`
   - Verifica recepción del correo en el sandbox.
   - Verifica que el cuerpo incluya el link `GET /api/users/validation?token=...`.
3. `POST /api/users/validation/resend`
   - Request ejemplo:
     ```json
     {
       "email": "guillermina.test@example.com"
     }
     ```
   - Verifica que el backend reemita un nuevo enlace de validación para cuentas todavía pendientes.
   - Verifica que el token anterior deje de ser válido cuando se reenvía uno nuevo.
4. Mailtrap: correo `RitualFresh - Validacion de cuenta`
   - Verifica recepción del correo reenviado en el sandbox.
   - Verifica que el cuerpo incluya el nuevo link `GET /api/users/validation?token=...`.
5. `GET /api/users/validation?token=...`
   - Verifica activación de la cuenta.
   - Resultado esperado: `accountStatus = ACTIVE`.
6. `POST /api/users/login`
   - Request ejemplo:
     ```json
     {
       "email": "guillermina.test@example.com",
       "password": "clave123"
     }
     ```
   - Verifica autenticación, generación de la sesión persistida y datos del usuario autenticado.
   - Verifica que la respuesta incluya `Set-Cookie` con la cookie `HttpOnly` de sesión.
7. `POST /api/users/password-reset`
   - Request ejemplo:
     ```json
     {
       "email": "guillermina.test@example.com"
     }
     ```
   - Verifica respuesta exitosa con `expiresAt`.
   - Verifica que la API ya no exponga el token de recuperación en la respuesta.
8. Mailtrap: correo `RitualFresh - Recuperacion de contrasena`
   - Verifica recepción del correo en el sandbox.
   - Verifica que el cuerpo incluya link y token de recuperación.
9. `POST /api/users/password-reset/confirm`
   - Request ejemplo:
     ```json
     {
       "resetToken": "<token tomado del correo>",
       "password": "nuevaClave123",
       "confirmPassword": "nuevaClave123"
     }
     ```
   - Verifica cambio efectivo de contraseña.
10. `POST /api/users/login` con la nueva contraseña
   - Verifica autenticación exitosa con `nuevaClave123`.
11. `POST /api/users/login` con la contraseña vieja
   - Verifica rechazo de credenciales viejas.
12. `DELETE /api/users/me`
    - Verifica autoeliminación lógica de la cuenta autenticada.
    - Verifica que la respuesta expire la cookie de sesión.
13. `POST /api/users/login` con la contraseña usada antes de eliminar la cuenta
    - Verifica rechazo porque la cuenta quedó en estado `DELETED`.
14. `GET /api/profiles/me`
    - Verifica acceso autenticado usando la cookie de sesión.
15. `POST /api/profiles/clientes` o `POST /api/profiles/trabajadores`
    - Verifica persistencia real del perfil asociado al usuario autenticado.
16. `POST /api/users/logout`
    - Verifica cierre de sesión y rechazo posterior del mismo token.
17. `GET /api/admin/users`
    - Verifica acceso sólo con un usuario `ADMIN` autenticado en una sesión separada.

##### Flujo manual para login con Google (OAuth 2.0)

1. Asegurarse de que el archivo `.env` tenga configuradas las credenciales
   `RITUALFRESH_GOOGLE_CLIENT_ID` y `RITUALFRESH_GOOGLE_CLIENT_SECRET`.
2. Desde el frontend en `http://localhost:5173/login`, hacer clic en "Continuar con Google".
3. Verificar redirección a la pantalla de autenticación de Google.
4. Iniciar sesión con una cuenta de Google de prueba.
5. Verificar redirección de vuelta a la aplicación y creación/autenticación exitosa del usuario local.
6. Verificar que se establezca la cookie `HttpOnly` de sesión.
7. Verificar que el usuario sea redirigido al home correspondiente a su rol.
8. Repetir con una cuenta de Google cuyo correo ya exista en la base de datos (cuenta pendiente o activa)
   y verificar que se reutilice correctamente.
9. Repetir con una cuenta de Google cuyo correo ya exista pero con estado `DELETED` o `SUSPENDED`
   y verificar que el login sea rechazado.
10. Si las credenciales OAuth no son válidas, verificar que se redirija a `/login?oauth=error` con un mensaje
    de error visible para el usuario.

##### Flujo para selección de rol post-Google OAuth (usuario nuevo)

1. Iniciar sesión con Google usando una cuenta que nunca haya iniciado sesión.
2. Verificar redirección a `/choose-role` en lugar del home por rol.
3. Hacer clic en "Soy Cliente".
4. Verificar redirección a `/client/home`.
5. Confirmar que el usuario quede con rol `CLIENT`.
6. Repetir con otra cuenta nueva de Google.
7. Hacer clic en "Soy Trabajador".
8. Verificar redirección a `/worker/home`.
9. Confirmar que el usuario quede con rol `WORKER`.

##### Flujo manual para subida de foto de perfil

1. Ingresar a la ruta protegida `/profiles`.
2. Hacer clic en el área de foto o en el input de archivo.
3. Seleccionar un archivo de imagen (jpg, png, webp) desde el sistema de archivos.
4. Verificar que se muestre una vista previa de la imagen seleccionada.
5. Hacer clic en "Guardar" y confirmar que la foto se persista.
6. Refrescar la página y verificar que la foto se cargue desde el backend (`GET /uploads/<filename>`).
7. Repetir seleccionando un archivo no imagen (pdf, txt) y verificar que se muestre un mensaje de error.

##### Prueba manual frontend del módulo `profiles`

Flujo recomendado para validar la pantalla implementada en React:

1. Levantar frontend con `cd frontend && npm run dev`.
2. Abrir `http://localhost:5173`.
3. Registrar y autenticar un usuario `CLIENT` o `WORKER`.
4. Ingresar al home del rol y seleccionar `Ir a mi perfil`.
5. Verificar que la ruta sea `/profiles`.
6. Si es la primera vez:
   - verificar que el formulario aparezca vacío;
   - completar datos válidos;
   - guardar y confirmar mensaje de éxito.
7. Reingresar a `/profiles`.
   - verificar que los datos previos se carguen automáticamente;
   - verificar que el botón quede en modo edición;
   - modificar al menos un campo y guardar nuevamente.
8. Confirmar que el resumen inferior refleje el último estado persistido.
9. Validar el control por rol:
   - `CLIENT` debe ver campos de contacto, domicilio y preferencias;
   - `WORKER` debe ver campos de descripción profesional, experiencia, servicios, disponibilidad y precio.
10. Validar error esperado:
   - intentar guardar con campos obligatorios vacíos;
   - verificar que el frontend o el backend informen el error sin romper la pantalla.

Observación actual conocida:

- Si se refresca la página completa, el frontend todavía no rehidrata la sesión desde la cookie. En ese caso puede requerirse iniciar sesión nuevamente para continuar probando.

##### Prueba manual frontend del módulo `admin`

1. Iniciar sesión con un usuario `ADMIN`.
2. Abrir `/admin/home`.
3. Verificar la carga de métricas y de la tabla de usuarios.
4. Ingresar al detalle de un usuario desde `Ver detalles`.
5. Confirmar navegación a `/admin/users/:userId`.
6. Cambiar el estado de cuenta y validar persistencia de la actualización.

Configuración mínima sugerida:

- Base URL local: `http://localhost:8080`.
- Header común: `Content-Type: application/json`.
- Para frontend o pruebas cercanas al comportamiento final, reutilizar la cookie de sesión devuelta por login.
- Para debugging manual o tests técnicos, el backend aún acepta `Authorization: Bearer <sessionToken>` como compatibilidad.

La confirmación de persistencia se completa revisando las tablas creadas por Hibernate en PostgreSQL, por ejemplo `users`, `user_sessions`, `client_profiles` y `worker_profiles`.

Si se documenta evidencia manual, conviene registrar:

- request utilizado;
- asunto del correo recibido en Mailtrap;
- enlace o token usado desde el correo;
- respuesta esperada;
- respuesta observada;
- estado final de la prueba.

### Pruebas de seguridad

Casos mínimos:

- Acceso a endpoint privado sin autenticación.
- Acceso con token inválido.
- Acceso con token expirado.
- Acceso con sesión cerrada.
- Acceso con rol incorrecto.
- `CLIENT` no accede a endpoints de `WORKER`.
- `WORKER` no accede a endpoints de `CLIENT`.
- `CLIENT` y `WORKER` no acceden a `/api/admin/**`.
- `ADMIN` sí accede a endpoints administrativos.
- Intento de modificar información de otro usuario.
- Protección de datos financieros sensibles.

Cobertura actual implementada en backend:

- `SecurityIntegrationTest`
  - acceso privado sin token
  - token inválido
  - token expirado
  - token cerrado
  - `CLIENT` contra endpoints `WORKER`
  - `WORKER` contra endpoints `CLIENT`
  - `CLIENT` y `WORKER` contra `/api/admin/**`
  - `ADMIN` contra `/api/admin/**`
- `UserServiceTest`
  - registro, validación, login y recuperación de contraseña
  - `loginWithGoogle` para cuentas nuevas y pendientes
- `ProfileServiceTest`
  - reglas de negocio y restricciones funcionales de perfiles
- `AdminServiceTest`
  - métricas, cambio de estado y restricciones administrativas

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
