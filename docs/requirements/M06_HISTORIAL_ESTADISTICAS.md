# M06 - Historial y Estadísticas

## Objetivo

El módulo permite que clientes y trabajadores consulten los servicios vinculados con su propia cuenta y obtengan indicadores de actividad dentro de períodos móviles. La identidad se obtiene siempre de la sesión autenticada; ningún contrato acepta un identificador de usuario para seleccionar el propietario de los datos.

M06 utiliza un read model persistente propio porque los contratos definitivos de contratación, calificación y pago de `M04`, `M07` y `M09` todavía no están implementados. No existen endpoints públicos para crear registros de historial ni datos simulados en producción. Los futuros módulos deberán alimentar este modelo mediante integración interna.

## Modelo de lectura

`ServiceHistoryRecord` registra:

- cliente y trabajador asociados;
- nombre del servicio y categoría;
- fecha y hora pactadas;
- estado `PENDING`, `COMPLETED` o `CANCELLED`;
- importe en pesos argentinos, opcional;
- calificación del trabajador, opcional.

La fecha pactada es la referencia utilizada tanto por los filtros de historial como por las estadísticas.

## US01-M06-RF01 - Visualización del historial de servicios

Como cliente o trabajador autenticado, quiero consultar el historial de servicios asociados con mi cuenta para revisar su información y estado.

### Contrato

`GET /api/history/services?status&from&to&page&size`

### Criterios de aceptación

- Sólo `CLIENT` y `WORKER` pueden acceder.
- Cada usuario visualiza únicamente registros donde participa con su rol actual.
- `status` acepta `PENDING`, `COMPLETED` o `CANCELLED` y es opcional.
- `from` y `to` usan formato ISO `yyyy-MM-dd`, son opcionales e incluyen el día completo indicado.
- Si ambas fechas están presentes, `from` no puede ser posterior a `to`.
- Los resultados se ordenan por fecha pactada más reciente y, ante empate, por identificador descendente.
- El tamaño predeterminado y máximo de página es de 20 registros.
- Cada elemento incluye toda la información requerida por la ficha lateral; no existe un endpoint de detalle separado.
- Un importe nulo se presenta como `Importe no disponible`.
- La interfaz diferencia un historial inexistente de una consulta filtrada sin coincidencias.
- La carga incremental conserva los registros de las páginas anteriores.

## US02-M06-RF02 - Estadísticas del trabajador

Como trabajador autenticado, quiero consultar mis trabajos completados y calificaciones para comprender mi actividad reciente.

### Contrato

`GET /api/statistics/workers/me?period=LAST_7_DAYS|LAST_30_DAYS|LAST_365_DAYS`

### Criterios de aceptación

- Sólo `WORKER` puede acceder.
- El período predeterminado es `LAST_30_DAYS`.
- Las ventanas son móviles, incluyen el día actual y utilizan la fecha pactada del servicio.
- Los trabajos completados consideran exclusivamente registros `COMPLETED`.
- El promedio se calcula sólo con registros completados que tengan calificación.
- Si no existen calificaciones, el promedio se devuelve como nulo y se muestra `Sin calificaciones`.
- La evolución temporal agrupa 7 días por día, 30 días en semanas consecutivas y 365 días por segmentos de mes calendario.
- Si no existen trabajos completados se presenta un estado vacío específico.

## US03-M06-RF03 - Estadísticas del cliente

Como cliente autenticado, quiero consultar servicios, gastos, categorías y trabajadores frecuentes para comprender mi actividad de contratación.

### Contrato

`GET /api/statistics/clients/me?period=LAST_7_DAYS|LAST_30_DAYS|LAST_365_DAYS`

### Criterios de aceptación

- Sólo `CLIENT` puede acceder.
- El período predeterminado es `LAST_30_DAYS` y sigue las mismas ventanas móviles del trabajador.
- La actividad efectiva incluye servicios `PENDING` y `COMPLETED`.
- Los registros `CANCELLED` se excluyen de todas las métricas del cliente.
- El gasto total suma únicamente importes disponibles de registros `COMPLETED`.
- Las categorías utilizadas consideran únicamente registros `COMPLETED` y se ordenan por cantidad descendente y nombre.
- Los trabajadores frecuentes consideran únicamente registros `COMPLETED`.
- Se muestran como máximo cinco trabajadores, ordenados por cantidad descendente y, ante empate, por nombre e identificador.
- El gasto temporal utiliza las mismas agrupaciones diaria, semanal y mensual.
- Si no existe actividad efectiva se presenta un estado vacío específico.

## Comportamiento frontend común

- `/history` y `/statistics` son rutas protegidas compartidas por `CLIENT` y `WORKER`.
- `/statistics` resuelve el dashboard dentro de la misma pantalla según el rol autenticado.
- Las solicitudes se centralizan en el servicio del módulo y utilizan la cookie de sesión mediante `credentials: include`.
- Los efectos React ignoran respuestas obsoletas cuando cambian filtros, página o período.
- Los importes se formatean como ARS con locale `es-AR`.
- Los gráficos se construyen con SVG y CSS propios e incluyen título, descripción y alternativa textual accesible.
- La línea temporal, los badges semánticos y la ficha lateral se adaptan a desktop y móvil.

## Exclusiones actuales

- M06 no crea contrataciones, pagos ni calificaciones.
- No se incorporan seeds ni endpoints de escritura para poblar el historial.
- La integración interna con `M04`, `M07` y `M09` permanece pendiente hasta definir sus contratos definitivos.
