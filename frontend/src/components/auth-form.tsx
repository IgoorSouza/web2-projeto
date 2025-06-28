import { FormEvent, ReactNode } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../hooks/use-auth";

interface Props {
  onSubmit: () => void;
  text: string;
  buttonText: string;
  linkRoute: string;
  linkText: string;
  children: ReactNode;
}

export default function AuthForm({
  onSubmit,
  text,
  buttonText,
  linkRoute,
  linkText,
  children,
}: Props) {
  const { loading } = useAuth();

  function handleOnSubmit(event: FormEvent) {
    event.preventDefault();
    onSubmit();
  }

  return (
    <div className="flex flex-col items-center gap-10">
      <h1 className="text-xl text-white text-center max-w-1/2 mt-10">{text}</h1>

      <form
        onSubmit={handleOnSubmit}
        className="flex flex-col px-5 py-3 bg-white rounded-md w-full max-w-1/3"
      >
        {children}

        <button
          className={`bg-gray-900 text-white rounded-md py-1 px-5 m-auto text-lg cursor-pointer transition font-semibold ${
            loading ? "opacity-80" : "cursor-pointer hover:opacity-80"
          }`}
          disabled={loading}
        >
          {buttonText}
        </button>

        <Link
          to={linkRoute}
          className="underline rounded-md py-1 px-5 m-auto text-lg mt-2"
        >
          {linkText}
        </Link>
      </form>
    </div>
  );
}
