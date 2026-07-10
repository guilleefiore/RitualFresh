export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

let unauthorizedHandler = null;

export function setApiUnauthorizedHandler(handler) {
  unauthorizedHandler = typeof handler === 'function' ? handler : null;
}

export async function apiRequest(path, options = {}) {
  const { body, headers, skipUnauthorizedHandler = false, ...restOptions } = options;
  const requestHeaders = new Headers(headers || {});
  const method = (restOptions.method || 'GET').toUpperCase();

  if (body !== undefined && !requestHeaders.has('Content-Type')) {
    requestHeaders.set('Content-Type', 'application/json');
  }

  setCsrfHeaderIfAvailable(requestHeaders, method);

  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: 'include',
    headers: requestHeaders,
    body: body !== undefined ? JSON.stringify(body) : undefined,
    ...restOptions,
  });

  const responseData = await parseResponseBody(response);

  if (!response.ok) {
    const error = new Error(responseData?.message || `Request failed with status ${response.status}`);
    error.status = response.status;
    error.payload = responseData;

    handleUnauthorizedError(error, skipUnauthorizedHandler);

    throw error;
  }

  return responseData;
}

export function getAssetUrl(path) {
  if (!path || path.startsWith('http')) {
    return path;
  }

  return `${API_BASE_URL}${path}`;
}

export function setCsrfHeaderIfAvailable(headers, method = 'GET') {
  if (['GET', 'HEAD', 'OPTIONS', 'TRACE'].includes(method.toUpperCase()) || headers.has('X-XSRF-TOKEN')) {
    return;
  }

  const token = getCookieValue('XSRF-TOKEN');
  if (token) {
    headers.set('X-XSRF-TOKEN', token);
  }
}

export function handleUnauthorizedError(error, skipUnauthorizedHandler = false) {
  if (error?.status === 401 && unauthorizedHandler && !skipUnauthorizedHandler) {
    unauthorizedHandler(error);
  }
}

function getCookieValue(name) {
  return document.cookie
    .split(';')
    .map((part) => part.trim())
    .find((part) => part.startsWith(`${name}=`))
    ?.slice(name.length + 1) || '';
}

async function parseResponseBody(response) {
  if (response.status === 204) {
    return null;
  }

  const contentLength = response.headers.get('content-length');
  if (contentLength === '0') {
    return null;
  }

  const contentType = response.headers.get('content-type') || '';
  if (!contentType.includes('application/json')) {
    const text = await response.text();
    return text ? { message: text } : null;
  }

  return response.json().catch(() => null);
}
