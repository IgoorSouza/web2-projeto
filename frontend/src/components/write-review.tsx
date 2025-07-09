import { useState } from "react";
import { useAuth } from "../hooks/use-auth";
import axios from "../lib/axios";
import toast from "react-hot-toast";
import { Review } from "../types/review";

interface Props {
  gameName: string;
  review?: string;
  reviewId?: string;
  setReview: React.Dispatch<React.SetStateAction<Review | null>>;
  onClose: () => void;
}

export default function WriteReview({
  gameName,
  review,
  reviewId,
  setReview,
  onClose,
}: Props) {
  const [newReview, setNewReview] = useState<string>(review || "");
  const [loading, setLoading] = useState<boolean>(false);
  const { authData } = useAuth();

  async function createReview() {
    try {
      if (newReview.trim() === "") return;

      setLoading(true);

      const response = await axios.post(
        "/games/review",
        {
          gameName,
          review: newReview,
        },
        { headers: { Authorization: `Bearer ${authData!.token}` } }
      );

      setReview(response.data);
      onClose();
      toast.success("Review criada com sucesso!");
    } catch (error) {
      console.error(error);
      toast.error("Ocorreu um erro ao criar a review.");
    } finally {
      setLoading(false);
    }
  }

  async function updateReview() {
    try {
      if (newReview.trim() === "") return;

      setLoading(true);

      const response = await axios.put(
        `/games/review/${reviewId}`,
        {
          review: newReview,
        },
        { headers: { Authorization: `Bearer ${authData!.token}` } }
      );

      if (setReview) setReview(response.data);
      onClose();
      toast.success("Review editada com sucesso!");
    } catch (error) {
      console.error(error);
      toast.error("Ocorreu um erro ao editar a review.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="absolute inset-0 bg-black opacity-80 z-0"></div>

      <div className="fixed inset-0 flex items-center justify-center z-50">
        <div className="bg-white text-black p-6 rounded-lg shadow-lg w-2xl relative">
          <h2 className="text-2xl text-center font-semibold mb-4">
            {reviewId ? "Editar Review" : "Escrever Review"}: {gameName}
          </h2>

          <textarea
            readOnly={loading}
            className="w-full min-h-[300px] p-4 bg-zinc-300 rounded-lg resize-y"
            value={newReview}
            onChange={(event) => setNewReview(event.target.value)}
          />

          <div className="flex gap-x-2 items-center justify-end mt-3">
            <button
              disabled={loading}
              onClick={onClose}
              className={`px-4 py-2 rounded-md bg-gray-300 text-black ${
                loading ? "opacity-70" : "cursor-pointer hover:opacity-70"
              }`}
            >
              Fechar
            </button>

            <button
              disabled={loading}
              onClick={reviewId ? updateReview : createReview}
              className="bg-black text-white py-2 px-4 rounded-md hover:bg-zinc-800 cursor-pointer"
            >
              Salvar
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
