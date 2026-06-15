import { apiFetch } from "./apiClient";

/**
 * 전체 알림 조회
 */
export const fetchNotifications = () => {
    return apiFetch("/api/notifications", {
        method: "GET",
    });
};

/**
 * 읽지 않은 알림 조회
 */
export const fetchUnreadNotifications = () => {
    return apiFetch("/api/notifications/unread", {
        method: "GET",
    });
};

/**
 * 읽음 처리
 */
export const markAsRead = (notificationId) => {
    return apiFetch(
        `/api/notifications/${notificationId}/read`,
        {
            method: "PATCH",
        }
    );
};

/**
 * 모두 읽음 처리
 */
export const markAllAsRead = () => {
    return apiFetch(
        "/api/notifications/read-all",
        {
            method: "PATCH",
        }
    );
};