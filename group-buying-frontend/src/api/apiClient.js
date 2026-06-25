import { useAuthStore } from "../store/useAuthStore";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8081";

export async function apiFetch(url, options = {}) {
    const token = localStorage.getItem("accessToken");
    const headers = { ...options.headers };

    if (!(options.body instanceof FormData)) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers.Authorization = token.startsWith("Bearer ")
            ? token
            : `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${url}`, {
        ...options,
        headers,
        credentials: "include",
    });

    if (!response.ok) {
        // 401: 인증 필요 → 로그아웃 후 리다이렉트
        if (response.status === 401 && !options.skipAuthRedirect) {
            useAuthStore.getState().logout();
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            throw new Error("UNAUTHORIZED");
        }

        const text = await response.text();
        let errorMessage = text || "API 요청 실패";

        try {
            const errorData = JSON.parse(text);
            if (errorData.message) {
                errorMessage = errorData.message;
            }
        } catch {
            // JSON 파싱 실패하면 원본 텍스트 그대로 사용
        }

        throw new Error(errorMessage);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}