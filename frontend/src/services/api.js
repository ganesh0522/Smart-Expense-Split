import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8082/api",
});

// 🔹 REQUEST INTERCEPTOR (attach token)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// 🔹 RESPONSE INTERCEPTOR (handle errors globally)
api.interceptors.response.use(
  (response) => response,
  (error) => {

    // 🔥 If token expired or invalid → logout
    if (error.response?.status === 401) {
      localStorage.removeItem("token");

      // redirect to login
      window.location.href = "/";
    }

    return Promise.reject(error);
  }
);

export default api;