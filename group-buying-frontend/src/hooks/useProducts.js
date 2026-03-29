import { useEffect, useState } from "react";
import { getProducts } from "../api/productApi";

export const useProducts = () => {

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchProducts = async () => {

        setLoading(true);

        try {

            const data = await getProducts();

            setProducts(data.data);

        } catch (err) {

            console.error("상품 조회 실패", err);
            setError(err);

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {
        fetchProducts();
    }, []);

    return { products, loading, error };
};