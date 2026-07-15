import { apiRequest } from '../../../shared/services/apiClient.js';

export function listUsers({ query = '', role = '', status = '', page = 0, size = 20, sort = 'createdAt', direction = 'desc' } = {}) {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    sort,
    direction,
  });

  if (query.trim()) params.set('query', query.trim());
  if (role) params.set('role', role);
  if (status) params.set('status', status);

  return apiRequest(`/api/admin/users?${params.toString()}`);
}

export function getUser(userId) {
  return apiRequest(`/api/admin/users/${userId}`);
}

export function updateUserStatus(userId, statusRequest) {
  return apiRequest(`/api/admin/users/${userId}/status`, {
    method: 'PATCH',
    body: statusRequest,
  });
}

export function getUserStatusHistory(userId, { page = 0, size = 10 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return apiRequest(`/api/admin/users/${userId}/status-history?${params.toString()}`);
}

export function getMetrics() {
  return apiRequest('/api/admin/metrics');
}
