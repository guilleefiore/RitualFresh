# AGENTS.md

## Propósito

Este archivo contiene el contexto estable del proyecto RitualFresh para Codex y otras IAs de asistencia al desarrollo. Usarlo como guía antes de proponer cambios, crear código o modificar documentación.

## Proyecto

RitualFresh es un proyecto académico de Seminario Integrador de la UTN-FRM. El objetivo es desarrollar una plataforma web para centralizar la contratación de servicios domésticos de limpieza y mantenimiento del hogar.

El sistema conecta clientes, trabajadores independientes, empresas prestadoras de servicios y administradores mediante perfiles, búsqueda, contrataciones, chat, calificaciones, pagos externos, historial, estadísticas, notificaciones y geolocalización.

## Fuente de contexto

El `README.md` es la fuente principal de contexto funcional del repositorio. No reintroducir documentos pesados en `docs/` salvo que la usuaria lo pida explícitamente.

Los documentos originales de trabajo existían como Anteproyecto, Requerimientos y Diseño, pero el repositorio debe mantener una síntesis navegable en Markdown en lugar de depender de archivos `.docx` o `.pdf`.

## Estilo de documentación

- Escribir en español.
- Usar tildes, ñ y redacción académica clara.
- Mantener un tono reutilizable para la cátedra.
- Evitar asignar responsabilidades individuales detalladas a los integrantes.
- Mantener la frase de trabajo colaborativo del equipo.
- Preferir Markdown simple y tablas cuando ayuden a leer rápido.
- No inventar funcionalidades fuera del alcance ya definido.
- Si se actualiza el alcance o el estado del proyecto, actualizar también la tabla de versiones del README.

## Integrantes

| Integrante | Legajo |
|---|---|
| Becerra, Joaquín | 50799 |
| Fiore, Guillermina | 50024 |
| Zalazar, Juan | 51156 |

No inventar legajos nuevos. Si falta un dato de integrante, dejarlo como `Pendiente` y pedirlo.

## Alcance funcional

Módulos principales:

- Gestión de usuarios y autenticación.
- Gestión de perfiles.
- Búsqueda y selección.
- Contratación del servicio.
- Chat y comunicación.
- Historial y estadísticas.
- Calificaciones y reputación.
- Notificaciones.
- Pagos mediante plataforma externa.
- Geolocalización.

No incluir por defecto:

- Aplicación móvil nativa.
- Múltiples pasarelas de pago.
- Gestión directa de datos financieros sensibles.
- Verificación biométrica.
- Recomendaciones con inteligencia artificial.
- Expansión a servicios fuera de limpieza y mantenimiento del hogar.

## Desarrollo por módulos

El desarrollo debe organizarse por módulo para mantener trazabilidad con los requerimientos y pantallas diseñadas.

| Módulo | Identificador | Alcance base |
|---|---|---|
| Gestión de usuarios y autenticación | M01 | Registro, inicio de sesión, recuperación de contraseña, validación de cuenta y roles. |
| Gestión de perfiles | M02 | Perfil del trabajador, perfil del cliente, edición y visualización de datos. |
| Búsqueda y selección | M03 | Buscador, filtros por categoría/ubicación/precio, resultados y ranking. |
| Contratación del servicio | M04 | Solicitudes, aceptación/rechazo, estados del servicio, finalización y cancelaciones. |
| Chat y comunicación | M05 | Mensajería interna, mensajes rápidos e historial de conversaciones. |
| Historial y estadísticas | M06 | Historial de servicios, métricas y reportes. |
| Calificaciones y reputación | M07 | Calificaciones, comentarios, reputación y uso de reputación en ranking. |
| Notificaciones | M08 | Panel de notificaciones, estados de lectura y alertas automáticas. |
| Pagos | M09 | Checkout externo, pagos, reembolsos, liquidaciones y trazabilidad financiera. |
| Geolocalización | M10 | Selección de ubicación, coordenadas y mapa interactivo. |

## Reglas de implementación

- Antes de editar, revisar `git status` y el contenido actual del archivo afectado.
- Mantener los cambios acotados al pedido.
- Trabajar por módulo: no mezclar funcionalidades de módulos distintos salvo que sea necesario para integrar.
- Usar nombres de commits con identificador cuando sea posible.
- No subir archivos binarios pesados al repositorio sin confirmación.
- No borrar configuración de GitHub como `CODEOWNERS` sin pedido explícito.
- Si todavía no existe stack técnico definido, no asumirlo como definitivo sin validarlo con la usuaria.
- Al implementar una pantalla, respetar el identificador del diseño cuando exista, por ejemplo `M01-WFR-01`.
- Al implementar una historia de usuario, conservar el identificador funcional cuando exista, por ejemplo `US01-M01-RF01`.

## Git y ramas

La rama estable es `main`. Las ramas de trabajo del equipo son:

- `dev-guillermina`
- `dev-joaquin`
- `dev-juan`

Los cambios importantes deben proponerse mediante Pull Request hacia `main`.

Usar mensajes de commit con identificador, por ejemplo:

```text
DOC-03: simplificar README
US01-M01-RF01: implementar registro de usuario
M03: agregar filtros de búsqueda
CU-03: agregar alta de cliente
```

## Criterios para futuras IAs

- Si la tarea es documental, actualizar primero `README.md` y sólo crear nuevos archivos Markdown si agregan valor real.
- Si la tarea es de desarrollo, identificar primero el módulo afectado y mencionar qué historias o pantallas impacta.
- Si la tarea implica arquitectura, base de datos, autenticación, pagos o geolocalización, explicitar supuestos y riesgos antes de implementar.
- Si la tarea toca pagos, recordar que la aplicación no debe gestionar directamente datos financieros sensibles.
- Si la tarea toca geolocalización, contemplar selección manual de ubicación y permisos del navegador.
- Si la tarea toca calificaciones o ranking, mantener relación con historial, reputación y contrataciones finalizadas.
- Si la tarea toca cancelaciones, mantener reglas de anticipación, reembolsos y penalizaciones como parte del diseño.
