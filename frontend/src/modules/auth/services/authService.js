import { apiRequest } from '../../../shared/services/apiClient.js';

export function registerUser(data) {
  return apiRequest('/api/users/register', {
    method: 'POST',
    body: data,
  });
}

export function loginUser(data) {
  return apiRequest('/api/users/login', {
    method: 'POST',
    body: data,
  });
}

export function requestPasswordReset(data) {
  return apiRequest('/api/users/password-reset', {
    method: 'POST',
    body: data,
  });
}

export function confirmPasswordReset(data) {
  return apiRequest('/api/users/password-reset/confirm', {
    method: 'POST',
    body: data,
  });
}

export function validateAccount(token) {
  return apiRequest(`/api/users/validation?token=${encodeURIComponent(token)}`);
}

export function resendAccountValidation(data) {
  return apiRequest('/api/users/validation/resend', {
    method: 'POST',
    body: data,
  });
}

export function logoutUser() {
  return apiRequest('/api/users/logout', {
    method: 'POST',
  });
}

export function deleteCurrentUser() {
  return apiRequest('/api/users/me', {
    method: 'DELETE',
  });
}
