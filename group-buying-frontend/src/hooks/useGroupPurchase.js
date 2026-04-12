import { useQuery } from "@tanstack/react-query";
import { getGroupPurchase } from "../api/groupPurchaseApi";

export const useGroupPurchase = (groupPurchaseId) => {
    return useQuery({
        queryKey: ["groupPurchase", groupPurchaseId],
        queryFn: () => getGroupPurchase(groupPurchaseId),
        select: (data) => data.data,
        enabled: !!groupPurchaseId, // id 없을 때 실행 방지
    });
};