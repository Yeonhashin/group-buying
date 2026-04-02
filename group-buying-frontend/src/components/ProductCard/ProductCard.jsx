import { useNavigate } from "react-router-dom";
import "./ProductCard.css";

export default function ProductCard({ product }) {

    const navigate = useNavigate();

    return (
        <div
            className="product-card"
            onClick={() => navigate(`/products/${product.id}`)}
        >
            <div className="product-image-wrapper">
                <img
                    src={`http://localhost:8081${product.imageUrl}`}
                    alt={product.name}
                    className="product-image"
                />
            </div>
            <div className="product-info">
                <div className="product-name">
                    {product.name}
                </div>

                <div className="product-price">
                    {product.price.toLocaleString()}원
                </div>

                <div className="product-stock">
                    재고 {product.stock}개
                </div>
            </div>

        </div>
    );
}