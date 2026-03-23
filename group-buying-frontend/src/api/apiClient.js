const API_BASE_URL = "http://localhost:8081";

export async function apiFetch(url, options = {}) {

    const token = localStorage.getItem("accessToken");

    const headers = {
        "Content-Type": "application/json",
        ...options.headers,
    };

    if (token) {
        headers.Authorization = token.startsWith("Bearer ")
            ? token
            : `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${url}`, {
        ...options,
        headers,
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "API 요청 실패");
    }

    return data;
}