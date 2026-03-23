import axiosInstance from "../services/axiosInstance";

export const checkEmailApi = async (email) => {

    const res = await axiosInstance.get(`/users/check-email`, {
        params: { email }
    });

    return res.data;
};

export const registerApi = async (data) => {

    const res = await axiosInstance.post(`/users/signup`, data);

    return res.data;
};