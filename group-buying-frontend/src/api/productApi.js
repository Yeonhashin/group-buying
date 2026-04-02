import { apiFetch } from "./apiClient";
import axios from "axios";

/**
 * 상품 목록 조회
 */
export async function getProducts() {

    const data = await apiFetch("/api/products", {
        method: "GET",
    });

    return data;
}

/**
 * 상품 상세 조회
 */
export async function getProductDetail(productId) {

    const data = await apiFetch(`/api/products/${productId}`, {
        method: "GET",
    });

    return data;
}

/**
 * 상품 신규 등록
 */
export const createProduct = async (formData) => {

    return apiFetch("/api/products", {
        method: "POST",
        body: formData
    });

};

/**
 * 상품 수정
 */
export const updateProduct = async (id, formData) => {
    return apiFetch(`/api/products/${id}`, {
        method: "PATCH",
        body: formData
    });
};
