import { useState } from "react";
import { useNavigate } from "react-router-dom";
import SearchBar from "../../components/SearchBar/SearchBar";
import ProductGrid from "../../components/ProductGrid/ProductGrid";
import Pagination from "../../components/Pagination/Pagination";
import { useProducts } from "../../hooks/useProducts";
import { useAuthStore } from "../../store/useAuthStore.js";

const ProductListPage = () => {
    const [keyword, setKeyword] = useState("");
    const [page, setPage] = useState(0);

    const navigate = useNavigate();
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);

    const { data, isLoading, isError } = useProducts({ page, keyword });

    const products = data?.content || [];
    const totalPages = data?.totalPages || 0;
    const totalCount = data?.totalElements || 0;

    if (isLoading) return <div className="text-center py-20 text-gray-500">상품을 불러오는 중...</div>;
    if (isError) return <div className="text-center py-20 text-red-500">상품 조회에 실패했습니다.</div>;

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900">상품 목록</h1>
                {isLoggedIn && (
                    <button
                        className="px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition-colors"
                        onClick={() => navigate("/products/create")}
                    >
                        + 상품 등록
                    </button>
                )}
            </div>

            <SearchBar keyword={keyword} setKeyword={setKeyword} setPage={setPage} />

            <p className="mt-4 mb-2 text-sm text-gray-500">
                {keyword ? `"${keyword}" 검색 결과 ${totalCount}개` : `총 ${totalCount}개의 상품`}
            </p>

            <ProductGrid products={products} />

            <Pagination page={page} totalPages={totalPages} setPage={setPage} />
        </div>
    );
};

export default ProductListPage;