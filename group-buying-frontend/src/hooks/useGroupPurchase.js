import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
    getGroupPurchase,
    joinGroupPurchase,
    cancelGroupPurchase
} from "../api/groupPurchaseApi";

/**
 * queryKey 표준 (절대 고정)
 */
const queryKeys = {
    groupPurchase: {
        detail: (id) => ["groupPurchase", "detail", String(id)],
        list: ["groupPurchase", "list"],
    },
};

/**
 * 공동구매 상세 조회
 */
export const useGroupPurchase = (groupPurchaseId) => {
    return useQuery({
        queryKey: queryKeys.groupPurchase.detail(groupPurchaseId),
        queryFn: () => getGroupPurchase(groupPurchaseId),

        enabled: !!groupPurchaseId,

        select: (res) => res?.data ?? null,

        staleTime: 0,
        refetchOnMount: "always",
        refetchOnWindowFocus: false,
    });
};

/**
 * 공동구매 참여
 */
export const useJoinGroupPurchase = (groupPurchaseId) => {
    const queryClient = useQueryClient();
    const key = queryKeys.groupPurchase.detail(groupPurchaseId);

    return useMutation({
        mutationFn: (data) => joinGroupPurchase(groupPurchaseId, data),

        onMutate: async () => {
            await queryClient.cancelQueries({ queryKey: key });

            const previousData = queryClient.getQueryData(key);

            queryClient.setQueryData(key, (old) => {
                if (!old) return old;

                return {
                    ...old,
                    isParticipated: true,
                    currentParticipants: old.currentParticipants + 1,
                };
            });

            return { previousData };
        },

        onError: (err, variables, context) => {
            if (context?.previousData) {
                queryClient.setQueryData(key, context.previousData);
            }
        },

        onSettled: () => {
            queryClient.invalidateQueries({ queryKey: key });
        },
    });
};

/**
 * 공동구매 참여 취소
 */
export const useCancelGroupPurchase = (groupPurchaseId) => {
    const queryClient = useQueryClient();
    const key = queryKeys.groupPurchase.detail(groupPurchaseId);

    return useMutation({
        mutationFn: () => cancelGroupPurchase(groupPurchaseId),

        onMutate: async () => {
            await queryClient.cancelQueries({ queryKey: key });

            const previousData = queryClient.getQueryData(key);

            queryClient.setQueryData(key, (old) => {
                if (!old) return old;

                return {
                    ...old,
                    isParticipated: false,
                    currentParticipants: old.currentParticipants - 1,
                };
            });

            return { previousData };
        },

        onError: (err, variables, context) => {
            if (context?.previousData) {
                queryClient.setQueryData(key, context.previousData);
            }
        },

        onSettled: () => {
            queryClient.invalidateQueries({ queryKey: key });
        },
    });
};