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

export const routePaths = {
  dashboard: '/',
  login: '/login',
  register: '/registro',
  forgotPassword: '/recuperar-contrasena',
  profiles: '/perfiles',
  search: '/busqueda',
  contracts: '/contrataciones',
};
