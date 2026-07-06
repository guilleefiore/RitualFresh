import { createContext, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { deleteCurrentUser, getCurrentSession, loginUser, logoutUser } from '../services/authService.js';
import { setApiUnauthorizedHandler } from '../../../shared/services/apiClient.js';

export const AuthContext = createContext(null);

function getRoleFromUser(user) {
  return user?.role || null;
}

function getAccountStatusFromUser(user) {
  return user?.accountStatus || null;
}

export function AuthProvider({ children }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [sessionExpiresAt, setSessionExpiresAt] = useState(null);
  const [isAuthReady, setIsAuthReady] = useState(false);

  const role = getRoleFromUser(user);
  const accountStatus = getAccountStatusFromUser(user);
  const isAuthenticated = Boolean(user);

  function clearAuthState() {
    setUser(null);
    setSessionExpiresAt(null);
  }

  useEffect(() => {
    let isMounted = true;

    async function bootstrapSession() {
      try {
        const response = await getCurrentSession();
        if (isMounted) {
          setUser(response.user);
          setSessionExpiresAt(response.sessionExpiresAt || null);
        }
      } catch (error) {
        if (error.status !== 401) {
          // La sesión no es crítica para bloquear la carga; sólo se ignoran errores de arranque.
        }
      } finally {
        if (isMounted) {
          setIsAuthReady(true);
        }
      }
    }

    bootstrapSession();

    setApiUnauthorizedHandler(() => {
      clearAuthState();
      navigate('/login', {
        replace: true,
        state: { message: 'La sesion expiro. Debe iniciar sesion nuevamente.' },
      });
    });

    return () => {
      isMounted = false;
      setApiUnauthorizedHandler(null);
    };
  }, [navigate]);

  async function login(credentials) {
    const response = await loginUser(credentials);
    setUser(response.user);
    setSessionExpiresAt(response.sessionExpiresAt || null);
    return response;
  }

  async function logout() {
    try {
      await logoutUser();
    } finally {
      clearAuthState();
    }
  }

  async function refreshSession() {
    try {
      const response = await getCurrentSession();
      setUser(response.user);
      setSessionExpiresAt(response.sessionExpiresAt || null);
    } catch {
      clearAuthState();
    }
  }

  async function deleteAccount() {
    try {
      const response = await deleteCurrentUser();
      clearAuthState();
      return response;
    } catch (error) {
      if (error.status === 401) {
        clearAuthState();
      }
      throw error;
    }
  }

  const value = useMemo(
    () => ({
      user,
      role,
      accountStatus,
      sessionExpiresAt,
      isAuthenticated,
      login,
      logout,
      deleteAccount,
      clearAuthState,
      refreshSession,
      isAuthReady,
    }),
    [user, role, accountStatus, sessionExpiresAt, isAuthenticated, isAuthReady]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
