import { apiRequest } from '../../../shared/services/apiClient.js';

export function getServiceHistory({ status, from, to, page = 0, size = 20 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (status) params.set('status', status);
  if (from) params.set('from', from);
  if (to) params.set('to', to);
  return apiRequest(`/api/history/services?${params.toString()}`);
}

export function getMyStatistics(role, period) {
  const resource = role === 'WORKER' ? 'workers' : 'clients';
  const params = new URLSearchParams({ period });
  return apiRequest(`/api/statistics/${resource}/me?${params.toString()}`);
}
