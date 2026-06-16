import { useState, useEffect } from 'react';
import { listUsers, getMetrics } from '../services/adminService.js';

// Hook para obtener la lista de usuarios
export function useAdminUsers() {
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setIsLoading(true);
        const data = await listUsers();
        setUsers(data || []);
        setError(null);
      } catch (err) {
        setError(err.message || 'Error al cargar usuarios');
        setUsers([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUsers();
  }, []);

  return { users, isLoading, error };
}

// Hook para obtener métricas de usuarios
export function useAdminMetrics() {
  const [metrics, setMetrics] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        setIsLoading(true);
        const data = await getMetrics();
        setMetrics(data);
        setError(null);
      } catch (err) {
        setError(err.message || 'Error al cargar métricas');
        setMetrics(null);
      } finally {
        setIsLoading(false);
      }
    };

    fetchMetrics();
  }, []);

  return { metrics, isLoading, error };
}
