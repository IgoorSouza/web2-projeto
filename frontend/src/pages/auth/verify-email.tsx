import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "../../lib/axios";
import toast from "react-hot-toast";
import { useAuth } from "../../hooks/use-auth";
import { AxiosError } from "axios";

export default function VerifyEmail() {
  const [loading, setLoading] = useState<boolean>(true);
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { authData, updateAuthData } = useAuth();

  useEffect(() => {
    if (authData!.emailVerified) {
      toast.error("Você já verificou seu email.");
      navigate("/");
      return;
    }

    const token = params.get("token");

    if (!token) {
      navigate("/");
      return;
    }

    async function verify() {
      try {
        await axios.post(`/auth/verify?token=${token}`, {}, {
          headers: { authorization: `Bearer ${token}` },
        });

        updateAuthData({ ...authData!, emailVerified: true });
        toast.success("Email verificado com sucesso!");
        navigate("/");
      } catch (error) {
        if (error instanceof AxiosError && error.status === 401) {
          toast.error("Link de verificação inválido.");
          return;
        }

        if (error instanceof AxiosError && error.status === 409) {
          toast.error("Você já verificou seu email.");
          return;
        }

        console.error(error);
        toast.error("Ocorreu um erro ao verificar seu email.");
      } finally {
        setLoading(false);
      }
    }

    verify();
  }, []);

  return (
    <div className="mx-auto mt-32 text-white text-center bg-zinc-900 border border-zinc-700 rounded-2xl p-10 max-w-xl">
      <h1 className="text-3xl font-bold mb-4">
        {loading ? "Verificando seu email..." : "Erro ao verificar."}
      </h1>

      <p className="text-lg text-gray-300">
        {loading
          ? "Isso pode levar alguns segundos..."
          : "Ocorreu um erro ao verificar seu email. Por favor, solicite a verificação novamente."}
      </p>
    </div>
  );
}
