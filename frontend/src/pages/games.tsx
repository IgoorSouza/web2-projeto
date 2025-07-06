import { FormEvent, useState } from "react";
import axios from "../lib/axios";
import { Game } from "../types/game";
import toast from "react-hot-toast";
import { useAuth } from "../hooks/use-auth";
import GameCard from "../components/game-card";
import { AxiosError } from "axios";

export default function Games() {
  const [games, setGames] = useState<Game[]>();
  const [search, setSearch] = useState<string>("");
  const [gameStore, setGameStore] = useState<string>("steam");
  const [loading, setLoading] = useState<boolean>(false);
  const [searchedGameStore, setSearchedGameStore] = useState<
    "Steam" | "Epic Games Store"
  >("Steam");
  const { authData } = useAuth();

  async function getGames(event: FormEvent<HTMLFormElement>) {
    try {
      event.preventDefault();
      if (search.trim() === "") return;

      setSearchedGameStore(
        gameStore === "steam" ? "Steam" : "Epic Games Store"
      );
      setLoading(true);

      const response = await axios.get(
        `/games/${gameStore}?gameName=${search}`,
        {
          headers: {
            Authorization: `Bearer ${authData!.token}`,
          },
        }
      );

      setGames(response.data);
    } catch (error) {
      console.error(error);
      toast.error(
        `Ocorreu um erro ao buscar os jogos da ${
          gameStore === "steam" ? "Steam" : "Epic Games Store"
        }.`
      );
    } finally {
      setLoading(false);
    }
  }

  async function addGameToWishlist(game: Game) {
    try {
      setLoading(true);

      await axios.post(
        "/wishlist",
        {
          platformIdentifier: game.identifier,
          platform: gameStore.toUpperCase(),
        },
        {
          headers: {
            Authorization: `Bearer ${authData!.token}`,
          },
        }
      );

      toast.success(`${game.title} foi adicionado à sua lista de desejos!`);
    } catch (error) {
      if (error instanceof AxiosError && error.status === 409) {
        toast.error(`${game.title} já está na sua lista de desejos.`);
        return;
      }

      console.error(error);
      toast.error(
        `Ocorreu um erro ao adicionar ${game.title} à sua lista de desejos.`
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="text-white">
      <form
        className="flex justify-center items-center mt-10 gap-x-4 text-lg"
        onSubmit={getGames}
      >
        <input
          id="gameName"
          name="gameName"
          placeholder="Nome do jogo..."
          className="border p-2 rounded-md min-w-lg"
          onChange={(event) => setSearch(event.target.value)}
        />

        <select
          disabled={loading}
          className={`border py-2 px-1 rounded-md ${loading && "opacity-70"}`}
          onChange={(event) => setGameStore(event.target.value)}
        >
          <option value="steam" className="text-black">
            Steam
          </option>
          <option value="epic" className="text-black">
            Epic Games Store
          </option>
        </select>

        <button
          disabled={loading}
          className={`px-3 py-2 rounded-lg bg-white text-black font-semibold transition ${
            loading ? "opacity-70" : "cursor-pointer hover:bg-gray-300"
          }`}
        >
          Pesquisar
        </button>
      </form>

      <div className="text-center my-6">
        {searchedGameStore === "Epic Games Store" && (
          <p>
            ⚠️ Nota: Alguns links para jogos da{" "}
            <strong>Epic Games Store</strong> podem não funcionar corretamente.
          </p>
        )}
      </div>

      <div className="flex flex-col gap-y-5 items-center mb-5">
        {games?.length === 0 ? (
          <p className="text-white text-2xl">
            {search} não foi encontrado na {searchedGameStore}.
          </p>
        ) : (
          games?.map((game) => (
            <GameCard
              game={game}
              gameStore={searchedGameStore}
              wishlistButtonText="Adicionar à lista de desejos"
              wishlistButtonFunction={addGameToWishlist}
              key={game.identifier}
            />
          ))
        )}
      </div>
    </div>
  );
}
