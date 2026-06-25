import { useQuery } from "@tanstack/react-query";
import { getGroupPurchases } from "../api/groupPurchaseApi";

export const useGroupPurchases = ({ page, size, keyword, onlyRecruiting }) => {
    return useQuery({
        queryKey: ["groupPurchases", page, size, keyword, onlyRecruiting],
        queryFn: () => getGroupPurchases({ page, size, keyword, onlyRecruiting }),
        select: (data) => data.data,
        keepPreviousData: true,
    });
};