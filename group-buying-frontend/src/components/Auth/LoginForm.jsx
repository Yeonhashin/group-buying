import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";
import useLogin from "../../hooks/useLogin";

export default function LoginForm() {
    const { register, handleSubmit, formState: { errors } } = useForm();
    const { login, loading, error } = useLogin();

    const onSubmit = async (data) => {
        try {
            await login(data);
        } catch {}
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="bg-white p-8 rounded-xl shadow-md w-full max-w-sm flex flex-col">
            <h2 className="text-2xl font-bold mb-1 text-center text-gray-900">로그인</h2>
            <p className="text-sm text-center text-gray-500 mb-7">공동구매 서비스에 오신 걸 환영합니다</p>

            <input
                type="email"
                placeholder="이메일"
                {...register("email", { required: "이메일을 입력해주세요" })}
                className="mb-1 px-4 py-2.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
            />
            {errors.email && <p className="text-red-500 text-xs mb-3">{errors.email.message}</p>}

            <input
                type="password"
                placeholder="비밀번호"
                {...register("password", { required: "비밀번호를 입력해주세요" })}
                className="mt-3 mb-1 px-4 py-2.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
            />
            {errors.password && <p className="text-red-500 text-xs mb-3">{errors.password.message}</p>}

            {error && <p className="text-red-500 text-xs mb-3">{error}</p>}

            <button
                type="submit"
                disabled={loading}
                className="mt-4 bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-semibold hover:bg-indigo-700 disabled:bg-indigo-300 transition-colors"
            >
                {loading ? "로그인 중..." : "로그인"}
            </button>

            <p className="mt-5 text-center text-sm text-gray-500">
                계정이 없으신가요?{" "}
                <Link to="/signup" className="text-indigo-600 font-medium hover:underline">
                    회원가입
                </Link>
            </p>
        </form>
    );
}
