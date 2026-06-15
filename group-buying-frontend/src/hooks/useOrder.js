import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import * as orderApi from "../api/orderApi";

/**
 * 주문 목록 조회
 */
export const useOrders = () => {
    return useQuery({
        queryKey: ["orders"],
        queryFn: orderApi.fetchMyOrders,

        staleTime: 1000 * 60 * 5,

        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
        refetchOnMount: false,
    });
};

/**
 * 주문 결제
 */
export const usePayOrder = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ orderId, paymentId }) =>
            orderApi.payOrder(orderId, paymentId),

        onSuccess: async () => {

            // 주문 목록 재조회
            await queryClient.invalidateQueries({
                queryKey: ["orders"],
            });

            // 알림 재조회
            await queryClient.invalidateQueries({
                queryKey: ["notifications"],
            });
        },
    });
};

/**
 * 주문 취소
 */
export const useCancelOrder = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ orderId }) =>
            orderApi.cancelOrder(orderId),

        onSuccess: async () => {

            // 주문 목록 재조회
            await queryClient.invalidateQueries({
                queryKey: ["orders"],
            });

            // 알림 재조회
            await queryClient.invalidateQueries({
                queryKey: ["notifications"],
            });
        },
    });
};