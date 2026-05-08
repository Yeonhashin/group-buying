import { create } from "zustand";

export const useAuthStore = create((set) => ({
    // 초기 상태 (localStorage 기반)
    accessToken: localStorage.getItem("accessToken") || null,
    user: JSON.parse(localStorage.getItem("user")) || null,
    isLoggedIn: !!localStorage.getItem("accessToken"),

    // 🔥 로그인
    setAuth: ({ accessToken, user }) => {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("user", JSON.stringify(user));

        set({
            accessToken,
            user,
            isLoggedIn: true,
        });
    },

    // 🔥 로그아웃
    logout: () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");

        set({
            accessToken: null,
            user: null,
            isLoggedIn: false,
        });
    },
}));