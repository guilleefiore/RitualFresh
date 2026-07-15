# Navegación y pantallas

## Acceso público

- Inicio.
- Registro.
- Inicio de sesión.
- Recuperación de contraseña.

## Navegación del cliente

- Inicio cliente.
- Mi perfil.
- Buscar servicios.
- Resultados de búsqueda.
- Detalle de trabajador.
- Solicitud de contratación.
- Pago.
- Mis contrataciones.
- Historial.
- Estadísticas.
- Chat.
- Notificaciones.
- Calificaciones.

## Navegación del trabajador

- Inicio trabajador.
- Mi perfil profesional.
- Solicitudes pendientes.
- Contrataciones asignadas.
- Historial.
- Estadísticas.
- Chat.
- Notificaciones.
- Datos de cobro.

## Navegación del administrador

- Dashboard administrativo con métricas y actividad reciente.
- Campana de notificaciones in-app en la barra de actividad.
- Directorio de clientes y trabajadores con búsqueda, filtros y paginación.
- Detalle de usuario con datos de cuenta e historial.
- Cambio de estado de cuenta mediante confirmación y motivo obligatorio.

## Transiciones relevantes

- Registro exitoso → Inicio de sesión o pantalla de bienvenida.
- Inicio de sesión con Google (usuario nuevo) → `/choose-role` (selección de rol).
- Selección de rol completada → Inicio cliente o Inicio trabajador según el rol elegido.
- Inicio de sesión cliente → Inicio cliente.
- Inicio de sesión trabajador → Inicio trabajador.
- Inicio cliente → Mi perfil (`/profiles`).
- Inicio trabajador → Mi perfil profesional (`/profiles`).
- Inicio cliente o trabajador → Historial (`/history`).
- Inicio cliente o trabajador → Estadísticas propias (`/statistics`).
- Mi perfil sin datos previos → Alta inicial del perfil correspondiente al rol.
- Mi perfil con datos existentes → Edición del perfil correspondiente al rol.
- Búsqueda → Resultados → Detalle de trabajador → Solicitud de contratación.
- Solicitud aceptada → Pago.
- Pago aprobado → Contratación confirmada.
- Contratación finalizada → Calificación.
- Campana → Panel con las últimas 20 notificaciones.
- Notificación seleccionada → Marcado de lectura → Pantalla relacionada si el recurso continúa accesible.
- Notificación sin recurso accesible → Marcado de lectura → Mensaje informativo dentro del panel.

## Acceso por rol

- Cliente: búsqueda, solicitud, pago, historial, calificaciones, estadísticas de cliente.
- Trabajador: perfil profesional, solicitudes, contrataciones asignadas, estadísticas de trabajador, datos de cobro.
- Administrador: dashboard, métricas, directorio de usuarios, detalle, cambio de estado e historial de auditoría.

## Implementación frontend actual de notificaciones

- La campana se encuentra en el `UserLayout` compartido por cliente y trabajador y en `AdminLayout`.
- El panel no utiliza una ruta propia: se superpone a cualquier pantalla autenticada.
- En escritorio se abre como menú flotante; en móvil se adapta a sheet inferior.
- La línea temporal diferencia servicio confirmado, pago aprobado y reclamo resuelto mediante iconos y color semántico.
- El estado vacío indica `No tienes notificaciones recientes`.
- La lectura individual resuelve el destino después de persistir el cambio; la lectura masiva actualiza todos los elementos visibles de inmediato.

## Implementación frontend actual de perfiles

- La ruta protegida compartida es `/profiles`.
- `CLIENT` visualiza y completa datos de contacto, domicilio y preferencias estructuradas de contratación: frecuencia, momentos preferidos, servicios de interés y observaciones opcionales.
- `WORKER` visualiza y completa descripción profesional, experiencia, servicios, zona, disponibilidad y precio orientativo.
- La misma pantalla resuelve alta o edición según la respuesta de `GET /api/profiles/me`.

## Implementación frontend actual de admin

- La ruta protegida principal es `/admin/home`.
- El dashboard muestra métricas operativas y los últimos usuarios registrados.
- El directorio independiente se encuentra en `/admin/users` y resuelve búsqueda, filtros, ordenamiento y paginación en el backend.
- El detalle navega a `/admin/users/:userId`.
- El detalle muestra únicamente clientes o trabajadores, las transiciones permitidas y el historial de cambios con su motivo.
- No existe todavía una pantalla independiente para reclamos, reportes o configuración.

## Implementación frontend actual de historial y estadísticas

- `/history` es una ruta protegida compartida por `CLIENT` y `WORKER`.
- El historial permite filtrar por estado y rango inclusivo, cargar páginas de 20 elementos y abrir una ficha lateral sin navegar a otra ruta.
- La interfaz diferencia un historial inexistente de una búsqueda sin coincidencias.
- `/statistics` resuelve el dashboard según el rol autenticado y utiliza los últimos 30 días como período inicial.
- El trabajador visualiza trabajos completados, promedio de calificaciones y evolución temporal.
- El cliente visualiza actividad efectiva, gasto, categorías y hasta cinco trabajadores frecuentes; los cancelados quedan excluidos.
- Los gráficos se construyen con SVG y CSS del proyecto, incluyen título, descripción y una alternativa textual accesible.
