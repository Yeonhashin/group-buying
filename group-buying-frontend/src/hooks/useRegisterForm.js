import { useForm } from "react-hook-form";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { checkEmailApi, registerApi } from "../api/userApi";

export const useRegisterForm = () => {

    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        watch,
        setError,
        clearErrors,
        formState: { errors }
    } = useForm();

    const [emailChecked, setEmailChecked] = useState(false);
    const [emailAvailable, setEmailAvailable] = useState(null);

    const email = watch("email");

    useEffect(() => {

        setEmailChecked(false);
        setEmailAvailable(null);

    }, [email]);

    const checkEmail = async () => {

        if (!email) {

            setError("email", {
                type: "manual",
                message: "이메일을 입력해주세요"
            });

            return;
        }

        try {

            const data = await checkEmailApi(email);

            const exists = data.data;

            setEmailChecked(true);

            if (exists) {

                setError("email", {
                    type: "manual",
                    message: "이미 사용중인 이메일입니다"
                });

                setEmailAvailable(false);

            } else {

                clearErrors("email");
                setEmailAvailable(true);

            }

        } catch (e) {
            console.error(e);
        }
    };

    const onSubmit = async (formData) => {

        if (!emailChecked) {

            setError("email", {
                type: "manual",
                message: "이메일 중복 확인을 해주세요"
            });

            return;
        }

        try {

            await registerApi(formData);

            alert("회원가입 성공");
            navigate("/login");
        } catch (error) {

            const fieldErrors = error.response?.data?.data;

            if (fieldErrors && typeof fieldErrors === "object") {
                Object.entries(fieldErrors).forEach(([field, message]) => {
                    setError(field, { type: "server", message });
                });
            } else {
                setError("root.serverError", {
                    type: "server",
                    message: error.response?.data?.message || "회원가입 실패"
                });
            }

        }
    };

    return {
        register,
        handleSubmit,
        watch,
        errors,
        checkEmail,
        emailAvailable,
        onSubmit
    };

};