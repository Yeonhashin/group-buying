import { useQuery } from "@tanstack/react-query";
import { getProduct } from "../api/productApi";

export const useProduct = (productId) => {
    return useQuery({
        queryKey: ["product", productId],
        queryFn: () => getProduct(productId),
        enabled: !!productId,
    });
};