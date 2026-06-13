export const navigationItems = [
  { id: 'dashboard', module: 'RF', label: 'Inicio', icon: 'dashboard' },
  { id: 'search', module: 'M03', label: 'Búsqueda', icon: 'manage_search' },
  { id: 'profile', module: 'M02', label: 'Perfil', icon: 'badge' },
  { id: 'contracts', module: 'M04', label: 'Contratación', icon: 'assignment_turned_in' },
  { id: 'chat', module: 'M05', label: 'Chat', icon: 'forum' },
  { id: 'history', module: 'M06', label: 'Historial', icon: 'monitoring' },
  { id: 'notifications', module: 'M08', label: 'Notificaciones', icon: 'notifications' },
  { id: 'payments', module: 'M09/M10', label: 'Pagos y ubicación', icon: 'payments' },
];

export const routePaths = {
  dashboard: '/',
  login: '/login',
  register: '/register',
  forgotPassword: '/forgot-password',
  profiles: '/profiles',
  search: '/search',
  contracts: '/contracts',
};
