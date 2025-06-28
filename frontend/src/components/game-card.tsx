import { useState } from "react";
import { Game } from "../types/game";
import formatGamePrice from "../utils/format-game-price";

interface Props {
  game: Game;
  gameStore: string;
  wishlistButtonText: string;
  wishlistButtonFunction: (game: Game) => Promise<void>;
}

export default function GameCard({
  game,
  gameStore,
  wishlistButtonText,
  wishlistButtonFunction,
}: Props) {
  const [loading, setLoading] = useState<boolean>(false);
  const gameInitialPrice = formatGamePrice(game.initialPrice);
  const gameDiscountPrice = formatGamePrice(game.discountPrice);

  return (
    <div
      key={game.identifier}
      className="w-full max-w-md bg-zinc-800 rounded-2xl overflow-hidden shadow-xl hover:shadow-2xl transition-shadow duration-300"
    >
      <img src={game.image} className="w-full h-48 object-cover" />

      <div className="p-5 flex flex-col gap-2 text-center">
        <h2 className="text-white font-semibold text-xl mb-2">{game.title}</h2>

        {game.initialPrice === 0 ? (
          <p className="text-green-400 font-semibold">Gratuito</p>
        ) : (
          <>
            <p className="text-green-400 font-bold">
              Preço Atual: R${gameDiscountPrice}
            </p>

            {game.initialPrice > game.discountPrice && (
              <>
                <p className="text-md text-white">
                  Preço Original: <span>R${gameInitialPrice}</span>
                  {game.discountPercent > 0 && (
                    <span className="ml-2 bg-zinc-600 px-3 py-1 rounded-full">
                      {game.discountPercent}% OFF!
                    </span>
                  )}
                </p>
              </>
            )}
          </>
        )}

        <a
          href={game.url}
          target="_blank"
          rel="noopener noreferrer"
          className="mt-4 inline-block text-sm bg-white text-black px-4 py-2 rounded-lg transition duration-300 hover:opacity-70"
        >
          Acessar na {gameStore}
        </a>

        {game.initialPrice > 0 && (
          <button
            disabled={loading}
            onClick={async () => {
              setLoading(true);
              await wishlistButtonFunction(game);
              setLoading(false);
            }}
            className={`mt-2 inline-block text-sm bg-white text-black px-4 py-2 rounded-lg transition duration-300 ${
              loading ? "opacity-70" : "hover:opacity-70 cursor-pointer"
            }`}
          >
            {wishlistButtonText}
          </button>
        )}
      </div>
    </div>
  );
}
