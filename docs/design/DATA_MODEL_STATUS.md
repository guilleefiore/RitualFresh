# Modelo de datos

## Estado actual

El modelo de datos se encuentra pendiente de revisión antes de su implementación definitiva.

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

- Revisar cardinalidades.
- Definir atributos obligatorios.
- Definir enums de estado.
- Definir reglas de borrado lógico o físico.
- Definir auditoría: createdAt, updatedAt, createdBy, updatedBy.
- Definir relaciones críticas antes de generar migraciones de base de datos.
