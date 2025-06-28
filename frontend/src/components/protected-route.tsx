import { PropsWithChildren } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/use-auth";

export default function ProtectedRoute({ children }: PropsWithChildren) {
  const { authData } = useAuth();
  return authData ? children : <Navigate to="/" replace />;
}
