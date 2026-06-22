import {
    useQuery,
    useMutation,
    useQueryClient,
} from "@tanstack/react-query";

import * as notificationApi from "../api/notificationApi";

/**
 * 전체 알림 조회
 */
export const useNotifications = () => {
    return useQuery({
        queryKey: ["notifications"],
        queryFn: notificationApi.fetchNotifications,

        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
        refetchOnMount: "always",
    });
};

/**
 * 읽지 않은 알림 개수
 */
export const useUnreadCount = () => {
    return useQuery({
        queryKey: ["notifications"],
        queryFn: notificationApi.fetchNotifications,

        staleTime: 1000 * 60 * 5,

        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
        refetchOnMount: false,

        select: (data) =>
            data.filter((notification) => !notification.isRead).length,
    });
};

/**
 * 알림 읽음 처리
 */
export const useMarkAsRead = () => {

    const queryClient = useQueryClient();

    return useMutation({

        mutationFn: notificationApi.markAsRead,

        /**
         * optimistic update
         */
        onMutate: async (notificationId) => {

            // 진행 중인 notifications 요청 취소
            await queryClient.cancelQueries({
                queryKey: ["notifications"],
            });

            // 기존 캐시 백업
            const previousNotifications =
                queryClient.getQueryData(["notifications"]);

            // 즉시 UI 반영
            queryClient.setQueryData(
                ["notifications"],
                (old = []) =>
                    old.map((notification) =>
                        notification.id === notificationId
                            ? {
                                ...notification,
                                isRead: true,
                            }
                            : notification
                    )
            );

            return {
                previousNotifications,
            };
        },

        /**
         * rollback
         */
        onError: (error, variables, context) => {

            if (context?.previousNotifications) {

                queryClient.setQueryData(
                    ["notifications"],
                    context.previousNotifications
                );
            }
        },

        /**
         * 서버 재동기화
         */
        onSettled: async () => {

            await queryClient.invalidateQueries({
                queryKey: ["notifications"],
            });
        },
    });
};

/**
 * 알림 전체 읽음 처리
 */
export const useMarkAllAsRead = () => {

    const queryClient = useQueryClient();

    return useMutation({

        mutationFn:
        notificationApi.markAllAsRead,

        onSuccess: async () => {

            await queryClient.invalidateQueries({
                queryKey: ["notifications"],
            });
        },
    });
};