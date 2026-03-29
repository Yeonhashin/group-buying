import { useEffect, useState } from "react";
import { getProductDetail } from "../api/productApi";

export const useProductDetail = (productId) => {

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchProduct = async () => {

        try {

            const data = await getProductDetail(productId);

            setProduct(data.data);

        } catch (err) {

            console.error("상품 상세 조회 실패", err);
            setError(err);

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {

        if (!productId) return;

        fetchProduct();

    }, [productId]);

    return { product, loading, error };

};