# Modelo de datos

## Estado actual

El modelo de datos general se mantiene abierto para módulos posteriores, pero ya existe una base implementada y operativa para `auth`, `profiles`, `admin`, `chat`, `history` y `notifications`.

Estado implementado actualmente:

- `auth`
  - `User`
  - `UserSession`
  - `UserRole`
  - `AccountStatus`
- `profiles`
  - `ClientProfile`
  - `WorkerProfile`
  - `ProfileType`
- `admin`
  - reutiliza `User` para listado, detalle, cambio de estado y métricas básicas
  - `AdminUserStatusChange` registra la auditoría de cada cambio administrativo de estado
- `chat`
  - `Conversation`
  - `ChatMessage`
  - `ChatPresence`
  - `ConversationStatus`
- `history`
  - `ServiceHistoryRecord`
  - `ServiceHistoryStatus`
  - referencias obligatorias a `User` para cliente y trabajador
  - importe ARS y calificación del trabajador opcionales
- `notifications`
  - `InAppNotification`
  - `NotificationType`
  - `NotificationResourceType`
  - fecha de lectura nullable y clave idempotente por destinatario

Persistencia actual generada por JPA/Hibernate:

- `users`
- `user_sessions`
- `client_profiles`
- `worker_profiles`
- `admin_user_status_changes`
- `chat_conversations`
- `chat_messages`
- `chat_presence`
- `service_history_records`
- `in_app_notifications`

La autenticación del backend usa Spring Security con token opaco persistido en `user_sessions`. No se utiliza JWT.

## Diagramas de clases

El [README](../../README.md) mantiene el catálogo completo de diagramas para M01 a M11 y el soporte administrativo. Cada diagrama declara su estado para evitar confundir el código existente con decisiones todavía abiertas:

- `Implementado`: deriva de clases presentes en el backend actual.
- `Implementación inicial existente`: representa una base funcional que todavía requiere integrarse con otro módulo.
- `Diseño propuesto`: anticipa entidades, servicios, repositorios y puertos de integración para poder revisarlos antes de codificar.

Los diagramas propuestos no fijan todavía nombres de tablas, migraciones ni contratos públicos. Deben validarse contra las historias y reglas definitivas del módulo correspondiente antes de convertirse en entidades JPA.

### Read model de M06

`service_history_records` conserva referencias obligatorias a cliente y trabajador, nombre del servicio, categoría, fecha pactada y estado. El importe ARS y la calificación del trabajador son opcionales porque pagos y reputación todavía no cuentan con contratos definitivos.

Los estados válidos son `PENDING`, `COMPLETED` y `CANCELLED`. El modelo se consulta mediante ownership de la sesión autenticada y no expone escritura pública. La integración interna con contratación, pagos y calificaciones continúa pendiente.

### Read model de M08

`in_app_notifications` conserva destinatario, tipo, título, mensaje, recurso relacionado, clave de evento, fecha de creación y fecha de lectura. La restricción única `(recipient_id, event_key)` garantiza idempotencia por usuario; los índices por destinatario, creación y lectura sostienen el panel descendente y el contador exacto.

El módulo no expone altas públicas. `NotificationPublisher` y `NotificationEventListener` dejan preparada la integración posterior con M04, M09 y M11. Los correos de validación y recuperación de M01 continúan bajo `AccountEmailService` y no utilizan esta tabla.

## Criterio de trabajo

No se tomará el diagrama de clases previo como base cerrada. Antes de codificar entidades JPA se debe revisar el modelo considerando:

- Historias de usuario definitivas.
- Reglas de negocio.
- Ciclo de vida de solicitud, contratación, pago y calificación.
- Integridad referencial.
- Nulabilidad de claves foráneas.
- Nombres semánticos y consistentes.
- Separación entre entidades de dominio, DTOs y modelos de persistencia si corresponde.

## Entidades candidatas a revisar

Estas entidades no constituyen un diseño definitivo. Funcionan únicamente como lista inicial de conceptos del dominio:

- Usuario.
- Rol.
- Cliente.
- Trabajador.
- Perfil.
- Servicio.
- Categoría.
- Disponibilidad.
- Ubicación.
- Solicitud de contratación.
- Contratación.
- Pago.
- Transacción.
- Reembolso.
- Liquidación.
- Calificación.
- Chat.
- Mensaje.
- Notificación.
- Reclamo.
- Reporte.

## Pendiente

- Revisar si `User` requiere mayor desacople entre creación de dominio, persistencia y seguridad.
- Definir entidades futuras para moderación o reclamos; la auditoría de cambios de estado ya se persiste en `admin_user_status_changes`.
- Revisar cardinalidades.
- Definir atributos obligatorios.
- Definir enums de estado.
- Definir reglas de borrado lógico o físico.
- Definir auditoría: createdAt, updatedAt, createdBy, updatedBy.
- Definir relaciones críticas antes de generar migraciones de base de datos.
- Conectar la política de habilitación del chat con el contrato definitivo de `M04` cuando esté disponible.
- Conectar la alimentación interna de `service_history_records` con los eventos definitivos de contratación, calificación y pago de `M04`, `M07` y `M09`.
- Conectar `NotificationPublisher` con los eventos definitivos de contratación, pago y reclamo de `M04`, `M09` y `M11`.
