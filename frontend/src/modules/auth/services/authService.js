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

export function getCurrentSession() {
  return apiRequest('/api/users/me', {
    method: 'GET',
    skipUnauthorizedHandler: true,
  });
}

export function startGoogleLogin() {
  window.location.assign(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/oauth2/authorization/google`);
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

export function updateUserRole(role) {
  return apiRequest('/api/users/me/role', {
    method: 'PUT',
    body: { role },
  });
}

export function deleteCurrentUser() {
  return apiRequest('/api/users/me', {
    method: 'DELETE',
  });
}
