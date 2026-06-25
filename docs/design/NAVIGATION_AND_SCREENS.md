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

- Dashboard administrativo.
- Gestión de usuarios.
- Gestión de reclamos.
- Reporte de contrataciones.
- Reporte de incidencias.
- Configuración general.

## Transiciones relevantes

- Registro exitoso → Inicio de sesión o pantalla de bienvenida.
- Inicio de sesión cliente → Inicio cliente.
- Inicio de sesión trabajador → Inicio trabajador.
- Inicio cliente → Mi perfil (`/profiles`).
- Inicio trabajador → Mi perfil profesional (`/profiles`).
- Mi perfil sin datos previos → Alta inicial del perfil correspondiente al rol.
- Mi perfil con datos existentes → Edición del perfil correspondiente al rol.
- Búsqueda → Resultados → Detalle de trabajador → Solicitud de contratación.
- Solicitud aceptada → Pago.
- Pago aprobado → Contratación confirmada.
- Contratación finalizada → Calificación.
- Notificación seleccionada → Pantalla relacionada.

## Acceso por rol

- Cliente: búsqueda, solicitud, pago, historial, calificaciones, estadísticas de cliente.
- Trabajador: perfil profesional, solicitudes, contrataciones asignadas, estadísticas de trabajador, datos de cobro.
- Administrador: reportes, gestión de usuarios, reclamos y configuración.

## Implementación frontend actual de perfiles

- La ruta protegida compartida es `/profiles`.
- `CLIENT` visualiza y completa datos de contacto, domicilio y preferencias de contratación.
- `WORKER` visualiza y completa descripción profesional, experiencia, servicios, zona, disponibilidad y precio orientativo.
- La misma pantalla resuelve alta o edición según la respuesta de `GET /api/profiles/me`.
