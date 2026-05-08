import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8081/api",
    headers: {
        "Content-Type": "application/json"
    }
});

// 🔥 요청 인터셉터 추가
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("accessToken");

        if (token) {
            config.headers.Authorization = token.startsWith("Bearer ")
                ? token
                : `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

export default axiosInstance;