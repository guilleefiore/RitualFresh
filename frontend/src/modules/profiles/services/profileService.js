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

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export async function uploadPhoto(file) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${API_BASE}/api/upload`, {
    method: 'POST',
    credentials: 'include',
    body: formData,
  });

  if (!response.ok) {
    const data = await response.json().catch(() => null);
    throw new Error(data?.message || 'Error al subir la imagen.');
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
