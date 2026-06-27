import { apiFetch } from "./apiClient";

/**
 * 상품 목록 조회
 */
export const getProducts = async ({ page = 0, size = 9, keyword = "", onlyMine = false }) => {
    return apiFetch(`/api/products?page=${page}&size=${size}&keyword=${keyword}&onlyMine=${onlyMine}`);
};

/**
 * 상품 단건 조회
 */
export const getProduct = async (productId) => {
    const res = await apiFetch(`/api/products/${productId}`);
    return res.data;
};

/**
 * 상품 생성
 */
export const createProduct = async (formData) => {
    const res = await apiFetch("/api/products", {
        method: "POST",
        body: formData,
    });
    return res.data;
};

/**
 * 상품 수정
 */
export const updateProduct = async (productId, formData) => {
    const res = await apiFetch(`/api/products/${productId}`, {
        method: "PATCH",
        body: formData,
    });
    return res.data;
};

/**
 * 상품 삭제
 */
export const deleteProduct = async (productId) => {
    const res = await apiFetch(`/api/products/${productId}`, {
        method: "DELETE",
    });
    return res.data;
};