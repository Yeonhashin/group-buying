import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
    getGroupPurchase,
    createGroupPurchase,
    updateGroupPurchase,
    getGroupPurchaseEdit
} from "../api/groupPurchaseApi";

const queryKeys = {
    detail: (id) => ["groupPurchase", "detail", String(id)],
    list: ["groupPurchase", "list"],
};

/**
 * form hook
 */
export const useGroupPurchaseForm = ({ mode, groupPurchaseId }) => {
    const queryClient = useQueryClient();

    /**
     * edit only (폼 초기값용, 캐시 의존 X 권장)
     */
    const editQuery = useQuery({
        queryKey: ["groupPurchase", "form", String(groupPurchaseId)],
        queryFn: () => getGroupPurchaseEdit(groupPurchaseId),
        enabled: mode === "edit" && !!groupPurchaseId,
        staleTime: 0,
    });

    /**
     * mutation
     */
    const mutation = useMutation({
        mutationFn: (formData) => {
            if (mode === "create") {
                return createGroupPurchase(formData);
            }

            return updateGroupPurchase(groupPurchaseId, formData);
        },

        onSuccess: () => {
            // 리스트
            queryClient.invalidateQueries({
                queryKey: queryKeys.list,
            });

            // detail 무조건 강제 갱신
            queryClient.invalidateQueries({
                queryKey: queryKeys.detail(groupPurchaseId),
            });

            // form 캐시 제거
            queryClient.removeQueries({
                queryKey: ["groupPurchase", "form", String(groupPurchaseId)],
            });
        },
    });

    return {
        data: editQuery.data,
        isLoading: editQuery.isLoading,
        submit: mutation.mutate,
        isSubmitting: mutation.isPending,
    };
};