import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/use-auth";
import user from "../assets/user.svg";

export default function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { authData, logout } = useAuth();

  const pages = [{ path: "/", label: "Buscar Jogos" }];
  const protectedPages = [
    ...pages,
    { path: "/wishlist", label: "Lista de Desejos" },
    { path: "/profile", label: "Perfil" },
  ];

  function handleLogout() {
    logout();

    const protectedPaths = protectedPages.map(({ path }) => path);
    if (protectedPaths.includes(location.pathname)) {
      navigate("/");
    }
  }

  return (
    <nav className="flex items-center justify-between bg-gradient-to-b from-zinc-900 to-zinc-800 text-white p-4 shadow-md px-16">
      <ul className="flex gap-8 items-center">
        {(authData ? protectedPages : pages).map((link) => (
          <li key={link.path}>
            <Link
              to={link.path}
              className={`text-lg hover:text-zinc-400 transition ${
                location.pathname === link.path ? "text-zinc-400 font-bold" : ""
              }`}
            >
              {link.label}
            </Link>
          </li>
        ))}
      </ul>

      <div className="flex items-center gap-x-3">
        <img src={user} className="w-[40px]" />

        {authData ? (
          <>
            <div className="text-right">
              <p className="text-sm font-semibold">{authData.name}</p>
              <p className="text-xs">{authData.email}</p>
            </div>

            <button
              onClick={handleLogout}
              className="bg-white text-black px-4 py-2 rounded-lg text-sm cursor-pointer font-semibold hover:bg-gray-300"
            >
              Logout
            </button>
          </>
        ) : (
          <Link
            to="/auth/login"
            className="bg-white text-black px-4 py-2 rounded-lg text-sm cursor-pointer font-semibold hover:bg-gray-300"
          >
            Login
          </Link>
        )}
      </div>
    </nav>
  );
}
