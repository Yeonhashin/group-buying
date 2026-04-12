import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
    getGroupPurchase,
    createGroupPurchase,
    updateGroupPurchase,
} from "../api/groupPurchaseApi";

export const useGroupPurchaseForm = ({ mode, groupPurchaseId }) => {
    const queryClient = useQueryClient();

    /**
     * 1. 상세 조회 (edit만)
     */
    const { data, isLoading } = useQuery({
        queryKey: ["groupPurchase", groupPurchaseId],
        queryFn: () => getGroupPurchase(groupPurchaseId),
        enabled: mode === "edit" && !!groupPurchaseId,
    });

    /**
     * 2. mutation 통합
     */
    const mutation = useMutation({
        mutationFn: (formData) => {
            if (mode === "create") {
                return createGroupPurchase(formData);
            }

            if (!groupPurchaseId) {
                throw new Error("groupPurchaseId 없음");
            }

            return updateGroupPurchase(groupPurchaseId, formData);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["groupPurchases"] });

            if (mode === "edit") {
                queryClient.invalidateQueries({
                    queryKey: ["groupPurchase", groupPurchaseId],
                });
            }
        },
    });

    /**
     * 3. submit
     */
    const submit = (formData, options) => {
        mutation.mutate(formData, options);
    };

    return {
        data,
        isLoading,
        submit,
        isSubmitting: mutation.isPending,
    };
};