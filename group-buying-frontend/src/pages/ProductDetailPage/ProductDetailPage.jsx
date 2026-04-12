import { useParams, useNavigate } from "react-router-dom";
import { useProduct } from "../../hooks/useProduct";
import "./ProductDetailPage.css";

function ProductDetailPage() {
    const { productId } = useParams();
    const { data: product, isLoading, isError } = useProduct(productId);
    const navigate = useNavigate();

    const token = localStorage.getItem("accessToken");

    if (isLoading) {
        return <div className="center">로딩중...</div>;
    }

    if (isError) {
        return <div className="center">상품 조회 실패</div>;
    }

    if (!product) {
        return <div className="center">상품이 존재하지 않습니다.</div>;
    }

    return (
        <div className="product-detail-container">
            <div className="product-detail-card">

                <img
                    src={`http://localhost:8081${product.imageUrl}`}
                    alt={product.name}
                    className="product-detail-image"
                />

                <h1 className="product-detail-name">{product.name}</h1>

                <p className="product-detail-description">
                    {product.details}
                </p>

                <p className="product-detail-price">
                    가격 : {product.price.toLocaleString()}원
                </p>

                <p>재고 : {product.stock}개</p>

            </div>

            {token && (
                <button
                    className="submit-btn"
                    onClick={() => navigate(`/products/${productId}/edit`)}
                >
                    수정하기
                </button>
            )}
        </div>
    );
}

export default ProductDetailPage;