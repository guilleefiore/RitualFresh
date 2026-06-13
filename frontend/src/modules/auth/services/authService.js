import { apiPost } from '../../../shared/services/api.js';

export function login(credentials) {
  return apiPost('/api/users/login', credentials);
}

export function register(payload) {
  return apiPost('/api/users/register', payload);
}

export function requestPasswordReset(payload) {
  return apiPost('/api/users/password-reset', payload);
}
