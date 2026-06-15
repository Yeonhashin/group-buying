import { Link } from "react-router-dom";
import { useRegisterForm } from "../hooks/useRegisterForm";

const RegisterForm = () => {
    const { register, handleSubmit, watch, errors, checkEmail, emailAvailable, onSubmit } = useRegisterForm();

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="bg-white p-8 rounded-xl shadow-md w-full max-w-sm">
            <h2 className="text-2xl font-bold mb-1 text-center text-gray-900">회원가입</h2>
            <p className="text-sm text-center text-gray-500 mb-7">공동구매 서비스에 가입하세요</p>

            {/* 이메일 */}
            <div className="mb-4">
                <div className="flex gap-2">
                    <input
                        type="text"
                        placeholder="이메일"
                        {...register("email", {
                            required: "이메일은 필수입니다",
                            pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: "올바른 이메일 형식이 아닙니다" },
                        })}
                        className={`flex-1 px-4 py-2.5 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent ${errors.email ? "border-red-400" : "border-gray-300"}`}
                    />
                    <button
                        type="button"
                        onClick={checkEmail}
                        className="px-3 py-2 text-xs font-medium bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors whitespace-nowrap"
                    >
                        중복 확인
                    </button>
                </div>
                {errors.email
                    ? <p className="mt-1 text-xs text-red-500">{errors.email.message}</p>
                    : emailAvailable && <p className="mt-1 text-xs text-green-600">사용 가능한 이메일입니다</p>
                }
            </div>

            {/* 비밀번호 */}
            <div className="mb-4">
                <input
                    type="password"
                    placeholder="비밀번호"
                    {...register("password", {
                        required: "비밀번호는 필수입니다",
                        minLength: { value: 4, message: "비밀번호는 4자 이상이어야 합니다" },
                    })}
                    className={`w-full px-4 py-2.5 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent ${errors.password ? "border-red-400" : "border-gray-300"}`}
                />
                {errors.password && <p className="mt-1 text-xs text-red-500">{errors.password.message}</p>}
            </div>

            {/* 비밀번호 확인 */}
            <div className="mb-4">
                <input
                    type="password"
                    placeholder="비밀번호 확인"
                    {...register("passwordConfirm", {
                        required: "비밀번호 확인은 필수입니다",
                        validate: (value) => value === watch("password") || "비밀번호가 일치하지 않습니다",
                    })}
                    className={`w-full px-4 py-2.5 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent ${errors.passwordConfirm ? "border-red-400" : "border-gray-300"}`}
                />
                {errors.passwordConfirm && <p className="mt-1 text-xs text-red-500">{errors.passwordConfirm.message}</p>}
            </div>

            {/* 닉네임 */}
            <div className="mb-6">
                <input
                    type="text"
                    placeholder="닉네임"
                    {...register("nickname", {
                        required: "닉네임은 필수입니다",
                        minLength: { value: 2, message: "닉네임은 2자 이상이어야 합니다" },
                    })}
                    className={`w-full px-4 py-2.5 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent ${errors.nickname ? "border-red-400" : "border-gray-300"}`}
                />
                {errors.nickname && <p className="mt-1 text-xs text-red-500">{errors.nickname.message}</p>}
            </div>

            <button
                type="submit"
                className="w-full bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-semibold hover:bg-indigo-700 transition-colors"
            >
                회원가입
            </button>

            <p className="mt-5 text-center text-sm text-gray-500">
                이미 계정이 있으신가요?{" "}
                <Link to="/login" className="text-indigo-600 font-medium hover:underline">
                    로그인
                </Link>
            </p>
        </form>
    );
};

export default RegisterForm;
