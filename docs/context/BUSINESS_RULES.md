# Reglas de negocio

Este documento resume reglas funcionales de RitualFresh derivadas del análisis del dominio, el relevamiento de plataformas existentes y el diseño del sistema.

## Usuarios y autenticación

- Un usuario puede registrarse como cliente o trabajador.
- El correo electrónico debe ser único entre cuentas vigentes dentro del sistema.
- No se permite registrar una cuenta con un correo ya existente si la cuenta está activa, pendiente de validación o suspendida.
- Si una cuenta fue eliminada lógicamente (`DELETED`), el usuario puede volver a registrarse con el mismo correo; el sistema reutiliza la cuenta existente y la deja pendiente de validación.
- Si una cuenta eliminada vuelve mediante Google OAuth, el sistema reactiva la cuenta existente y crea una nueva sesión.
- Una cuenta suspendida (`SUSPENDED`) no puede iniciar sesión ni volver a registrarse con el mismo correo hasta que un administrador la reactive.
- El registro público solicita email, contraseña, confirmación y rol; DNI, teléfono, dirección y datos profesionales se completan posteriormente en el perfil correspondiente.
- El rol del usuario queda bloqueado una vez creado su perfil para evitar perfiles incompatibles.
- El usuario debe iniciar sesión para acceder a funcionalidades privadas.
- La recuperación de contraseña requiere un correo registrado.
- El sistema debe diferenciar permisos según rol.

## Perfiles

- Un trabajador debe completar su perfil profesional para ofrecer servicios de forma confiable.
- Un cliente debe completar información personal y dirección para agilizar futuras solicitudes.
- Los campos obligatorios deben validarse antes de guardar.
- La foto de perfil es obligatoria y debe ser una imagen JPG, PNG o WEBP de hasta 5 MB.
- Los datos ingresados con formato inválido deben conservarse en pantalla para permitir corrección sin pérdida de información.
- La información del perfil debe presentarse de forma estructurada para facilitar comparación y confianza.

## Búsqueda y selección

- La búsqueda debe permitir localizar trabajadores o servicios.
- Los resultados deben poder filtrarse por categoría, ubicación y precio.
- Si no existen coincidencias, el sistema debe mostrar un estado vacío claro.
- Los trabajadores deben ordenarse según criterios de coincidencia, reputación y ranking.
- El ranking considera principalmente calificaciones, trabajos realizados recientemente y cancelaciones.
- El ranking debe recalcularse periódicamente.

## Contratación

- Un cliente debe estar autenticado para solicitar una contratación.
- El cliente debe seleccionar trabajador, servicio, fecha y horario.
- El horario solicitado debe estar disponible para poder crear la solicitud.
- Al crear una solicitud válida, el horario queda reservado hasta la decisión del trabajador.
- Una solicitud puede ser aceptada o rechazada por el trabajador.
- Al aceptar una solicitud, se crea una contratación asociada.
- Al rechazar una solicitud, el trabajador puede definir si el horario vuelve a quedar disponible.
- La contratación debe mantener estado registrado y trazable.

## Estados sugeridos de solicitud

- Solicitada.
- Aceptada.
- Rechazada.
- Pendiente de confirmación de pago.
- Confirmada.
- Cancelada.

## Estados sugeridos de contratación

- Pendiente.
- En curso.
- Finalizada.
- Cancelada.

## Finalización del servicio

- El cliente puede confirmar la finalización cuando el servicio ya debería haberse realizado.
- No se permite finalizar una contratación antes de la fecha acordada.
- Al finalizar una contratación, se habilita el proceso de calificación.
- Si el cliente no confirma manualmente la finalización, el sistema puede finalizarla automáticamente después del plazo definido.

## Cancelación del cliente

- El cliente puede cancelar una contratación pendiente o en curso.
- Si cancela con 48 horas o más de anticipación, corresponde reembolso total.
- Si cancela con menos de 48 horas de anticipación, se advierte la penalización antes de confirmar.
- En cancelación tardía del cliente, el sistema procesa reembolso parcial y registra una compensación para el trabajador.
- Toda cancelación debe notificar a las partes involucradas.

## Cancelación del trabajador

- El trabajador puede cancelar una contratación asignada.
- Si cancela con 48 horas o más de anticipación, no recibe penalización.
- Si cancela con menos de 48 horas de anticipación, recibe 1 strike.
- Si un trabajador alcanza 3 strikes, su perfil se suspende automáticamente.
- Un trabajador suspendido no debe aparecer en búsquedas públicas.
- Un trabajador suspendido no debe poder aceptar nuevas solicitudes.
- Los strikes pueden reducirse progresivamente mediante proceso automático mensual, salvo en perfiles suspendidos.

## Pagos

- El pago se realiza mediante pasarela externa.
- El sistema no debe gestionar directamente datos financieros sensibles.
- Para iniciar el pago, debe existir solicitud válida, trabajador seleccionado, horario válido y monto mayor a cero.
- Al generar preferencia de pago, el cliente es redirigido al checkout externo.
- Al volver de un pago exitoso, la solicitud queda pendiente de confirmación hasta validar el webhook.
- El webhook debe validar el pago contra la pasarela antes de confirmar la contratación.
- Un pago aprobado confirma la solicitud y registra notificaciones para cliente y trabajador.
- Los webhooks duplicados deben procesarse de forma idempotente.
- Un pago rechazado o cancelado debe cancelar la solicitud y liberar la disponibilidad.
- Un pago pendiente debe mantener la reserva durante el plazo definido.
- Si un pago pendiente supera el plazo máximo sin aprobarse, la solicitud se cancela automáticamente.

## Transacciones

- Cada transacción debe registrar identificador externo, monto bruto, comisión, monto neto, estado, fecha, cliente y trabajador.
- El identificador externo del pago debe ser único.
- Los cambios de estado de un pago existente no deben crear transacciones duplicadas.
- El monto neto se calcula restando la comisión configurada de la plataforma.

## Liquidación al trabajador

- La liquidación se dispara cuando el servicio se encuentra finalizado y el pago está aprobado.
- El trabajador debe tener datos de cobro válidos.
- Si los datos de cobro faltan o son inválidos, el pago queda pendiente de datos de cobro o fallo de liquidación.
- Toda liquidación exitosa o fallida debe generar notificación al trabajador.

## Chat y comunicación

- El chat se habilita solo si existe solicitud o contratación asociada entre las partes.
- No se deben enviar mensajes vacíos o compuestos solo por espacios.
- El mensaje debe respetar el límite máximo de caracteres definido.
- El sistema debe persistir el historial de conversaciones.
- Los mensajes deben cargarse de forma paginada para conversaciones largas.
- Si un servicio se cancela o finaliza, el chat pasa a solo lectura.
- El estado de conexión y los mensajes no leídos deben actualizarse dinámicamente.

## Notificaciones

- M08 sólo genera notificaciones mediante integración interna; no expone endpoints públicos de escritura ni datos simulados.
- Los eventos iniciales son servicio confirmado, pago aprobado y reclamo resuelto; nuevos eventos deben incorporarse con criterios de aceptación propios.
- Cada comando incluye una clave estable y sólo puede producir una notificación por destinatario para evitar duplicados.
- El panel muestra como máximo las últimas 20 notificaciones ordenadas de más nuevas a más antiguas.
- El badge cuenta todas las notificaciones no leídas del usuario, incluidas las anteriores a las 20 visibles.
- Las notificaciones no leídas deben diferenciarse visualmente.
- La identidad siempre se obtiene de la sesión; ningún usuario puede leer o modificar avisos ajenos.
- Al interactuar con una notificación debe marcarse como leída de forma idempotente.
- Marcar todas como leídas sólo afecta al usuario autenticado.
- Si una notificación apunta a un contenido inexistente o inaccesible, debe mostrar mensaje informativo y marcarse igualmente como leída.
- Las altas y los cambios de lectura se publican por WebSocket sólo después de persistirse y se resincronizan por REST al reconectar.
- El correo de autenticación de M01 y las notificaciones in-app de M08 son responsabilidades independientes.

## Historial y estadísticas

- El usuario autenticado puede consultar servicios asociados a su cuenta.
- La identidad se obtiene de la sesión y no de identificadores recibidos por la API.
- El historial admite exclusivamente los estados `PENDING`, `COMPLETED` y `CANCELLED`.
- El historial se ordena por fecha pactada más reciente y se pagina con un máximo de 20 registros.
- El historial permite filtros opcionales por estado y rango inclusivo de fechas.
- La fecha inicial no puede ser posterior a la fecha final.
- Cada registro contiene la información completa de la ficha lateral; un importe nulo se presenta como `Importe no disponible`.
- Si no existen servicios asociados, debe mostrarse estado vacío.
- La interfaz diferencia la ausencia total de historial de una consulta filtrada sin resultados.
- Los períodos estadísticos son ventanas móviles de 7, 30 o 365 días e incluyen el día actual.
- Las series temporales agrupan 7 días por día, 30 días por semanas consecutivas y 365 días por mes calendario.
- El trabajador contabiliza sólo trabajos `COMPLETED` y promedia únicamente las calificaciones disponibles.
- La actividad efectiva del cliente incluye registros `PENDING` y `COMPLETED`.
- Los registros `CANCELLED` se excluyen de todas las estadísticas del cliente.
- El gasto, las categorías y los trabajadores frecuentes del cliente se calculan sólo con registros `COMPLETED`.
- Los trabajadores frecuentes se ordenan por cantidad y se limitan a los cinco primeros.
- M06 no expone escritura pública; los módulos de contratación, calificación y pago alimentarán el read model internamente.

## Calificaciones y reputación

- Solo se puede calificar una contratación finalizada.
- Un usuario no puede calificar más de una vez la misma contratación.
- La calificación debe asociarse a la contratación, al usuario emisor y al usuario evaluado.
- Las calificaciones alimentan la reputación del trabajador y el ranking.

## Geolocalización

- El usuario debe estar autenticado para registrar ubicación.
- La ubicación puede asociarse a perfil o contratación según el flujo.
- El sistema debe registrar coordenadas y descripción de ubicación cuando corresponda.

## Reclamos e incidencias

- Los usuarios pueden registrar reclamos o incidencias asociados a servicios.
- El administrador debe poder visualizar, clasificar y gestionar reclamos.
- La resolución de un reclamo debe notificar al usuario involucrado.
- Los reclamos forman parte de reportes administrativos.
