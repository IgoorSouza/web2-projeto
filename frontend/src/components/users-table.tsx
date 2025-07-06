import { useState } from "react";
import { User } from "../types/user";
import axios from "../lib/axios";
import { useAuth } from "../hooks/use-auth";
import toast from "react-hot-toast";

interface Props {
  users: User[];
  setUsers: React.Dispatch<React.SetStateAction<User[]>>;
}

export default function UsersTable({ users, setUsers }: Props) {
  const [loading, setLoading] = useState<boolean>(false);
  const { authData } = useAuth();

  async function setUserRoles(userId: string, roles: string[]) {
    try {
      setLoading(true);

      await axios.put(
        "/user/roles",
        { userId, roles },
        { headers: { Authorization: `Bearer ${authData!.token}` } }
      );

      setUsers((prev) =>
        prev.map((user) => (user.id === userId ? { ...user, roles } : user))
      );

      toast.success("Permissões do usuário alteradas com sucesso!");
    } catch (error) {
      console.error(error);
      toast.error("Ocorreu um erro ao alterar as permissões do usuário.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <table
      className={`w-full text-white border border-zinc-700 ${
        loading && "opacity-70"
      }`}
    >
      <thead className="bg-zinc-800">
        <tr>
          <th className="text-left px-4 py-3 border-b border-zinc-700">Nome</th>
          <th className="text-left px-4 py-3 border-b border-zinc-700">
            Email
          </th>
          <th className="text-left px-4 py-3 border-b border-zinc-700">
            Verificado
          </th>
          <th className="text-left px-4 py-3 border-b border-zinc-700">
            Roles
          </th>
          <th className="text-left px-4 py-3 border-b border-zinc-700">
            Ações
          </th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id} className="odd:bg-zinc-900 even:bg-zinc-800">
            <td className="px-4 py-2">{user.name}</td>
            <td className="px-4 py-2">{user.email}</td>
            <td className="px-4 py-2">{user.emailVerified ? "Sim" : "Não"}</td>
            <td className="px-4 py-2">{user.roles.join(", ")}</td>
            <td className="px-4 py-2">
              {user.roles.includes("SUPER_ADMIN") ? (
                <p>Nenhuma ação disponível.</p>
              ) : (
                <button
                  type="button"
                  disabled={loading}
                  className="text-blue-400 hover:underline mr-2 cursor-pointer"
                  onClick={() => {
                    const roles = user.roles.includes("ADMIN")
                      ? ["USER"]
                      : ["ADMIN", "USER"];

                    setUserRoles(user.id, roles);
                  }}
                >
                  {user.roles.includes("ADMIN")
                    ? "Remover permissão de ADMIN"
                    : "Conceder permissão de ADMIN"}
                </button>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
