export const navigationItems = [
  { id: 'dashboard', module: 'RF', label: 'Inicio', icon: 'dashboard' },
  { id: 'busqueda', module: 'M03', label: 'Búsqueda', icon: 'manage_search' },
  { id: 'perfil', module: 'M02', label: 'Perfil', icon: 'badge' },
  { id: 'contratacion', module: 'M04', label: 'Contratación', icon: 'assignment_turned_in' },
  { id: 'chat', module: 'M05', label: 'Chat', icon: 'forum' },
  { id: 'historial', module: 'M06', label: 'Historial', icon: 'monitoring' },
  { id: 'notificaciones', module: 'M08', label: 'Notificaciones', icon: 'notifications' },
  { id: 'pagos', module: 'M09/M10', label: 'Pagos y ubicación', icon: 'payments' },
];

export const roleOptions = ['Cliente', 'Trabajador', 'Administrador'];

export const workers = [
  {
    id: 1,
    name: 'Sofía Benítez',
    initials: 'SB',
    specialty: 'Limpieza integral del hogar',
    zone: 'Ciudad de Mendoza',
    availability: 'Hoy 16:00',
    rating: '4.9',
    completed: 128,
    price: '$9.500',
    response: '12 min',
    verified: true,
    tags: ['Limpieza profunda', 'Cocina', 'Baños'],
    accent: 'teal',
  },
  {
    id: 2,
    name: 'Martín Aguilar',
    initials: 'MA',
    specialty: 'Mantenimiento y arreglos menores',
    zone: 'Godoy Cruz',
    availability: 'Mañana 09:30',
    rating: '4.8',
    completed: 96,
    price: '$11.800',
    response: '20 min',
    verified: true,
    tags: ['Plomería básica', 'Electricidad', 'Pintura'],
    accent: 'amber',
  },
  {
    id: 3,
    name: 'Equipo Andes Clean',
    initials: 'AC',
    specialty: 'Empresa de limpieza programada',
    zone: 'Guaymallén y Capital',
    availability: 'Viernes 11:00',
    rating: '4.7',
    completed: 214,
    price: '$18.400',
    response: '35 min',
    verified: true,
    tags: ['Oficinas', 'Hogar', 'Servicio mensual'],
    accent: 'blue',
  },
];

export const kpis = [
  {
    label: 'Servicios activos',
    value: '8',
    detail: '3 confirmados para esta semana',
    icon: 'event_available',
    tone: 'blue',
  },
  {
    label: 'Calificación promedio',
    value: '4.8',
    detail: 'Basada en contrataciones finalizadas',
    icon: 'star',
    tone: 'amber',
  },
  {
    label: 'Pagos trazados',
    value: '$184k',
    detail: 'Procesados por checkout externo',
    icon: 'account_balance_wallet',
    tone: 'teal',
  },
  {
    label: 'Notificaciones',
    value: '12',
    detail: '5 pendientes de lectura',
    icon: 'notifications_active',
    tone: 'red',
  },
];

export const serviceRequests = [
  {
    code: 'US12-M04-RF04',
    service: 'Limpieza profunda de departamento',
    worker: 'Sofía Benítez',
    date: '10/06/2026',
    status: 'Confirmado',
    payment: 'Pago pendiente',
  },
  {
    code: 'US03-M03-RF01',
    service: 'Mantenimiento de baño',
    worker: 'Martín Aguilar',
    date: '12/06/2026',
    status: 'Solicitud enviada',
    payment: 'A confirmar',
  },
  {
    code: 'US20-M06-RF02',
    service: 'Limpieza semanal',
    worker: 'Equipo Andes Clean',
    date: '14/06/2026',
    status: 'Programado',
    payment: 'Checkout externo',
  },
];

export const notifications = [
  {
    title: 'Solicitud aceptada',
    text: 'Sofía Benítez confirmó el servicio para el miércoles a las 16:00.',
    time: 'Hace 8 min',
    icon: 'task_alt',
    tone: 'teal',
    unread: true,
  },
  {
    title: 'Pago pendiente',
    text: 'El checkout externo está listo para completar la reserva.',
    time: 'Hace 25 min',
    icon: 'payments',
    tone: 'amber',
    unread: true,
  },
  {
    title: 'Nueva calificación recibida',
    text: 'La contratación finalizada obtuvo una valoración de 5 estrellas.',
    time: 'Ayer',
    icon: 'reviews',
    tone: 'blue',
    unread: false,
  },
];

export const conversations = [
  {
    name: 'Sofía Benítez',
    preview: 'Perfecto, llevo los insumos indicados.',
    time: '10:42',
    active: true,
  },
  {
    name: 'Martín Aguilar',
    preview: 'Puedo revisar la pérdida antes del presupuesto final.',
    time: '09:18',
    active: false,
  },
  {
    name: 'Soporte RitualFresh',
    preview: 'Tu reclamo quedó registrado con trazabilidad.',
    time: 'Ayer',
    active: false,
  },
];

export const messages = [
  {
    from: 'worker',
    text: 'Hola, Guillermina. Vi la solicitud para limpieza profunda del departamento.',
    time: '10:31',
  },
  {
    from: 'client',
    text: 'Sí, necesito cocina, baño y ventanas. ¿Podés el miércoles a las 16?',
    time: '10:33',
  },
  {
    from: 'worker',
    text: 'Perfecto. Llevo los insumos indicados y confirmo la duración estimada de 3 horas.',
    time: '10:42',
  },
];

export const historyRows = [
  ['RF-2026-014', 'Limpieza profunda', 'Finalizado', '$9.500', '5.0'],
  ['RF-2026-013', 'Mantenimiento baño', 'Cancelado', '$0', '-'],
  ['RF-2026-012', 'Limpieza semanal', 'Finalizado', '$18.400', '4.8'],
  ['RF-2026-011', 'Cocina y vidrios', 'Finalizado', '$7.900', '4.9'],
];
