import { apiPost } from '../../../shared/services/api.js';

export function login(credentials) {
  return apiPost('/api/auth/login', credentials);
}

export function register(payload) {
  return apiPost('/api/auth/register', payload);
}

export function requestPasswordReset(payload) {
  return apiPost('/api/auth/password-reset', payload);
}
