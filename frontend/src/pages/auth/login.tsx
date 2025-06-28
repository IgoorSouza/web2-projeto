import { useEffect, useState } from "react";
import AuthForm from "../../components/auth-form";
import toast from "react-hot-toast";
import { AxiosError } from "axios";
import eye from "../../assets/eye.svg";
import eyeClosed from "../../assets/eye-closed.svg";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../../hooks/use-auth";

export default function Login() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const { login, logout } = useAuth();
  const navigate = useNavigate();
  const [params] = useSearchParams();

  useEffect(() => {
    const expired = params.get("expired");

    if (expired) {
      logout();
      toast.error("Sua sessão expirou. Por favor, faça o login novamente.");
    }
  }, [params]);

  async function handleLogin() {
    try {
      await login(email, password);
      toast.success("Login realizado com sucesso!");
      navigate("/");
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.status === 404) {
          toast.error("Usuário não encontrado.");
          return;
        }

        if (
          error.status === 400 &&
          error.response?.data === "Wrong password."
        ) {
          toast.error("Senha incorreta.");
          return;
        }
      }

      toast.error("Ocorreu um erro ao fazer login.");
    }
  }

  return (
    <AuthForm
      onSubmit={handleLogin}
      text="Faça login para usar a lista de desejos e receber notificações de promoções e descontos de jogos de seu interesse em seu email!"
      buttonText="Entrar"
      linkText="Criar conta"
      linkRoute="/auth/register"
    >
      <h1 className="text-3xl text-center mb-3">Login</h1>

      <label htmlFor="email">Email: </label>
      <input
        type="email"
        id="email"
        required
        className="border-1 border-black rounded-md mt-1 mb-3 p-2"
        onChange={(event) => setEmail(event.target.value)}
      />

      <label htmlFor="password">Senha: </label>
      <div className="flex items-center justify-between border-1 border-black rounded-md mt-1 mb-4">
        <input
          type={showPassword ? "text" : "password"}
          id="password"
          required
          min={6}
          className="w-full p-2"
          onChange={(event) => setPassword(event.target.value)}
        />
        <img
          src={showPassword ? eyeClosed : eye}
          className="bg-gray-300 rounded-r-md p-2 cursor-pointer"
          onClick={() => setShowPassword((showPassword) => !showPassword)}
        />
      </div>
    </AuthForm>
  );
}
