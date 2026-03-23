import { useRegisterForm } from "../hooks/useRegisterForm";

const RegisterForm = () => {
    const {
        register,
        handleSubmit,
        watch,
        errors,
        checkEmail,
        emailAvailable,
        onSubmit
    } = useRegisterForm();

    const watchEmail = watch("email");

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            className="bg-white p-8 rounded-xl shadow-lg w-full max-w-lg"
        >
            <h2 className="text-gray-900 text-3xl font-bold mb-8 text-center">
                회원가입
            </h2>

            {/* 이메일 */}
            <div className="mb-6 flex flex-col sm:flex-row items-center sm:space-x-4">
                <input
                    type="text"
                    placeholder="이메일"
                    {...register("email", {
                        required: "이메일은 필수입니다",
                        pattern: {
                            value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                            message: "올바른 이메일 형식이 아닙니다"
                        }
                    })}
                    className={`w-64 border p-3 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white text-gray-900
                        ${errors.email ? "border-red-500" : "border-gray-300"}`}
                />
                <button
                    type="button"
                    onClick={checkEmail}
                    className="w-20 mt-2 sm:mt-0 bg-blue-500 text-white px-1 py-2 rounded hover:bg-blue-600 text-xs"
                >
                    중복 확인
                </button>
                <span className="mt-2 sm:mt-0 text-red-500 text-xs min-w-[160px]">
                    {errors.email?.message || (emailAvailable && "사용 가능한 이메일입니다") || "\u00A0"}
                </span>
            </div>

            {/* 비밀번호 */}
            <div className="mb-6 flex flex-col sm:flex-row items-center sm:space-x-4">
                <input
                    type="password"
                    placeholder="비밀번호"
                    {...register("password", {
                        required: "비밀번호는 필수입니다",
                        minLength: { value: 4, message: "비밀번호는 4자 이상이어야 합니다" }
                    })}
                    className={`w-64 border p-3 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white text-gray-900
                        ${errors.password ? "border-red-500" : "border-gray-300"}`}
                />
                <span className="mt-2 sm:mt-0 text-red-500 text-xs min-w-[160px]">
                    {errors.password?.message || "\u00A0"}
                </span>
            </div>

            {/* 비밀번호 확인 */}
            <div className="mb-6 flex flex-col sm:flex-row items-center sm:space-x-4">
                <input
                    type="password"
                    placeholder="비밀번호 확인"
                    {...register("passwordConfirm", {
                        required: "비밀번호 확인은 필수입니다",
                        validate: value => value === watch("password") || "비밀번호가 일치하지 않습니다"
                    })}
                    className={`w-64 border p-3 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white text-gray-900
                        ${errors.passwordConfirm ? "border-red-500" : "border-gray-300"}`}
                />
                <span className="mt-2 sm:mt-0 text-red-500 text-xs min-w-[160px]">
                    {errors.passwordConfirm?.message || "\u00A0"}
                </span>
            </div>

            {/* 닉네임 */}
            <div className="mb-8 flex flex-col sm:flex-row items-center sm:space-x-4">
                <input
                    type="text"
                    placeholder="닉네임"
                    {...register("nickname", {
                        required: "닉네임은 필수입니다",
                        minLength: { value: 2, message: "닉네임은 2자 이상이어야 합니다" }
                    })}
                    className={`w-64 border p-3 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white text-gray-900
                        ${errors.nickname ? "border-red-500" : "border-gray-300"}`}
                />
                <span className="mt-2 sm:mt-0 text-red-500 text-xs min-w-[160px]">
                    {errors.nickname?.message || "\u00A0"}
                </span>
            </div>

            <button
                type="submit"
                className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 font-semibold"
            >
                회원가입
            </button>
        </form>
    );
};

export default RegisterForm;