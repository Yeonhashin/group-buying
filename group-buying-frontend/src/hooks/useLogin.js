import { useState } from "react";
import { login } from "../api/authApi";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";

export default function useLogin() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const navigate = useNavigate();
    const setAuth = useAuthStore((state) => state.setAuth);

    const loginUser = async (data) => {
        setLoading(true);
        setError("");

        try {
            const res = await login(data);
            console.log("login response:", res);

            // JWT 저장
            const { accessToken, userId, nickname } = res.data;
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem(
                "user",
                JSON.stringify({ id: userId, nickname })
            );
            console.log("token:", accessToken);

            setAuth({
                accessToken,
                user: { id: userId, nickname },
            });

            navigate("/");

        } catch (err) {
            setError(err.message || "로그인 실패");
        } finally {
            setLoading(false);
        }
    };

    return { login: loginUser, loading, error };
}