import { useEffect, useState } from "react";

export const useProducts = (keyword) => {

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [page, setPage] = useState(0);
    const [size] = useState(9);
    const [totalPages, setTotalPages] = useState(0);
    const [totalCount, setTotalCount] = useState(0);

    useEffect(() => {

        setLoading(true);

        fetch(`/api/products?page=${page}&size=${size}&keyword=${keyword ?? ""}`)
            .then(res => res.json())
            .then(data => {

                const result = data.data;

                setProducts(result.content);
                setTotalPages(result.totalPages);
                setTotalCount(result.totalElements);

            })
            .catch(err => {
                console.error("상품 조회 실패", err);
                setError(err);
            })
            .finally(() => {
                setLoading(false);
            });

    }, [page, size, keyword]);

    return {
        products,
        page,
        setPage,
        totalPages,
        totalCount,
        loading,
        error
    };
};