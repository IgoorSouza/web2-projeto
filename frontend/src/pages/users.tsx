import { useEffect, useState } from "react";
import { useAuth } from "../hooks/use-auth";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { User } from "../types/user";
import axios from "../lib/axios";
import UsersTable from "../components/users-table";

export default function Users() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const { authData } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!authData?.roles.includes("SUPER_ADMIN")) {
      toast.error("Você não possui acesso a esta página.");
      navigate("/");
      return;
    }

    async function getUsers() {
      try {
        const response = await axios.get("/user/all", {
          headers: { Authorization: `Bearer ${authData!.token}` },
        });
        setUsers(response.data);
      } catch (error) {
        console.error(error);
        toast.error("Ocorreu um erro ao buscar os usuários.");
      } finally {
        setLoading(false);
      }
    }

    getUsers();
  }, [authData]);

  return (
    <div className="flex flex-col gap-y-5 items-center mb-5 px-4">
      <h1 className="text-white text-3xl mt-5">Usuários cadastrados</h1>

      {loading ? (
        <p className="text-white text-xl">Carregando usuários...</p>
      ) : (
        <div className="overflow-x-auto w-full max-w-6xl">
          <UsersTable users={users} setUsers={setUsers} />
        </div>
      )}
    </div>
  );
}
