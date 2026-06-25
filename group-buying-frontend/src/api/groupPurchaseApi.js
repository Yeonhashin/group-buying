import { apiFetch } from "./apiClient";

/**
 * 공동구매 목록 조회
 */
export const getGroupPurchases = ({ page, size, keyword, onlyRecruiting }) => {
    return apiFetch(
        `/api/group-purchases?page=${page}&size=${size}&keyword=${keyword ?? ""}&onlyRecruiting=${onlyRecruiting ?? false}`,
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
 * 공동구매 수정용 데이터 조회
 */
export const getGroupPurchaseEdit = (groupPurchaseId) => {
    return apiFetch(`/api/group-purchases/${groupPurchaseId}/edit`, {
        method: "GET",
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

/**
 * 공동구매 참여
 */
export const joinGroupPurchase = (groupPurchaseId, data) => {
    return apiFetch(`/api/group-purchases/${groupPurchaseId}/participation`, {
        method: "POST",
        body: JSON.stringify(data),
    });
};

/**
 * 공동구매 참여 취소
 */
export const cancelGroupPurchase = (groupPurchaseId) => {
    return apiFetch(`/api/group-purchases/${groupPurchaseId}/participation`, {
        method: "DELETE",
    });
};