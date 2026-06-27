import { useQuery } from "@tanstack/react-query";
import { getGroupPurchases } from "../api/groupPurchaseApi";

export const useGroupPurchases = ({ page, size, keyword, onlyRecruiting, onlyMine }) => {
    return useQuery({
        queryKey: ["groupPurchases", page, size, keyword, onlyRecruiting, onlyMine],
        queryFn: () => getGroupPurchases({ page, size, keyword, onlyRecruiting, onlyMine }),
        select: (data) => data.data,
        keepPreviousData: true,
    });
};