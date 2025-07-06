import axios from "../lib/axios";
import { useEffect, useState } from "react";
import { useAuth } from "../hooks/use-auth";
import toast from "react-hot-toast";
import { GameSearch } from "../types/game-search";

interface GameSearchHistoryProps {
  onClose: () => void;
}

export default function GameSearchHistory({ onClose }: GameSearchHistoryProps) {
  const [searches, setSearches] = useState<GameSearch[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const { authData } = useAuth();

  useEffect(() => {
    async function getUserGameSearches() {
      try {
        const response = await axios.get("/user/searches", {
          headers: { Authorization: `Bearer ${authData!.token}` },
        });

        setSearches(response.data);
        setLoading(false);
      } catch (error) {
        console.error(error);
        toast.error(
          "Ocorreu um erro ao buscar seu histórico de pesquisa de jogos."
        );
        onClose();
      }
    }

    getUserGameSearches();
  }, []);

  function formatDate(date: Date) {
    return new Date(date).toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  }

  return (
    <>
      <div
        className="fixed inset-0 bg-black bg-opacity-80 z-40 opacity-70"
        onClick={onClose}
      />

      <div className="fixed inset-0 flex items-center justify-center z-50">
        <div className="bg-white text-black p-6 rounded-lg shadow-lg max-w-md w-full relative">
          <h2 className="text-2xl text-center font-semibold mb-4">
            Histórico de Pesquisa
          </h2>

          {loading ? (
            <p className="text-center text-lg">Carregando histórico...</p>
          ) : searches.length > 0 ? (
            <ul className="max-h-80 overflow-y-auto space-y-3 p-2">
              {searches.map((item, index) => (
                <li key={index} className="border-b pb-2">
                  <p className="font-medium">{item.gameName}</p>
                  <div className="text-sm text-gray-600 flex justify-between">
                    <span>
                      {item.platform === "STEAM" ? "Steam" : "Epic Games Store"}
                    </span>
                    <span>{formatDate(item.date)}</span>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-600">Nenhuma pesquisa encontrada.</p>
          )}

          <button
            onClick={onClose}
            className="mt-6 w-full bg-black text-white py-2 rounded-md hover:bg-zinc-800 cursor-pointer"
          >
            Fechar
          </button>
        </div>
      </div>
    </>
  );
}
