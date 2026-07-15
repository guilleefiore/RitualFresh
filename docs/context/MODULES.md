# Módulos funcionales

La cobertura de clases implementadas y propuestas para todos los módulos se encuentra en la sección de diagramas del [README](../../README.md). Los diagramas marcados como diseño propuesto sirven como base de discusión y no representan código ya disponible.

## M01 - Gestión de Usuarios y Autenticación

Gestiona registro, validación de cuenta, inicio de sesión (incluyendo login social con Google), recuperación de contraseña, roles y acceso al sistema.

Estado actual de implementación backend:

- registro público para `CLIENT` y `WORKER`
- validación de cuenta mediante token
- reenvío público del enlace de validación
- login con sesión opaca persistida y cookie `HttpOnly` como transporte principal
- login con Google (OAuth 2.0)
- logout
- autoeliminación lógica de la cuenta autenticada
- recuperación de contraseña
- Spring Security con sesión opaca persistida en `user_sessions`

Historias principales:

- US01-M01-RF01: Registro de usuario.
- US02-M01-RF02: Inicio de sesión.
- US03-M01-RF03: Recuperación de contraseña.
- US04-M01-RF04: Validación de cuenta.

## M02 - Gestión de Perfiles

Permite a clientes y trabajadores completar y administrar su información personal y profesional.

Estado actual de implementación backend:

- creación de perfil cliente
- creación de perfil trabajador
- consulta de perfil propio
- edición de perfil cliente
- edición de perfil trabajador
- autorización por rol aplicada con Spring Security y seguridad declarativa

Historias principales:

- US01-M02-RF01: Perfil del trabajador.
- US02-M02-RF02: Perfil del cliente.

## M03 - Búsqueda y Selección

Permite buscar trabajadores o servicios y aplicar filtros por categoría, ubicación y precio. También contempla el cálculo de ranking.

Historias principales:

- US01-M03-RF01: Búsqueda por trabajador o servicio.
- US02-M03-RF02: Filtrar por categoría.
- US03-M03-RF03: Filtrar por ubicación.
- US04-M03-RF04: Filtrar por precio.
- US05-M03-RF05: Ordenar ranking.

## M04 - Contratación del Servicio

Gestiona solicitudes de contratación, aceptación o rechazo, visualización de contrataciones, finalización y cancelaciones.

Historias principales:

- US01-M04-RF01: Solicitud de contratación.
- US02-M04-RF02: Gestión de solicitud.
- US03-M04-RF03: Visualización de contrataciones.
- US04-M04-RF04: Confirmación de finalización.
- US05-M04-RF05: Cancelación del cliente.
- US06-M04-RF06: Cancelación del trabajador.

## M05 - Chat y Comunicación

Permite comunicación en tiempo real entre cliente y trabajador, mensajes predeterminados, indicador de no leídos, historial y estado de conexión.

Estado actual de implementación inicial:

- conversación única reutilizable por pareja cliente-trabajador
- API REST para listar conversaciones, cargar historial paginado, enviar mensajes, marcar lecturas y actualizar presencia
- WebSocket `/ws/chat` para eventos de mensajes, lectura y actualización de conversaciones
- pantalla protegida `/chat` para `CLIENT` y `WORKER`
- integración con `M04` pendiente mediante una política de acceso desacoplada; no se modifican entidades ni endpoints de contratación

Historias principales:

- US01-M05-RF01: Mensajería en tiempo real.
- US02-M05-RF02: Mensajes predeterminados para negociación.
- US03-M05-RF03: Notificaciones e indicador de no leídos.
- US04-M05-RF04: Persistencia e historial.
- US05-M05-RF05: Estado de conexión.

## M06 - Historial y Estadísticas

Permite consultar historial de servicios y visualizar estadísticas de actividad para clientes y trabajadores.

Estado actual de implementación:

- read model persistente `ServiceHistoryRecord`, sin endpoint público de escritura
- historial propio en `GET /api/history/services`, con estado, rango inclusivo, paginación y orden descendente
- estadísticas del trabajador en `GET /api/statistics/workers/me`
- estadísticas del cliente en `GET /api/statistics/clients/me`
- ventanas móviles de 7, 30 y 365 días, incluyendo el día actual
- rutas frontend protegidas `/history` y `/statistics`, con resolución del dashboard según el rol autenticado
- integración de altas pendiente de los contratos definitivos de `M04`, `M07` y `M09`; hasta entonces producción presenta estados vacíos reales

Historias principales:

- US01-M06-RF01: Visualización de historial de servicios.
- US02-M06-RF02: Estadísticas del trabajador.
- US03-M06-RF03: Estadísticas del cliente.

## M07 - Calificaciones y Reputación

Permite calificar una contratación finalizada y alimentar la reputación del usuario calificado.

Historia principal:

- US01-M07-RF01: Calificar servicio.

## M08 - Notificaciones

Gestiona notificaciones in-app, badge de no leídas, panel desplegable, marcado de lectura y generación automática por eventos relevantes.

Estado actual de implementación:

- read model persistente `InAppNotification` con deduplicación por destinatario y evento
- últimas 20 notificaciones y contador exacto en `GET /api/notifications/recent`
- lectura individual y masiva mediante endpoints autenticados
- canal dedicado `/ws/notifications` con autenticación por cookie y aislamiento por usuario
- panel responsive disponible para `CLIENT`, `WORKER` y `ADMIN`
- puerto interno `NotificationPublisher` preparado para M04, M09 y M11, sin endpoint público de escritura ni datos simulados

Historias principales:

- US01-M08-RF01: Panel de notificaciones in-app.
- US02-M08-RF02: Interacción y marcado de lectura.
- US03-M08-RF03: Generación automática de notificaciones.

## M09 - Pagos

Integra checkout con Mercado Pago, webhooks, registro de transacciones, reembolsos, compensaciones, strikes y liquidación al trabajador.

Historias principales:

- US01-M09-RF01: Checkout Mercado Pago.
- US02-M09-RF02: Recepción de notificaciones por pago aprobado.
- US03-M09-RF03: Recepción de notificaciones por pagos rechazados o pendientes.
- US04-M09-RF04: Registro de transacciones.
- US05-M09-RF05: Visualización de pago en historial de cliente.
- US06-M09-RF06: Desglose de ingresos del trabajador.
- US07-M09-RF07: Reembolso por cancelación del cliente.
- US08-M09-RF08: Cancelación del trabajador y strikes.
- US09-M09-RF09: Limpieza progresiva mensual de strikes.
- US10-M09-RF10: Liquidación automática de fondos al trabajador.

## M10 - Geolocalización

Permite seleccionar una ubicación en mapa y asociarla a perfil o contratación.

Historia principal:

- US01-M10-RF01: Seleccionar ubicación.

## M11 - Reclamos e Incidencias

Permite a usuarios registrar reclamos o incidencias asociados a una contratación, y al administrador gestionarlos, clasificarlos y resolverlos.

Historias principales:

- US01-M11-RF01: Registrar reclamo o incidencia.
- US02-M11-RF02: Gestión administrativa de reclamos.
- US03-M11-RF03: Notificación de resolución de reclamo.

## Soporte administrativo actual

Aunque todavía no existe un módulo funcional completo de reclamos o moderación, el backend ya incorpora una base administrativa operativa:

- listado paginado con búsqueda, filtros y ordenamiento del lado del servidor
- detalle de clientes y trabajadores con transiciones válidas
- cambio de estado con motivo obligatorio e historial de auditoría
- métricas agregadas por rol y estado
- protección para impedir la gestión de otras cuentas administrativas

En frontend, este soporte se consume desde `/admin/home`, `/admin/users` y `/admin/users/:userId`.

Este soporte se expone bajo `/api/admin/**` y requiere rol `ADMIN`.
