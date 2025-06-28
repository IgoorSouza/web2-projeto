import axios from "axios";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response.status === 401 &&
      error.response.data === "Token de autenticação inválido ou expirado."
    ) {
      window.location.href = "/auth/login?expired=true";
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
