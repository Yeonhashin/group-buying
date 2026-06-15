import { useParams, useNavigate } from "react-router-dom";
import { useProduct } from "../../hooks/useProduct";
import { useAuthStore } from "../../store/useAuthStore";

function ProductDetailPage() {
    const { productId } = useParams();
    const { data: product, isLoading, isError } = useProduct(productId);
    const navigate = useNavigate();
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);

    if (isLoading) return <div className="text-center py-20 text-gray-500">로딩 중...</div>;
    if (isError) return <div className="text-center py-20 text-red-500">상품 조회에 실패했습니다.</div>;
    if (!product) return <div className="text-center py-20 text-gray-400">상품이 존재하지 않습니다.</div>;

    return (
        <div className="max-w-2xl mx-auto">
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="w-full aspect-square bg-gray-100">
                    <img
                        src={`http://localhost:8081${product.imageUrl}`}
                        alt={product.name}
                        className="w-full h-full object-cover"
                    />
                </div>

                <div className="p-6">
                    <h1 className="text-xl font-bold text-gray-900 mb-2">{product.name}</h1>
                    <p className="text-2xl font-bold text-indigo-600 mb-4">{product.price.toLocaleString()}원</p>
                    <p className="text-sm text-gray-600 leading-relaxed mb-4">{product.details}</p>
                    <p className="text-sm text-gray-400">재고 {product.stock}개</p>
                </div>
            </div>

            {isLoggedIn && (
                <button
                    className="mt-4 w-full py-3 bg-indigo-600 text-white font-medium rounded-xl hover:bg-indigo-700 transition-colors"
                    onClick={() => navigate(`/products/${productId}/edit`)}
                >
                    수정하기
                </button>
            )}
        </div>
    );
}

export default ProductDetailPage;
