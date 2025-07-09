import { FormEvent, useState } from "react";
import { Review } from "../types/review";
import { useAuth } from "../hooks/use-auth";
import axios from "../lib/axios";
import { AxiosError } from "axios";
import toast from "react-hot-toast";
import WriteReview from "../components/write-review";

export default function Reviews() {
  const [gameName, setGameName] = useState<string>("");
  const [searchedGame, setSearchedGame] = useState<string | null>(null);
  const [review, setReview] = useState<Review | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [showWriteReviewModal, setShowWriteReviewModal] =
    useState<boolean>(false);
  const [showDeleteReviewConfirmation, setShowDeleteReviewConfirmation] =
    useState<boolean>(false);
  const { authData } = useAuth();

  async function getGameReview(event: FormEvent<HTMLFormElement>) {
    try {
      event.preventDefault();
      if (gameName.trim() === "") return;

      setSearchedGame(gameName);
      setLoading(true);

      const response = await axios.get(`/games/review?gameName=${gameName}`, {
        headers: {
          Authorization: `Bearer ${authData!.token}`,
        },
      });

      setReview(response.data);
    } catch (error) {
      if (error instanceof AxiosError && error.status === 404) {
        setReview(null);
        return;
      }

      console.error(error);
      toast.error("Ocorreu um erro ao buscar a review do jogo.");
    } finally {
      setLoading(false);
    }
  }

  async function generateGameReview() {
    try {
      if (gameName.trim() === "") return;

      setLoading(true);

      const response = await axios.post(
        `/games/generate-review?gameName=${gameName}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${authData!.token}`,
          },
        }
      );

      setReview(response.data);
    } catch (error) {
      console.error(error);
      toast.error("Ocorreu um erro ao gerar a review do jogo.");
    } finally {
      setLoading(false);
    }
  }

  async function deleteReview() {
    try {
      setLoading(true);

      await axios.delete(`/games/review/${review!.id}`, {
        headers: {
          Authorization: `Bearer ${authData!.token}`,
        },
      });

      setReview(null);
      setShowDeleteReviewConfirmation(false);
      toast.success("Review excluída com sucesso!");
    } catch (error) {
      console.error(error);
      toast.error("Ocorreu um erro ao excluir a review.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="text-white">
        <form
          className="flex justify-center items-center mt-10 gap-x-4 text-lg"
          onSubmit={getGameReview}
        >
          <input
            id="gameName"
            name="gameName"
            placeholder="Nome do jogo..."
            className="border p-2 rounded-md min-w-lg"
            onChange={(event) => setGameName(event.target.value)}
          />

          <button
            disabled={loading}
            className={`px-3 py-2 rounded-lg bg-white text-black font-semibold transition ${
              loading ? "opacity-70" : "cursor-pointer hover:bg-gray-300"
            }`}
          >
            Buscar Review
          </button>
        </form>

        {searchedGame && (
          <div className="text-center my-6 px-4 max-w-3xl mx-auto">
            {review?.aiGenerated && (
              <p className="mb-4">
                ⚠️ Nota: Esta análise foi gerada por uma{" "}
                <strong>Inteligência Artificial</strong> e pode conter
                imprecisões.
              </p>
            )}

            {review ? (
              <div className="bg-zinc-800 p-6 rounded-lg shadow-md mb-6 text-left">
                <div className="flex justify-between items-center mb-2">
                  <h2 className="text-xl font-semibold mb-3">
                    Review: {searchedGame}
                  </h2>

                  <div className="flex flex-col items-end">
                    <p className="text-sm text-zinc-400">
                      Criada em:{" "}
                      {new Date(review.createdAt).toLocaleString("pt-BR", {
                        day: "2-digit",
                        month: "2-digit",
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </p>

                    {review.createdAt !== review.updatedAt && (
                      <p className="text-sm text-zinc-400">
                        Atualizada em:{" "}
                        {new Date(review.updatedAt).toLocaleString("pt-BR", {
                          day: "2-digit",
                          month: "2-digit",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </p>
                    )}
                  </div>
                </div>

                <textarea
                  readOnly
                  className="w-full min-h-[300px] max-h-[350px] p-4 bg-zinc-900 text-white rounded-lg resize-y"
                  value={review.content}
                />

                {(authData?.roles.includes("ADMIN") ||
                  authData?.roles.includes("SUPER_ADMIN")) && (
                  <div className="flex gap-x-3 mt-3 justify-end">
                    <button
                      onClick={() => setShowWriteReviewModal(true)}
                      className="px-3 py-2 rounded-lg bg-white text-black font-semibold cursor-pointer transition hover:opacity-70"
                    >
                      Editar Review
                    </button>
                    <button
                      className="px-4 py-2 bg-red-600 text-white rounded-md cursor-pointer transition hover:bg-red-700"
                      onClick={() => setShowDeleteReviewConfirmation(true)}
                    >
                      Excluir Review
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <>
                <p className="text-lg mb-5">
                  O jogo {searchedGame} não possui uma review.
                </p>

                <button
                  type="button"
                  onClick={generateGameReview}
                  disabled={loading}
                  className={`px-4 py-2 rounded-lg bg-white text-black font-semibold transition ${
                    loading ? "opacity-70" : "cursor-pointer hover:bg-gray-300"
                  }`}
                >
                  Gerar Review (IA)
                </button>

                {(authData?.roles.includes("ADMIN") ||
                  authData?.roles.includes("SUPER_ADMIN")) && (
                  <button
                    type="button"
                    disabled={loading}
                    onClick={() => setShowWriteReviewModal(true)}
                    className={`ml-5 px-4 py-2 rounded-lg bg-white text-black font-semibold transition ${
                      loading
                        ? "opacity-70"
                        : "cursor-pointer hover:bg-gray-300"
                    }`}
                  >
                    Escrever Review
                  </button>
                )}
              </>
            )}
          </div>
        )}
      </div>

      {showWriteReviewModal && (
        <WriteReview
          gameName={searchedGame!}
          review={review?.content}
          reviewId={review?.id}
          setReview={setReview}
          onClose={() => setShowWriteReviewModal(false)}
        />
      )}

      {showDeleteReviewConfirmation && (
        <>
          <div className="absolute inset-0 bg-black opacity-80 z-0"></div>

          <div className="fixed inset-0 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-lg p-6 max-w-sm w-full text-black">
              <h2 className="text-xl font-semibold mb-4">Confirmar exclusão</h2>
              <p className="mb-6">
                Tem certeza que deseja excluir essa review? Essa ação não pode
                ser desfeita.
              </p>
              <div className="flex justify-end gap-4">
                <button
                  onClick={() => setShowDeleteReviewConfirmation(false)}
                  className={`px-4 py-2 rounded-md bg-gray-300 text-black ${
                    loading ? "opacity-70" : "cursor-pointer hover:opacity-70"
                  }`}
                >
                  Cancelar
                </button>
                <button
                  onClick={deleteReview}
                  disabled={loading}
                  className={`px-4 py-2 rounded-md bg-red-600 text-white ${
                    loading ? "bg-red-700" : "cursor-pointer hover:bg-red-700"
                  }`}
                >
                  Confirmar
                </button>
              </div>
            </div>
          </div>
        </>
      )}
    </>
  );
}
