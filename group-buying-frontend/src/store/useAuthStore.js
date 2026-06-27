import { create } from "zustand";

export const useAuthStore = create((set) => ({
    // 초기 상태 (localStorage 기반)
    accessToken: localStorage.getItem("accessToken") || null,
    user: JSON.parse(localStorage.getItem("user")) || null,
    role: JSON.parse(localStorage.getItem("role")) || null,

    isLoggedIn: !!localStorage.getItem("accessToken"),

    // 로그인
    setAuth: ({ accessToken, user, role }) => {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("role", JSON.stringify(role));

        set({
            accessToken,
            user,
            role,
            isLoggedIn: true,
        });
    },

    // 로그아웃
    logout: () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        localStorage.removeItem("role");

        set({
            accessToken: null,
            user: null,
            role: null,
            isLoggedIn: false,
        });
    },
}));