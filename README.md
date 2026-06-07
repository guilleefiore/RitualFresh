# RitualFresh

RitualFresh es una plataforma web orientada a centralizar la contratacion de servicios domesticos de limpieza y mantenimiento del hogar. El sistema busca conectar clientes, trabajadores independientes y empresas prestadoras de servicios mediante un entorno organizado, transparente y confiable.

El proyecto se desarrolla para la materia Seminario Integrador de la UTN-FRM.

## Objetivo general

Desarrollar una plataforma web que permita organizar y optimizar la contratacion de servicios domesticos, reemplazando mecanismos informales de busqueda y contacto por un sistema digital que facilite la seleccion, coordinacion, seguimiento y evaluacion de los servicios.

## Problema a resolver

Actualmente, muchas contrataciones de servicios domesticos se realizan mediante recomendaciones personales, redes sociales o contactos informales. Esto genera informacion dispersa, dificultad para comparar prestadores, poca visibilidad para trabajadores independientes y menor confianza entre las partes.

RitualFresh propone centralizar la informacion de trabajadores, servicios, disponibilidad, calificaciones e historial de contrataciones para mejorar la transparencia y la toma de decisiones.

## Alcance del sistema

El sistema contempla los siguientes modulos principales:

| Modulo | Descripcion |
|---|---|
| Gestion de usuarios y autenticacion | Registro, inicio de sesion, recuperacion de contrasena y roles de usuario. |
| Gestion de perfiles | Perfiles de clientes, trabajadores y empresas con informacion personal, servicios, experiencia, disponibilidad y precios orientativos. |
| Busqueda y seleccion | Busqueda de trabajadores o servicios mediante filtros por categoria, ubicacion, precio, disponibilidad y reputacion. |
| Contratacion del servicio | Solicitud, aceptacion, rechazo, seguimiento, finalizacion y cancelacion de servicios. |
| Chat y comunicacion | Mensajeria interna para coordinar detalles del servicio entre cliente y trabajador. |
| Historial y estadisticas | Consulta de servicios realizados, metricas de desempeno y actividad de usuarios. |
| Calificaciones y reputacion | Evaluacion del servicio mediante calificaciones y comentarios. |
| Notificaciones | Alertas sobre solicitudes, mensajes, pagos, cancelaciones y otros eventos relevantes. |
| Pagos | Integracion con plataforma externa de pago para confirmar servicios, gestionar reembolsos y liquidaciones. |
| Geolocalizacion | Seleccion de ubicacion mediante mapa para mejorar la precision de las busquedas y contrataciones. |

## Integrantes

| Integrante | Rol principal | Responsabilidades iniciales |
|---|---|---|
| Becerra, Joaquin | Backend Developer | Logica del sistema, base de datos, APIs y funcionalidades principales. |
| Fiore, Guillermina | Full Stack Developer / Project Lead | Coordinacion del proyecto, arquitectura, frontend, backend e integracion de modulos. |
| Zalazar, Juan | Frontend Developer | Interfaz de usuario, UX/UI, integracion con backend y validacion de funcionalidades. |

Los roles son una organizacion inicial. El equipo trabajara de forma colaborativa en las distintas etapas del desarrollo.

## Documentacion

| Documento | Ubicacion | Contenido |
|---|---|---|
| Anteproyecto | [`docs/anteproyecto/Anteproyecto.docx`](docs/anteproyecto/Anteproyecto.docx) | Idea del proyecto, problema, objetivos, alcance, innovacion, sustentabilidad e integrantes. |
| Requerimientos | [`docs/requerimientos/Requerimientos.pdf`](docs/requerimientos/Requerimientos.pdf) | Relevamiento, analisis de sistemas existentes, requerimientos, roles, tareas y alcance preliminar. |
| Diseno | [`docs/diseño/Diseno.docx`](docs/diseño/Diseno.docx) | Modelo funcional, historias de usuario, modelo de datos, pantallas, reportes y alcance definitivo. |
| Versiones | [`docs/versiones.md`](docs/versiones.md) | Registro simple de versiones del sistema. |

## Gestion de configuracion

El repositorio utiliza Git y GitHub como herramienta de control de versiones.

### Ramas

| Rama | Uso |
|---|---|
| `main` | Rama estable del proyecto. Los cambios importantes se integran mediante Pull Request. |
| `dev-guillermina` | Rama de trabajo de Guillermina. |
| `dev-joaquin` | Rama de trabajo de Joaquin. |
| `dev-juan` | Rama de trabajo de Juan. |

### Flujo de trabajo

1. Cada integrante trabaja sobre su rama `dev-nombre`.
2. Los cambios importantes se proponen mediante Pull Request hacia `main`.
3. La integracion a `main` requiere revision antes de mergear.
4. Los commits deben incluir el identificador del caso de uso, historia de usuario o tarea trabajada.

Ejemplos de commits:

```text
CU-03: agregar alta de cliente
US01-M01-RF01: implementar registro de usuario
DOC-01: actualizar documentacion inicial
```

## Estado actual

El proyecto se encuentra iniciando la etapa de Desarrollo e Implementacion. La documentacion de Anteproyecto, Requerimientos y Diseno ya fue elaborada y se esta configurando el repositorio para organizar el trabajo del equipo.

## Versiones

El historial de versiones del sistema se mantiene en [`docs/versiones.md`](docs/versiones.md).
