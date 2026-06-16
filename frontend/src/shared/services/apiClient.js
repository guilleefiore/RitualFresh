const DEFAULT_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

let unauthorizedHandler = null;

export function setApiUnauthorizedHandler(handler) {
  unauthorizedHandler = typeof handler === 'function' ? handler : null;
}

export async function apiRequest(path, options = {}) {
  const { body, headers, ...restOptions } = options;
  const requestHeaders = new Headers(headers || {});

  if (body !== undefined && !requestHeaders.has('Content-Type')) {
    requestHeaders.set('Content-Type', 'application/json');
  }

  const response = await fetch(`${DEFAULT_BASE_URL}${path}`, {
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

    if (response.status === 401 && unauthorizedHandler) {
      unauthorizedHandler(error);
    }

    throw error;
  }

  return responseData;
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
