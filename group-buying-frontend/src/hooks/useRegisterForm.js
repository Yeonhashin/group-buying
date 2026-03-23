import { useForm } from "react-hook-form";
import { useEffect, useState } from "react";
import { checkEmailApi, registerApi } from "../api/userApi";

export const useRegisterForm = () => {

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
        } catch (error) {

            setError("root.serverError", {
                type: "server",
                message: error.response?.data?.message || "회원가입 실패"
            });

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