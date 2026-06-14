# Modelo de datos

## Estado actual

El modelo de datos general se mantiene abierto para módulos posteriores, pero ya existe una base implementada y operativa para `auth`, `profiles` y soporte administrativo mínimo.

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
  - no agrega tablas nuevas en esta primera etapa
  - reutiliza `User` para listado, detalle, cambio de estado y métricas básicas

Persistencia actual generada por JPA/Hibernate:

- `users`
- `user_sessions`
- `client_profiles`
- `worker_profiles`

La autenticación del backend usa Spring Security con token opaco persistido en `user_sessions`. No se utiliza JWT.

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
- Definir si la administración futura necesita entidades propias para auditoría, moderación o reclamos.
- Revisar cardinalidades.
- Definir atributos obligatorios.
- Definir enums de estado.
- Definir reglas de borrado lógico o físico.
- Definir auditoría: createdAt, updatedAt, createdBy, updatedBy.
- Definir relaciones críticas antes de generar migraciones de base de datos.
