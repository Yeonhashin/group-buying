import { apiFetch } from "./apiClient";

/**
 * 내 주문 목록 조회
 */
export const fetchMyOrders = () => {
    return apiFetch("/api/orders/my/orders", {
        method: "GET",
    });
};

/**
 * 주문 단건 조회
 */
export const fetchOrder = (orderId) => {
    return apiFetch(`/api/orders/${orderId}`, {
        method: "GET",
    });
};

/**
 * 주문 결제
 */
export const payOrder = (orderId, paymentId) => {
    return apiFetch(
        `/api/orders/${orderId}/pay?paymentId=${paymentId}`,
        {
            method: "POST",
        }
    );
};

/**
 * 주문 취소
 */
export const cancelOrder = (orderId) => {
    return apiFetch(`/api/orders/${orderId}/cancel`, {
        method: "POST",
    });
};