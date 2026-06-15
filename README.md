# RitualFresh

Plataforma web académica para la gestión de servicios domésticos de limpieza y mantenimiento del hogar.

## Referencias principales

- [Contexto del proyecto](docs/context/PROJECT_CONTEXT.md)
- [Alcance](docs/context/SCOPE.md)
- [Módulos funcionales](docs/context/MODULES.md)
- [Reglas de negocio](docs/context/BUSINESS_RULES.md)
- [Guía de implementación](docs/development/IMPLEMENTATION_GUIDE.md)
- [Estado del modelo de datos](docs/architecture/DATA_MODEL_STATUS.md)
- [Índice de historias](docs/requirements/USER_STORIES_INDEX.md)

## Stack base

- Backend: Java 21, Spring Boot 3.5.x, Maven, PostgreSQL.
- Frontend: React 19, Node.js 22, Bootstrap 5.3.

## Estado actual

El proyecto ya cuenta con una base backend funcional para:

- `M01`: gestión de usuarios, validación de cuenta, login, logout, recuperación de contraseña y sesiones opacas persistidas.
- `M02`: creación, consulta y actualización de perfiles de cliente y trabajador.
- administración mínima: listado de usuarios, detalle, cambio de estado y métricas básicas.

La autenticación y autorización del backend se resuelven con Spring Security, manteniendo el modelo actual de `UserSession` y el uso de `Authorization: Bearer <sessionToken>`.

El modelo de datos general sigue sujeto a revisión para módulos posteriores, pero las entidades actuales de `auth`, `profiles` y `admin` ya se encuentran implementadas y probadas.

## Variables de entorno

El backend usa `application.properties` solo para leer configuración, pero los valores sensibles deben venir de variables de entorno.

- Para desarrollo con Docker Compose, conviene definirlas en un archivo `.env` en la raíz del proyecto.
- El repositorio incluye [.env.example](/Users/guillermina/Downloads/4º%20año/primer%20semestre/seminario%20integrador/RitualFresh/.env.example:1) como plantilla.
- `docker-compose.yml` toma esas variables y se las pasa al contenedor `backend`.

Para el envío de correos con Mailtrap, completar al menos:

- `RITUALFRESH_MAIL_ENABLED`
- `RITUALFRESH_MAIL_FROM`
- `RITUALFRESH_BACKEND_BASE_URL`
- `RITUALFRESH_FRONTEND_BASE_URL`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
