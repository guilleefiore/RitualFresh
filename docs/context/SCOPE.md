# Alcance del sistema

## Alcance incluido

RitualFresh incluye el análisis, diseño y desarrollo de una plataforma web para servicios domésticos de limpieza y mantenimiento del hogar.

El sistema contempla:

- Registro e inicio de sesión de usuarios (email/contraseña y Google OAuth).
- Validación de cuenta, cierre de sesión y recuperación de contraseña.
- Reenvío de validación y autoeliminación lógica de cuenta.
- Roles de cliente, trabajador y administrador.
- Perfil del cliente.
- Perfil del trabajador.
- Búsqueda de trabajadores o servicios.
- Filtros por categoría, ubicación y precio.
- Ranking de trabajadores basado en reputación, actividad y cancelaciones.
- Solicitud de contratación.
- Aceptación o rechazo de solicitudes por parte del trabajador.
- Visualización de contrataciones.
- Confirmación de finalización del servicio.
- Cancelación del cliente.
- Cancelación del trabajador.
- Chat entre cliente y trabajador.
- Mensajes predeterminados.
- Indicadores de mensajes no leídos.
- Historial de conversaciones.
- Historial de servicios.
- Estadísticas para trabajadores y clientes.
- Calificación del servicio.
- Notificaciones in-app.
- Checkout con Mercado Pago.
- Registro de transacciones.
- Reembolsos y compensaciones.
- Liquidación automática al trabajador.
- Selección de ubicación mediante mapa.
- Reportes administrativos.
- Gestión administrativa básica de usuarios.

## Fuera de alcance inicial

No forman parte del alcance inicial:

- Aplicación móvil nativa.
- Múltiples pasarelas de pago.
- Gestión directa de datos financieros sensibles.
- Verificación biométrica.
- Inteligencia artificial para recomendaciones.
- Expansión a categorías no vinculadas con limpieza y mantenimiento del hogar.
- Sistema avanzado de capacitación de trabajadores.
- Sistema financiero propio o créditos internos.
- Gestión de empresas prestadoras de servicios (el sistema se centra en trabajadores independientes).

## Criterio de priorización

Se priorizan funcionalidades que permitan demostrar el flujo completo del sistema:

1. Registro e inicio de sesión.
2. Perfil del trabajador y del cliente.
3. Búsqueda y selección.
4. Solicitud y gestión de contratación.
5. Pago, confirmación y notificación.
6. Historial, calificación y reportes.

## Nota sobre estado actual

En el estado actual del repositorio, el backend ya implementa una base operativa para:

- `M01` autenticación, validación, recuperación y sesiones opacas con Spring Security
- `M02` perfiles de cliente y trabajador
- soporte administrativo mínimo para usuarios y métricas

Los módulos restantes continúan como alcance funcional previsto, pero no cuentan todavía con implementación completa.
