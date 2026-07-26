import { createContext, useCallback, useContext, useMemo, useState } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const isAuthenticated = Boolean(user && token);

  const loginUser = useCallback((authPayload) => {
    const { user: nextUser, token: nextToken } = authPayload;
    setUser(nextUser);
    setToken(nextToken);
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
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
