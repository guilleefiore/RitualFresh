# Guía técnica de implementación

## Propósito

Este documento centraliza criterios técnicos para implementar RitualFresh de forma ordenada, consistente y mantenible.

## Principios generales

- Mantener código simple y legible.
- Evitar duplicación innecesaria.
- Separar responsabilidades.
- Priorizar trazabilidad con historias de usuario.
- No implementar funcionalidades fuera del alcance definido.
- Antes de programar un módulo, revisar contexto, reglas de negocio, historias y pantallas relacionadas.

## Backend

### Stack

- Java 21.
- Spring Boot 3.5.x.
- Maven.
- PostgreSQL.
- Hibernate ORM.
- Jakarta Persistence.
- Spring Security.
- Spring WebSocket.
- SpringDoc OpenAPI.
- JUnit 5.

### Estructura sugerida

```txt
backend/src/main/java/com/ritualfresh/
├── auth/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   └── model/
├── profiles/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   └── model/
├── contracts/
├── payments/
├── notifications/
├── reviews/
├── chat/
├── geolocation/
└── shared/
    ├── exception/
    ├── config/
    └── security/
```

Cada módulo puede contener, como mínimo, `controller`, `service`, `repository`, `dto` y `model`.

## Reglas de implementación backend

- No exponer entidades JPA directamente en controllers.
- Usar DTOs para request y response.
- Validar entradas con Jakarta Validation.
- Centralizar errores con manejo global de excepciones.
- Usar enums para estados de solicitud, contratación, pago, notificación y reclamo.
- Mantener métodos pequeños y con responsabilidad clara.
- Evitar lógica de negocio dentro de controllers.
- No mezclar lógica de pagos con lógica de contratación fuera de servicios coordinadores.
- Los webhooks deben ser idempotentes.
- Las operaciones críticas deben registrar eventos o logs.

## Frontend

### Stack

- React 19.
- Bootstrap 5.3.x.
- Google Maps API.
- Node.js 22 LTS.

### Estructura sugerida

```txt
frontend/src/
├── app/
│   ├── App.jsx
│   └── router.jsx
├── modules/
│   ├── auth/
│   ├── profiles/
│   ├── search/
│   └── contracts/
├── shared/
│   ├── components/
│   ├── services/
│   ├── constants/
│   └── styles/
└── main.jsx
```

## Reglas de implementación frontend

- Separar páginas de componentes reutilizables.
- Centralizar llamadas HTTP en services.
- Mantener validaciones visuales coherentes con backend.
- Implementar estados de carga, error, vacío y éxito.
- Respetar guía visual.
- No duplicar lógica de filtros o formateo.
- Mantener nombres de rutas y componentes consistentes con módulos.

## Convenciones de nombres

- Paquetes Java: minúsculas.
- Clases Java: PascalCase.
- Métodos y atributos Java: camelCase.
- Componentes React: PascalCase.
- Hooks React: useNombre.
- Archivos de documentación: MAYÚSCULAS_CON_GUIONES o nombres descriptivos.

## Flujo recomendado para implementar una historia

1. Leer la historia de usuario.
2. Revisar reglas de negocio asociadas.
3. Revisar pantalla o reporte relacionado.
4. Identificar entidades afectadas.
5. Definir endpoints necesarios.
6. Implementar backend.
7. Implementar frontend.
8. Agregar pruebas.
9. Probar manualmente el flujo completo.
10. Documentar evidencia si corresponde.

## Criterios de calidad

- Código compilable.
- Tests ejecutables.
- Endpoints documentados.
- Errores controlados.
- Validaciones claras.
- Seguridad básica aplicada.
- Sin cambios innecesarios en módulos no relacionados.
