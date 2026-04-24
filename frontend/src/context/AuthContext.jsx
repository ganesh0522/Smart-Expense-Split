import { createContext, useState, useEffect } from "react";

export const AuthContext = createContext({
  token: null,
  login: () => {},
  logout: () => {},
});

export const AuthProvider = ({ children }) => {

  const [token, setToken] = useState(null);

  // 🔹 Load token on app start
  useEffect(() => {
    const savedToken = localStorage.getItem("token");
    if (savedToken) {
      setToken(savedToken);
    }
  }, []);

  // 🔹 Login
  const login = (newToken) => {
    localStorage.setItem("token", newToken);
    setToken(newToken);
  };

  // 🔹 Logout
  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};