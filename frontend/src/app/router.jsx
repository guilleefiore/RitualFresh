import HomePage from '../modules/home/pages/HomePage.jsx';
import SearchPage from '../modules/search/pages/SearchPage.jsx';
import WorkerProfilePage from '../modules/profiles/pages/WorkerProfilePage.jsx';
import ContractsPage from '../modules/contracts/pages/ContractsPage.jsx';
import ChatPage from '../modules/chat/pages/ChatPage.jsx';
import HistoryPage from '../modules/history/pages/HistoryPage.jsx';
import NotificationsPage from '../modules/notifications/pages/NotificationsPage.jsx';
import PaymentsPage from '../modules/payments/pages/PaymentsPage.jsx';
import { navigationItems } from '../shared/constants/routes.js';
import { roleOptions } from '../shared/constants/roles.js';

export const viewComponents = {
  dashboard: HomePage,
  busqueda: SearchPage,
  perfil: WorkerProfilePage,
  contratacion: ContractsPage,
  chat: ChatPage,
  historial: HistoryPage,
  notificaciones: NotificationsPage,
  pagos: PaymentsPage,
};

export { navigationItems, roleOptions };
