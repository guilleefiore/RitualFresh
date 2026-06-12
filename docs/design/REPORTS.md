# Reportes del sistema

## Objetivo

Los reportes permiten analizar actividad operativa, historial, reclamos, contrataciones e indicadores de desempeño dentro de RitualFresh.

## REP-01 - Dashboard Administrativo

Permite al administrador visualizar métricas generales del sistema.

Indicadores sugeridos:

- Cantidad total de usuarios registrados.
- Cantidad de clientes.
- Cantidad de trabajadores.
- Contrataciones totales.
- Contrataciones pendientes, finalizadas y canceladas.
- Reclamos abiertos y resueltos.
- Promedio general de calificaciones.
- Ingresos o montos procesados.

Representaciones sugeridas:

- Cards de indicadores.
- Gráficos de barras.
- Gráficos de línea por período.
- Gráficos de torta para distribución de estados.

## REP-02 - Reporte Tabular de Contrataciones

Permite consultar contrataciones realizadas en la plataforma.

Columnas sugeridas:

- ID de contratación.
- Cliente.
- Trabajador.
- Servicio.
- Fecha pactada.
- Estado.
- Monto.
- Estado de pago.
- Fecha de creación.
- Fecha de finalización o cancelación.

Filtros sugeridos:

- Estado.
- Rango de fechas.
- Cliente.
- Trabajador.
- Categoría.
- Estado de pago.

## REP-03 - Reporte de Reclamos e Incidencias

Permite monitorear y gestionar reclamos registrados por los usuarios.

Indicadores sugeridos:

- Reclamos totales.
- Reclamos abiertos.
- Reclamos en revisión.
- Reclamos resueltos.
- Tiempo promedio de resolución.
- Categorías más frecuentes de reclamo.

Columnas sugeridas:

- ID de reclamo.
- Usuario que reporta.
- Contratación asociada.
- Tipo de incidencia.
- Estado.
- Fecha de creación.
- Responsable de gestión.
- Resultado.

## Criterios generales

- Todo reporte debe permitir lectura clara de datos.
- Los datos deben poder filtrarse por período.
- Los estados deben estar diferenciados visualmente.
- Los reportes administrativos deben mostrar información agregada y trazable.
