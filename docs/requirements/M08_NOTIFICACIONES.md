# M08 - Notificaciones

## Objetivo

M08 centraliza las alertas recientes dentro de la aplicación para usuarios autenticados. El módulo persiste un read model propio, muestra las últimas 20 notificaciones, mantiene un contador exacto de pendientes y sincroniza los cambios mediante WebSocket sin recargar la página.

Las notificaciones in-app son independientes de los correos de validación y recuperación de M01. No existen seeds ni endpoints públicos para crear avisos: los módulos funcionales deben utilizar el puerto interno `NotificationPublisher`.

## Modelo persistente

`InAppNotification` registra:

- usuario destinatario;
- tipo, título y mensaje histórico;
- tipo e identificador del recurso relacionado, opcionales pero siempre presentes como par;
- clave de evento para deduplicación por destinatario;
- fecha de creación;
- fecha de lectura, nula mientras la notificación está pendiente.

Los tipos iniciales son `SERVICE_CONFIRMED`, `PAYMENT_APPROVED` y `CLAIM_RESOLVED`. Los recursos iniciales son `CONTRACT`, `PAYMENT` y `CLAIM`.

## US01-M08-RF01 - Panel de notificaciones in-app

Como usuario autenticado, quiero abrir un panel desde la campana para consultar mis alertas recientes de forma centralizada.

### Contrato

`GET /api/notifications/recent`

### Criterios de aceptación

- `CLIENT`, `WORKER` y `ADMIN` pueden consultar exclusivamente sus notificaciones.
- La identidad se obtiene de la sesión; la API no acepta identificadores de usuario.
- El panel devuelve como máximo 20 elementos, ordenados por fecha e identificador descendentes.
- `unreadCount` cuenta todas las notificaciones pendientes, incluso las que no entran entre las últimas 20.
- El badge muestra la cantidad exacta y desaparece al llegar a cero.
- Las no leídas se distinguen mediante fondo y marcador visual, además de información accesible.
- Cuando no existen elementos se muestra `No tienes notificaciones recientes`.
- La campana se integra en el encabezado compartido de cliente y trabajador y en la barra de actividad administrativa.
- En escritorio se utiliza un panel flotante y en móvil un sheet inferior.

## US02-M08-RF02 - Interacción y marcado de lectura

Como usuario autenticado, quiero marcar avisos como leídos para mantener actualizado mi listado de pendientes.

### Contratos

- `PATCH /api/notifications/{notificationId}/read`
- `PATCH /api/notifications/read-all`

### Criterios de aceptación

- El marcado individual es idempotente y sólo puede afectar notificaciones propias.
- Un identificador inexistente o ajeno responde `404` sin revelar ownership.
- La interfaz actualiza inmediatamente el resaltado y el badge; si la operación falla, revierte el cambio y muestra el error.
- Marcar todas afecta únicamente las pendientes del usuario autenticado y devuelve la cantidad actualizada.
- Al seleccionar un aviso se marca como leído antes de resolver su destino.
- `NotificationDestinationResolver` permite que cada módulo valide existencia y acceso al recurso sin acoplar M08 a su repositorio.
- Si el recurso no existe, no es accesible o no tiene resolver, se informa `El contenido ya no se encuentra disponible.` y el aviso permanece leído.
- `Escape`, clic exterior y el botón de cierre cierran el panel; los controles poseen foco visible y atributos accesibles.

## US03-M08-RF03 - Generación automática de notificaciones

Como sistema, quiero consumir eventos internos relevantes para persistir y entregar avisos sin depender de escrituras públicas.

### Integración interna

- `NotificationPublisher.publish(NotificationCommand)` publica el comando dentro de la aplicación.
- `NotificationEventListener` lo consume después del commit de origen y admite ejecución sin transacción para pruebas o adaptadores controlados.
- `NotificationService.createFromEvent` persiste en una transacción nueva.
- La combinación `(recipient_id, event_key)` evita duplicados ante reintentos.
- El WebSocket autenticado `/ws/notifications` entrega `notification.created`, `notification.read` y `notifications.read-all` únicamente al destinatario.
- El frontend reconecta con espera progresiva y vuelve a consultar REST después de una reconexión para recuperar eventos perdidos.

Integraciones previstas:

- M04 confirma una contratación: aviso para cliente y trabajador.
- M09 aprueba un pago: aviso para cliente y trabajador.
- M11 resuelve un reclamo: aviso para el usuario involucrado.

M04, M09 y M11 todavía no están implementados; por lo tanto, M08 deja disponibles y probados sus contratos internos, pero no simula eventos en producción.

## Exclusiones actuales

- No se envían SMS, push móvil ni correos desde M08.
- No existe una página completa ni paginación pública del historial de notificaciones.
- No se permite eliminar notificaciones ni configurar preferencias.
- El contador de mensajes de M05 permanece independiente.
- No se implementan dentro de M08 las contrataciones, pagos ni reclamos que originarán los eventos.
