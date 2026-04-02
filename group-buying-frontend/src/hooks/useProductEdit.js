import { useEffect, useState } from "react";
import { getProductDetail, updateProduct } from "../api/productApi";

export const useProductEdit = (id) => {

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {

        if (!id) return;

        const fetchProduct = async () => {
            const res = await getProductDetail(id);
            setProduct(res.data);
        };

        fetchProduct();

    }, [id]);

    const editProduct = async (formData) => {
        setLoading(true);
        try {
            await updateProduct(id, formData);
        } finally {
            setLoading(false);
        }
    };

    return {
        product,
        loading,
        editProduct
    };
};