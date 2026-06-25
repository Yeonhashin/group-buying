import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";

export default function ProductCard({ product }) {
    const navigate = useNavigate();
    const user = useAuthStore((state) => state.user);
    const isMyProduct = user && product.userId === user.id;

    return (
        <div
            className="bg-white border border-gray-200 rounded-xl overflow-hidden cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => navigate(`/products/${product.id}`)}
        >
            <div className="relative aspect-square w-full bg-gray-100 overflow-hidden">
                <img
                    src={product?.imageUrl?.startsWith('http')
                        ? product?.imageUrl
                        : `${import.meta.env.VITE_API_URL || "http://localhost:8081"}${product?.imageUrl}`}
                    alt={product.name}
                    className="w-full h-full object-cover"
                />
                {isMyProduct && (
                    <span className="absolute top-2 left-2 bg-indigo-600 text-white text-xs font-semibold px-2 py-0.5 rounded-full">
                        내 상품
                    </span>
                )}
            </div>
            <div className="p-4">
                <p className="text-sm font-semibold text-gray-900 leading-snug line-clamp-2">{product.name}</p>
                <p className="mt-1 text-sm font-bold text-indigo-600">{product.price.toLocaleString()}원</p>
                <p className="mt-0.5 text-xs text-gray-400">재고 {product.stock}개</p>
            </div>
        </div>
    );
}
