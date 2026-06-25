import { apiRequest } from '../../../shared/services/apiClient.js';

export function getMyProfile() {
  return apiRequest('/api/profiles/me');
}

export function createMyProfile(role, data) {
  return apiRequest(getProfileCollectionPath(role), {
    method: 'POST',
    body: data,
  });
}

export function updateMyProfile(role, data) {
  return apiRequest(getProfileItemPath(role), {
    method: 'PUT',
    body: data,
  });
}

function getProfileCollectionPath(role) {
  if (role === 'WORKER') {
    return '/api/profiles/trabajadores';
  }

  return '/api/profiles/clientes';
}

function getProfileItemPath(role) {
  if (role === 'WORKER') {
    return '/api/profiles/trabajadores/me';
  }

  return '/api/profiles/clientes/me';
}
