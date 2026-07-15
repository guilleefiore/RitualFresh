import { useEffect, useState } from 'react';
import { getMetrics, listUsers } from '../services/adminService.js';

const EMPTY_PAGE = {
  content: [],
  page: 0,
  size: 20,
  totalElements: 0,
  totalPages: 0,
};

export function useAdminUsers(filters = {}, refreshKey = 0) {
  const {
    query = '',
    role = '',
    status = '',
    page = 0,
    size = 20,
    sort = 'createdAt',
    direction = 'desc',
  } = filters;
  const [result, setResult] = useState(EMPTY_PAGE);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function fetchUsers() {
      setIsLoading(true);
      setError('');

      try {
        const data = await listUsers({ query, role, status, page, size, sort, direction });
        if (!cancelled) setResult(data || EMPTY_PAGE);
      } catch (requestError) {
        if (!cancelled) {
          setResult(EMPTY_PAGE);
          setError(requestError.message || 'No se pudieron cargar los usuarios.');
        }
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }

    fetchUsers();
    return () => { cancelled = true; };
  }, [query, role, status, page, size, sort, direction, refreshKey]);

  return { result, isLoading, error };
}

export function useAdminMetrics(refreshKey = 0) {
  const [metrics, setMetrics] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function fetchMetrics() {
      setIsLoading(true);
      setError('');

      try {
        const data = await getMetrics();
        if (!cancelled) setMetrics(data);
      } catch (requestError) {
        if (!cancelled) {
          setMetrics(null);
          setError(requestError.message || 'No se pudieron cargar las métricas.');
        }
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }

    fetchMetrics();
    return () => { cancelled = true; };
  }, [refreshKey]);

  return { metrics, isLoading, error };
}
