import { useQuery } from "@tanstack/react-query";
import { getGroupPurchases } from "../api/groupPurchaseApi";

export const useGroupPurchases = ({ page, size, keyword }) => {
    return useQuery({
        queryKey: ["groupPurchases", page, size, keyword],
        queryFn: () => getGroupPurchases({ page, size, keyword }),
        select: (data) => data.data, // CommonResponse 구조 대응
        keepPreviousData: true,
    });
};