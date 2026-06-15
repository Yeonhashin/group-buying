import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import toast from "react-hot-toast";

import LoginForm from "../components/Auth/LoginForm";

export default function LoginPage() {
    const location = useLocation();

    useEffect(() => {
        if (location.state?.loginRequired) {
            toast.error(
                "로그인이 필요합니다.",
                {
                    id: "login-required",
                }
            );
        }
    }, [location]);

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50">
            <LoginForm />
        </div>
    );
}