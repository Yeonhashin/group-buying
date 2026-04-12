import { apiFetch } from "./apiClient";

/**
 * 공동구매 목록 조회
 */
export const getGroupPurchases = ({ page, size, keyword }) => {
    return apiFetch(
        `/api/group-purchases?page=${page}&size=${size}&keyword=${keyword ?? ""}`,
        {
            method: "GET",
        }
    );
};

/**
 * 공동구매 단건 조회
 */
export const getGroupPurchase = (groupPurchaseId) => {
    return apiFetch(`/api/group-purchases/${groupPurchaseId}`, {
        method: "GET",
    });
};

/**
 * 공동구매 신규 생성
 */
export const createGroupPurchase = (data) => {
    return apiFetch(`/api/group-purchases`, {
        method: "POST",
        body: JSON.stringify(data),
    });
};

/**
 * 공동구매 수정
 */
export const updateGroupPurchase = (groupPurchaseId, data) => {
    return apiFetch(`/api/group-purchases/${groupPurchaseId}`, {
        method: "PATCH",
        body: JSON.stringify(data),
    });
};