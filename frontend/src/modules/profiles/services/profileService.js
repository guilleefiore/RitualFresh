import { API_BASE_URL, apiRequest, handleUnauthorizedError, setCsrfHeaderIfAvailable } from '../../../shared/services/apiClient.js';

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

export async function uploadPhoto(file) {
  const formData = new FormData();
  formData.append('file', file);
  const headers = new Headers();
  setCsrfHeaderIfAvailable(headers, 'POST');

  const response = await fetch(`${API_BASE_URL}/api/upload`, {
    method: 'POST',
    credentials: 'include',
    headers,
    body: formData,
  });

  if (!response.ok) {
    const data = await response.json().catch(() => null);
    const error = new Error(data?.message || 'Error al subir la imagen.');
    error.status = response.status;
    error.payload = data;
    handleUnauthorizedError(error);
    throw error;
  }

  return response.json();
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
