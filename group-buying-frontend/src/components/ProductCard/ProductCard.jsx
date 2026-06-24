import { useNavigate } from "react-router-dom";

export default function ProductCard({ product }) {
    const navigate = useNavigate();

    return (
        <div
            className="bg-white cursor-pointer group"
            onClick={() => navigate(`/products/${product.id}`)}
        >
            <div className="w-full aspect-square overflow-hidden bg-gray-100 rounded-lg">
                <img
                    src={product?.imageUrl?.startsWith('http')
                        ? product?.imageUrl
                        : `${import.meta.env.VITE_API_URL || "http://localhost:8081"}${product?.imageUrl}`}
                    alt={product.name}
                    className="w-full h-full object-cover transition-transform duration-200 group-hover:scale-105"
                />
            </div>
            <div className="pt-3">
                <p className="text-sm text-gray-800 leading-snug line-clamp-2">{product.name}</p>
                <p className="mt-1 text-base font-bold text-gray-900">{product.price.toLocaleString()}원</p>
                <p className="mt-0.5 text-xs text-gray-400">재고 {product.stock}개</p>
            </div>
        </div>
    );
}
