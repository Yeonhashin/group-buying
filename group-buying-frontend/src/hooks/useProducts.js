import { useQuery } from "@tanstack/react-query";
import { getProducts } from "../api/productApi";

export const useProducts = ({ page = 0, size = 9, keyword = "" }) => {
    return useQuery({
        queryKey: ["products", page, size, keyword],
        queryFn: () => getProducts({ page, size, keyword }),
    });
};