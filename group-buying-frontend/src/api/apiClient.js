const API_BASE_URL = "http://localhost:8081";

export async function apiFetch(url, options = {}) {

    const token = localStorage.getItem("accessToken");

    const headers = {
        ...options.headers,
    };

    console.log("token:", token);

    // JSON body일 때만 Content-Type 설정
    if (!(options.body instanceof FormData)) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers.Authorization = token.startsWith("Bearer ")
            ? token
            : `Bearer ${token}`;
    }
    console.log("headers:", headers);
    const response = await fetch(`${API_BASE_URL}${url}`, {
        ...options,
        headers,
    });

    if (!response.ok) {

        if (response.status === 401 || response.status === 403) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            throw new Error("UNAUTHORIZED");
        }

        const text = await response.text();
        throw new Error(text || "API 요청 실패");
    }

    const text = await response.text();

    return text ? JSON.parse(text) : null;
}