import { useState } from "react";
import { useNavigate } from "react-router-dom";
import SearchBar from "../../components/SearchBar/SearchBar";
import ProductGrid from "../../components/ProductGrid/ProductGrid";
import Pagination from "../../components/Pagination/Pagination";
import { useProducts } from "../../hooks/useProducts";
import "./ProductListPage.css";
import {useAuthStore} from "../../store/useAuthStore.js";

const ProductListPage = () => {
    const [keyword, setKeyword] = useState("");
    const [page, setPage] = useState(0);

    const navigate = useNavigate();
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);

    const { data, isLoading, isError } = useProducts({
        page,
        keyword
    });

    const products = data?.content || [];
    const totalPages = data?.totalPages || 0;
    const totalCount = data?.totalElements || 0;

    if (isLoading) {
        return <div className="center">상품을 불러오는 중...</div>;
    }

    if (isError) {
        return <div className="center">상품 조회 실패</div>;
    }

    return (
        <div className="product-list-container">

            {isLoggedIn && (
                <div className="top-actions">
                    <button
                        className="submit-btn"
                        onClick={() => navigate("/products/create")}
                    >
                        상품 등록
                    </button>

                    <button className="logout-btn" onClick={() => {
                        localStorage.removeItem("accessToken");
                        window.location.reload();
                    }}>
                        로그아웃
                    </button>
                </div>
            )}

            <SearchBar
                keyword={keyword}
                setKeyword={setKeyword}
                setPage={setPage}
            />

            <div className="product-count">
                {keyword
                    ? `"${keyword}" 검색 결과 ${totalCount}개`
                    : `총 ${totalCount}개의 상품`}
            </div>

            <ProductGrid products={products} />

            <Pagination
                page={page}
                totalPages={totalPages}
                setPage={setPage}
            />
        </div>
    );
};

export default ProductListPage;