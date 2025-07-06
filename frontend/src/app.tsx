import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Games from "./pages/games";
import Register from "./pages/auth/register";
import Login from "./pages/auth/login";
import { Toaster } from "react-hot-toast";
import { AuthProvider } from "./context/auth-context";
import Wishlist from "./pages/wishlist";
import ProtectedRoute from "./components/protected-route";
import VerifyEmail from "./pages/auth/verify-email";
import Navbar from "./components/nav-bar";
import Profile from "./pages/profile";
import Users from "./pages/users";

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Toaster
          position="bottom-right"
          reverseOrder
          toastOptions={{ duration: 3000 }}
        />
        <Navbar />

        <Routes>
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Games />
              </ProtectedRoute>
            }
          />
          <Route
            path="/wishlist"
            element={
              <ProtectedRoute>
                <Wishlist />
              </ProtectedRoute>
            }
          />
          <Route path="/auth/register" element={<Register />} />
          <Route path="/auth/login" element={<Login />} />
          <Route
            path="/auth/verify"
            element={
              <ProtectedRoute>
                <VerifyEmail />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          <Route
            path="/users"
            element={
              <ProtectedRoute>
                <Users />
              </ProtectedRoute>
            }
          />
          <Route
            path="*"
            element={
              <ProtectedRoute>
                <Games />
              </ProtectedRoute>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
