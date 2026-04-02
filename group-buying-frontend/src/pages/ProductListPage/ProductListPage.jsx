import { useState } from "react";
import SearchBar from "../../components/SearchBar/SearchBar.jsx";
import ProductGrid from "../../components/ProductGrid/ProductGrid.jsx";
import Pagination from "../../components/Pagination/Pagination.jsx";

import { useNavigate } from "react-router-dom";

import { useProducts } from "../../hooks/useProducts.js";
import "./ProductListPage.css";


const ProductListPage = () => {

    const [keyword, setKeyword] = useState("");
    const token = localStorage.getItem("accessToken");
    const isLogin = !!token;
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        window.location.reload();
    };

    const {
        products,
        loading,
        error,
        page,
        setPage,
        totalPages,
        totalCount
    } = useProducts(keyword);

    if (loading) {
        return <div className="loading">상품을 불러오는 중...</div>;
    }

    if (error) {
        return <div className="error">상품 조회 실패</div>;
    }

    return (
        <div className="product-list-page">
            {isLogin && (
                <>
                    <button onClick={() => navigate("/products/create")}
                            className="add-product-button">
                        상품 등록
                    </button>

                    <button onClick={handleLogout}>
                        로그아웃
                    </button>
                </>
            )}
            <SearchBar
                keyword={keyword}
                setKeyword={setKeyword}
                setPage={setPage}
            />

            <div className="product-count">
                {keyword
                    ? `"${keyword}" 검색 결과 ${totalCount}개`
                    : `총 ${totalCount}개의 상품`
                }
            </div>

            <ProductGrid products={products}/>

            <Pagination
                page={page}
                totalPages={totalPages}
                setPage={setPage}
            />

        </div>
    );
};

export default ProductListPage;