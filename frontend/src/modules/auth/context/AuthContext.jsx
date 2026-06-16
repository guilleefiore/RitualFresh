import { createContext, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { deleteCurrentUser, loginUser, logoutUser } from '../services/authService.js';
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

  const role = getRoleFromUser(user);
  const accountStatus = getAccountStatusFromUser(user);
  const isAuthenticated = Boolean(user);

  function clearAuthState() {
    setUser(null);
    setSessionExpiresAt(null);
  }

  useEffect(() => {
    setApiUnauthorizedHandler(() => {
      clearAuthState();
      navigate('/login', {
        replace: true,
        state: { message: 'La sesion expiro. Debe iniciar sesion nuevamente.' },
      });
    });

    return () => {
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
    }),
    [user, role, accountStatus, sessionExpiresAt, isAuthenticated]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
