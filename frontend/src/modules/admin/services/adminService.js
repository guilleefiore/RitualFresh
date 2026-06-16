import { apiRequest } from '../../../shared/services/apiClient.js';

// Obtiene la lista de todos los usuarios
export function listUsers() {
  return apiRequest('/api/admin/users');
}

// Obtiene los datos de un usuario específico
export function getUser(userId) {
  return apiRequest(`/api/admin/users/${userId}`);
}

// Cambia el estado de cuenta de un usuario
export function updateUserStatus(userId, statusRequest) {
  return apiRequest(`/api/admin/users/${userId}/status`, {
    method: 'PATCH',
    body: statusRequest,
  });
}

// Obtiene métricas de usuarios (total, por rol, por estado)
export function getMetrics() {
  return apiRequest('/api/admin/metrics');
}
