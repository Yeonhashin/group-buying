import { useQuery } from "@tanstack/react-query";
import { getProducts } from "../api/productApi";

export const useProducts = ({ page = 0, size = 9, keyword = "", onlyMine = false }) => {
    return useQuery({
        queryKey: ["products", page, size, keyword, onlyMine],
        queryFn: () => getProducts({ page, size, keyword, onlyMine }),
        select: (data) => data.data,
    });
};