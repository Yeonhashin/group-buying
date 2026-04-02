import { useState } from "react";
import { login } from "../api/authApi";
import { useNavigate } from "react-router-dom";

export default function useLogin() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    const loginUser = async (data) => {
        setLoading(true);
        setError("");

        try {
            const res = await login(data);
            console.log("login response:", res);

            // JWT 저장
            const token = res.data;
            localStorage.setItem("accessToken", token);
            console.log("token:", token);
            // 로그인 성공 후 상품 목록 페이지 이동
            navigate("/products");

        } catch (err) {
            setError(err.message || "로그인 실패");
        } finally {
            setLoading(false);
        }
    };

    return { login: loginUser, loading, error };
}