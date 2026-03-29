import { apiFetch } from "./apiClient";

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