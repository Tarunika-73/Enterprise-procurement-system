import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { STORAGE_KEYS } from '../utils/constants';

const AuthContext = createContext(null);

const safeParse = (value) => {
  if (!value) return null;
  try {
    return JSON.parse(value);
  } catch {
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => safeParse(localStorage.getItem(STORAGE_KEYS.USER)));
  const [token, setToken] = useState(() => localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN));
  const [isLoading, setIsLoading] = useState(false);

  const isAuthenticated = Boolean(user && token);

  const loginUser = useCallback((authPayload) => {
    const { user: nextUser, token: nextToken } = authPayload;
    setUser(nextUser);
    setToken(nextToken);
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, nextToken);
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(nextUser));
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
  }, []);

  const value = useMemo(
    () => ({
      user,
      userRole: user?.role ?? null,
      token,
      isAuthenticated,
      isLoading,
      setIsLoading,
      loginUser,
      logout,
    }),
    [user, token, isAuthenticated, isLoading, loginUser, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
