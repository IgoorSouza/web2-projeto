import { useState } from "react";
import AuthForm from "../../components/auth-form";
import toast from "react-hot-toast";
import { AxiosError } from "axios";
import eye from "../../assets/eye.svg";
import eyeClosed from "../../assets/eye-closed.svg";
import { useAuth } from "../../hooks/use-auth";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [name, setName] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  async function createAccount() {
    try {
      await register(name, email, password);
      toast.success("Conta criada com sucesso!");
      navigate("/auth/login");
    } catch (error) {
      if (error instanceof AxiosError && error.status === 409) {
        toast.error(`O email ${email} já está em uso.`);
        return;
      }

      toast.error("Ocorreu um erro ao criar sua conta.");
    }
  }

  return (
    <AuthForm
      onSubmit={createAccount}
      text="Crie uma conta para usar a lista de desejos e receber notificações de promoções e descontos de jogos de seu interesse em seu email!"
      buttonText="Criar"
      linkText="Entrar"
      linkRoute="/auth/login"
    >
      <h1 className="text-3xl text-center mb-3">Criar conta</h1>

      <label htmlFor="name">Nome: </label>
      <input
        type="text"
        id="name"
        required
        className="border-1 border-black rounded-md mt-1 mb-3 p-2"
        onChange={(event) => setName(event.target.value)}
      />

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
          minLength={6}
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
