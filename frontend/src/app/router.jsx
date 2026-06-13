import HomePage from '../modules/home/pages/HomePage.jsx';
import SearchPage from '../modules/search/pages/SearchPage.jsx';
import WorkerProfilePage from '../modules/profiles/pages/WorkerProfilePage.jsx';
import ServiceRequestPage from '../modules/contracts/pages/ServiceRequestPage.jsx';
import ChatPage from '../modules/chat/pages/ChatPage.jsx';
import HistoryPage from '../modules/history/pages/HistoryPage.jsx';
import NotificationsPage from '../modules/notifications/pages/NotificationsPage.jsx';
import PaymentsPage from '../modules/payments/pages/PaymentsPage.jsx';
import { navigationItems } from '../shared/constants/routes.js';
import { roleOptions } from '../shared/constants/roles.js';

export const viewComponents = {
  dashboard: HomePage,
  search: SearchPage,
  profile: WorkerProfilePage,
  contracts: ServiceRequestPage,
  chat: ChatPage,
  history: HistoryPage,
  notifications: NotificationsPage,
  payments: PaymentsPage,
};

export { navigationItems, roleOptions };
