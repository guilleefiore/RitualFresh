# AGENTS.md

## Proyecto

RitualFresh es un proyecto academico de Seminario Integrador de la UTN-FRM. El objetivo es desarrollar una plataforma web para centralizar la contratacion de servicios domesticos de limpieza y mantenimiento del hogar.

El sistema conecta clientes, trabajadores independientes, empresas prestadoras de servicios y administradores mediante perfiles, busqueda, contrataciones, chat, calificaciones, pagos externos, historial, estadisticas, notificaciones y geolocalizacion.

## Fuente de contexto

El README.md es la fuente principal de contexto funcional del repositorio. No reintroducir documentos pesados en `docs/` salvo que la usuaria lo pida explicitamente.

Los documentos originales de trabajo existian como Anteproyecto, Requerimientos y Diseno, pero el repositorio debe mantener una sintesis navegable en Markdown en lugar de depender de archivos `.docx` o `.pdf`.

## Estilo de documentacion

- Escribir en espanol.
- Mantener un tono academico, claro y reutilizable para la catedra.
- Evitar asignar responsabilidades individuales detalladas a los integrantes.
- Mantener la frase de trabajo colaborativo del equipo.
- Preferir Markdown simple y tablas cuando ayuden a leer rapido.
- No inventar funcionalidades fuera del alcance ya definido.

## Alcance funcional

Modulos principales:

- Gestion de usuarios y autenticacion.
- Gestion de perfiles.
- Busqueda y seleccion.
- Contratacion del servicio.
- Chat y comunicacion.
- Historial y estadisticas.
- Calificaciones y reputacion.
- Notificaciones.
- Pagos mediante plataforma externa.
- Geolocalizacion.

No incluir por defecto:

- Aplicacion movil nativa.
- Multiples pasarelas de pago.
- Gestion directa de datos financieros sensibles.
- Verificacion biometrica.
- Recomendaciones con inteligencia artificial.
- Expansion a servicios fuera de limpieza y mantenimiento del hogar.

## Git y ramas

La rama estable es `main`. Las ramas de trabajo del equipo son:

- `dev-guillermina`
- `dev-joaquin`
- `dev-juan`

Los cambios importantes deben proponerse mediante Pull Request hacia `main`.

Usar mensajes de commit con identificador cuando sea posible, por ejemplo:

```text
DOC-03: simplificar README
US01-M01-RF01: implementar registro de usuario
CU-03: agregar alta de cliente
```

## Indicaciones para agentes

- Antes de editar, revisar el estado de Git y el contenido actual del README.
- Mantener los cambios acotados al pedido.
- Si se actualiza el alcance o el estado del proyecto, actualizar tambien la tabla de versiones del README.
- No subir archivos binarios pesados al repositorio sin confirmacion.
- No borrar configuracion de GitHub como CODEOWNERS sin pedido explicito.
