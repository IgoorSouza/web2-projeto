import { createContext, PropsWithChildren, useEffect, useState } from "react";
import axios from "../lib/axios";

interface AuthData {
  name: string;
  email: string;
  token: string;
  emailVerified: boolean;
  notificationsEnabled: boolean;
}

interface AuthContextType {
  authData: AuthData | null;
  register: (name: string, email: string, password: string) => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  updateAuthData: (newAuthData: AuthData) => void;
  loading: boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined
);

export function AuthProvider({ children }: PropsWithChildren) {
  const [authData, setAuthData] = useState<AuthData | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    const authData = localStorage.getItem("authData");

    if (authData) {
      setAuthData(JSON.parse(authData));
    }
  }, []);

  async function register(name: string, email: string, password: string) {
    setLoading(true);

    try {
      await axios.post("/auth/register", { name, email, password });
    } catch (error) {
      console.error(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }

  async function login(email: string, password: string) {
    setLoading(true);

    try {
      const { data: authData }: { data: AuthData } = await axios.post(
        "/auth/login",
        { email, password }
      );

      setAuthData(authData);
      localStorage.setItem("authData", JSON.stringify(authData));
    } catch (error) {
      console.error(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    setAuthData(null);
    localStorage.removeItem("authData");
  }

  function updateAuthData(newAuthData: AuthData) {
    setAuthData(newAuthData);
    localStorage.setItem("authData", JSON.stringify(newAuthData));
  }

  return (
    <AuthContext.Provider
      value={{ authData, register, login, logout, updateAuthData, loading }}
    >
      {children}
    </AuthContext.Provider>
  );
}
