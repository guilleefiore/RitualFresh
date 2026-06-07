# RitualFresh

RitualFresh es una plataforma web orientada a centralizar la contratacion de servicios domesticos de limpieza y mantenimiento del hogar. El sistema busca conectar clientes, trabajadores independientes y empresas prestadoras de servicios mediante un entorno organizado, transparente y confiable.

El proyecto se desarrolla para la materia Seminario Integrador de la UTN-FRM.

## Idea del proyecto

La propuesta surge a partir de una problematica cotidiana: muchas personas necesitan contratar servicios de limpieza o mantenimiento del hogar, pero suelen hacerlo mediante recomendaciones personales, redes sociales o contactos informales. Esto genera informacion dispersa, dificultad para comparar alternativas y poca confianza al momento de elegir un prestador.

Al mismo tiempo, muchos trabajadores independientes tienen pocas herramientas para mostrar sus servicios, experiencia, disponibilidad y referencias. RitualFresh busca mejorar esa vinculacion mediante una plataforma que organice la informacion y facilite la contratacion.

## Problema a resolver

El proceso actual de busqueda y contratacion de servicios domesticos presenta varios problemas:

- Uso de medios informales para encontrar trabajadores.
- Falta de informacion centralizada sobre servicios, experiencia y disponibilidad.
- Dificultad para comparar prestadores.
- Poca visibilidad para trabajadores independientes.
- Ausencia de referencias o evaluaciones confiables.
- Menor transparencia en los acuerdos de trabajo.

Como consecuencia, los clientes tienen mas incertidumbre al contratar y los trabajadores pierden oportunidades laborales o dependen de intermediarios que reducen su ingreso final.

## Objetivo general

Desarrollar una plataforma web que permita centralizar, organizar y optimizar la contratacion de servicios domesticos de limpieza y mantenimiento del hogar, facilitando la interaccion entre clientes, trabajadores independientes y empresas mediante un sistema estructurado, transparente y confiable.

## Objetivos especificos

- Permitir que trabajadores y empresas creen perfiles con servicios, experiencia, especialidades, disponibilidad y precios orientativos.
- Facilitar la busqueda de prestadores mediante filtros por tipo de servicio, ubicacion, precio, disponibilidad y reputacion.
- Incorporar solicitudes de servicio con informacion clara sobre fecha, horario, tipo de tarea y condiciones del trabajo.
- Registrar el estado de cada contratacion para mejorar la trazabilidad del servicio.
- Permitir calificaciones y comentarios para generar confianza entre usuarios.
- Integrar comunicacion interna entre cliente y trabajador.
- Incorporar pagos mediante plataformas externas, sin gestionar directamente datos financieros sensibles.
- Brindar historial y estadisticas de uso para clientes, trabajadores y administradores.

## Actores principales

| Actor | Descripcion |
|---|---|
| Cliente | Usuario que busca, compara y contrata servicios domesticos. |
| Trabajador | Usuario que ofrece servicios de limpieza o mantenimiento de forma independiente. |
| Empresa | Prestador que puede registrar y gestionar personal dentro de la plataforma. |
| Administrador | Usuario encargado de supervisar usuarios, reclamos, categorias y actividad general del sistema. |

## Alcance funcional

El sistema contempla los siguientes modulos principales:

| Modulo | Descripcion |
|---|---|
| Gestion de usuarios y autenticacion | Registro, inicio de sesion, recuperacion de contrasena, validacion de cuenta y roles de usuario. |
| Gestion de perfiles | Perfiles de clientes, trabajadores y empresas con informacion personal, servicios, experiencia, disponibilidad y precios orientativos. |
| Busqueda y seleccion | Busqueda de trabajadores o servicios mediante filtros por categoria, ubicacion, precio, disponibilidad y reputacion. |
| Contratacion del servicio | Solicitud, aceptacion, rechazo, seguimiento, finalizacion y cancelacion de servicios. |
| Chat y comunicacion | Mensajeria interna para coordinar detalles del servicio entre cliente y trabajador. |
| Historial y estadisticas | Consulta de servicios realizados, metricas de desempeno y actividad de usuarios. |
| Calificaciones y reputacion | Evaluacion del servicio mediante calificaciones, comentarios y reputacion del prestador. |
| Notificaciones | Alertas sobre solicitudes, mensajes, pagos, cancelaciones y otros eventos relevantes. |
| Pagos | Integracion con plataforma externa de pago para confirmar servicios, gestionar reembolsos y liquidaciones. |
| Geolocalizacion | Seleccion de ubicacion mediante mapa para mejorar la precision de las busquedas y contrataciones. |

## Requerimientos principales

### Gestion de usuarios y autenticacion

- Registro como cliente o trabajador.
- Inicio de sesion mediante correo y contrasena.
- Recuperacion de contrasena por correo electronico.
- Validacion de cuenta.
- Diferenciacion de roles.

### Gestion de perfiles

- Creacion y edicion de perfiles.
- Perfil de trabajador con servicios ofrecidos, experiencia, zona de trabajo, disponibilidad y precios orientativos.
- Perfil de cliente con informacion personal, direccion y preferencias de contratacion.
- Visualizacion de perfiles para facilitar la comparacion.

### Busqueda y seleccion

- Busqueda por trabajador o tipo de servicio.
- Filtros por categoria, ubicacion, precio y reputacion.
- Ordenamiento por ranking de trabajadores.
- Visualizacion de resultados con informacion relevante para decidir.

### Contratacion del servicio

- Solicitud de contratacion por parte del cliente.
- Aceptacion o rechazo de la solicitud por parte del trabajador.
- Registro de estados como pendiente, aceptado, en curso, finalizado o cancelado.
- Gestion de cancelaciones y reglas asociadas.

### Comunicacion

- Mensajeria interna entre cliente y trabajador.
- Notificaciones de nuevos mensajes.
- Historial de conversaciones para consultar acuerdos previos.

### Historial, estadisticas y reportes

- Historial de servicios realizados.
- Visualizacion de trabajos completados, pendientes o cancelados.
- Estadisticas para trabajadores y clientes.
- Reportes administrativos sobre usuarios, contrataciones, reclamos e incidencias.

### Calificaciones y reputacion

- Calificacion del servicio al finalizar una contratacion.
- Comentarios de usuarios.
- Promedio de calificaciones y reputacion del trabajador.
- Uso de la reputacion como criterio para ordenar resultados.

### Pagos

- Integracion con Mercado Pago u otra plataforma externa.
- Checkout para confirmar servicios.
- Registro de transacciones.
- Reembolsos por cancelacion.
- Liquidacion del monto correspondiente al trabajador.

### Geolocalizacion

- Seleccion de ubicacion mediante mapa.
- Almacenamiento de coordenadas asociadas al perfil o contratacion.
- Uso de la ubicacion para mejorar busquedas y contrataciones.

## Diseno del sistema

La etapa de diseno define el comportamiento funcional y visual de RitualFresh a partir de historias de usuario, criterios de aceptacion, modelo de datos, pantallas, reportes y reglas de navegacion.

El modelo funcional se organiza por modulos y utiliza historias de usuario con identificadores como `US01-M01-RF01`, lo que permite mantener trazabilidad entre requerimientos, pantallas y funcionalidades.

Entre las pantallas principales se incluyen:

- Registro de usuario.
- Inicio de sesion.
- Recuperacion de contrasena.
- Perfil del trabajador.
- Perfil del cliente.
- Busqueda y seleccion de prestadores.
- Solicitud de contratacion.
- Gestion de solicitudes pendientes.
- Gestion de contrataciones.
- Chat activo.
- Historial de servicios.
- Estadisticas del trabajador.
- Estadisticas del cliente.
- Calificacion de servicios.
- Panel de notificaciones.
- Checkout y estados de pago.
- Seleccion de ubicacion mediante mapa.

Tambien se contemplan reportes administrativos, como dashboard general, reporte tabular de contrataciones y reporte de reclamos e incidencias.

## Alcance no incluido en esta etapa

Para mantener un alcance realista dentro del tiempo academico, no se incluyen en esta etapa:

- Aplicacion movil nativa.
- Multiples pasarelas de pago.
- Gestion directa de datos financieros sensibles.
- Verificacion biometrica.
- Recomendaciones con inteligencia artificial.
- Expansion a categorias no relacionadas con limpieza y mantenimiento del hogar.

## Innovacion y sustentabilidad

RitualFresh propone una forma mas estructurada y digital de gestionar la contratacion de servicios domesticos. La plataforma incorpora perfiles profesionales, calificaciones, historial de trabajos, registro de contrataciones, reclamos, notificaciones y pagos digitales.

Desde el punto de vista social, busca dar mayor visibilidad a trabajadores independientes y mejorar la confianza entre las partes. Desde el punto de vista economico, contribuye a ordenar el mercado de servicios domesticos y a transparentar precios, condiciones y oportunidades laborales.

El proyecto se vincula con los siguientes Objetivos de Desarrollo Sostenible:

- ODS 8: Trabajo decente y crecimiento economico.
- ODS 9: Industria, innovacion e infraestructura.
- ODS 10: Reduccion de las desigualdades.

## Integrantes

| Integrante |
|---|
| Becerra, Joaquin |
| Fiore, Guillermina |
| Zalazar, Juan |

El equipo trabajara de forma colaborativa en las distintas etapas del desarrollo.

## Estado actual

El proyecto se encuentra iniciando la etapa de Desarrollo e Implementacion. Ya se elaboraron las etapas de Anteproyecto, Requerimientos y Diseno, y se esta consolidando el repositorio para comenzar la implementacion.

## Versiones

| Version | Fecha | Funcionalidades incluidas | Responsable |
|---|---|---|---|
| v0.1 | 2026-06-07 | Creacion del repositorio, configuracion inicial de ramas y CODEOWNERS. | Equipo RitualFresh |
| v0.2 | 2026-06-07 | README consolidado con sintesis de anteproyecto, requerimientos y diseno. | Equipo RitualFresh |
