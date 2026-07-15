# Guía visual y UX/UI

## Enfoque visual

La interfaz de RitualFresh debe ser clara, profesional, accesible y consistente. Se busca una estética SaaS moderna inspirada en Material Design 3.

## Paleta de colores

- Primario: `#1565C0`.
- Primario oscuro: `#0D47A1`.
- Secundario: `#26A69A`.
- Acento: `#FFA726`.
- Fondo general: `#F5F7FA`.
- Error: `#B3261E`.

## Tipografía

Tipografías sugeridas:

- Roboto.
- Inter.
- Poppins.

Criterios:

- Diseño limpio y legible.
- Jerarquía visual clara.
- Textos simples para usuarios no técnicos.

## Componentes reutilizables

- Navbar.
- Sidebar.
- Formularios.
- Cards de servicios y trabajadores.
- Botones primarios y secundarios.
- Modales de confirmación.
- Mensajes de validación.
- Badges de estado.
- Tablas.
- Reportes.
- Dashboards.
- Indicadores visuales.
- Paneles desplegables.
- Mapa interactivo.

## Estados visuales obligatorios

Las pantallas críticas deben contemplar:

- Estado por defecto.
- Estado de carga.
- Estado con datos cargados.
- Estado vacío.
- Estado con errores de validación.
- Estado exitoso.
- Estado de advertencia para acciones irreversibles o con penalización.

## Lineamientos UX/UI

- Navegación intuitiva.
- Diseño responsive.
- Validaciones visibles e inmediatas.
- Consistencia visual entre módulos.
- Mensajes claros ante errores.
- Confirmación visible ante acciones exitosas.
- Diferenciación clara de estados: pendiente, completado, cancelado, aprobado, rechazado, leído, no leído.
- Evitar pantallas saturadas.
- Priorizar información relevante: precio, disponibilidad, ubicación, calificación, estado y próxima acción.

## Pantallas principales

- M01-WFR-01: Registro de usuario.
- M01-WFR-02: Inicio de sesión.
- M01-WFR-03: Recuperación de contraseña.
- M02-WFR-01: Perfil del trabajador.
- M02-WFR-02: Perfil del cliente.
- M03-WFR-01: Búsqueda y selección.
- M04-WFR-01: Solicitud de contratación.
- M04-WFR-02: Visualización de solicitudes pendientes.
- M04-WFR-03: Gestión de contrataciones.
- M05-WFR-01: Sala de chat activa.
- M05-WFR-02: Mensajes predeterminados.
- M05-WFR-03: Historial de conversaciones.
- M06-WFR-01: Historial de servicios.
- M06-WFR-02: Estadísticas del trabajador.
- M06-WFR-03: Estadísticas del cliente.
- M07-WFR-01: Calificación de servicios.
- M08-WFR-01: Indicador de notificaciones.
- M08-WFR-02: Panel de notificaciones.
- M08-WFR-03: Estado vacío de notificaciones.
- M09-WFR-01: Checkout Mercado Pago.
- M09-WFR-02: Pago exitoso.
- M09-WFR-03: Pago cancelado o error.
- M09-WFR-04: Detalle de pago cliente.
- M09-WFR-05: Desglose financiero trabajador.
- M09-WFR-06: Reembolso por cancelación cliente.
- M09-WFR-07: Cancelación trabajador y strikes.
- M09-WFR-08: Estado de liquidación al trabajador.
- M10-WFR-01: Seleccionar ubicación.

## Estado visual implementado de M06

- `/history` utiliza una línea temporal como elemento visual principal y badges semánticos para pendiente, completado y cancelado.
- La ficha de servicio se presenta lateralmente en desktop y como panel superpuesto en móvil.
- Los filtros de estado y fechas distinguen el historial vacío de una consulta sin coincidencias.
- `/statistics` mantiene una única ruta y adapta las métricas al rol autenticado.
- Los períodos se presentan como controles seleccionables de 7, 30 y 365 días.
- Los gráficos temporales utilizan SVG propio; categorías y tablas emplean CSS y HTML semántico.
- Títulos, descripciones, valores alternativos, foco visible y preferencias de movimiento reducido forman parte del comportamiento accesible.
- Los importes se muestran en ARS con locale `es-AR`; los valores nulos se comunican como `Importe no disponible`.

## Estado visual implementado de M08

- La campana mantiene un badge con la cantidad exacta de notificaciones pendientes y un nombre accesible equivalente.
- El panel utiliza la paleta oficial, tipografía Inter/Roboto y una línea temporal vertical para comunicar el orden cronológico.
- Servicio, pago y reclamo poseen iconos y marcadores semánticos; el color nunca es la única señal de estado leído.
- Las notificaciones no leídas usan fondo suave, punto azul y texto accesible `No leída`.
- El panel contempla carga, error con reintento, información de contenido inaccesible y el vacío `No tienes notificaciones recientes`.
- En desktop se ancla a la campana y en móvil se presenta como sheet inferior sin cambiar el orden de lectura.
- `Escape`, clic exterior y el botón visible permiten cerrar; todos los botones tienen foco visible.
- Las transiciones se desactivan mediante `prefers-reduced-motion`.
