import { useForm } from "react-hook-form";
import useLogin from "../../hooks/useLogin";

export default function LoginForm() {
    const { register, handleSubmit, formState: { errors } } = useForm();
    const { login, loading, error } = useLogin();

    const onSubmit = async (data) => {
        try {
            await login(data);
        } catch (err) {
            // error 메시지는 useLogin 훅에서 관리됨
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="bg-white p-8 rounded-lg shadow-md w-80 flex flex-col">
            <h2 className="text-2xl font-bold mb-6 text-center">로그인</h2>

            <input
                type="email"
                placeholder="이메일"
                {...register("email", { required: "이메일을 입력해주세요" })}
                className="mb-4 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.email && <p className="text-red-500 mb-2">{errors.email.message}</p>}

            <input
                type="password"
                placeholder="비밀번호"
                {...register("password", { required: "비밀번호를 입력해주세요" })}
                className="mb-4 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.password && <p className="text-red-500 mb-2">{errors.password.message}</p>}

            {error && <p className="text-red-500 mb-2">{error}</p>}

            <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 text-white p-2 rounded hover:bg-blue-700 disabled:bg-blue-300"
            >
                {loading ? "로그인 중..." : "로그인"}
            </button>
        </form>
    );
}