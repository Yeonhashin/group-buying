import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";

const STATUS_MAP = {
    COMPLETED: { label: "달성", className: "bg-green-100 text-green-700" },
    FAILED: { label: "마감", className: "bg-red-100 text-red-600" },
    default: { label: "모집중", className: "bg-indigo-100 text-indigo-700" },
};

const GroupPurchaseDetail = ({ groupPurchase }) => {
    const navigate = useNavigate();
    const currentUserId = useAuthStore((state) => state.user?.id);

    if (!groupPurchase) return <div className="text-center py-10 text-gray-400">로딩 중...</div>;

    const { id, title, details, targetPrice, currentParticipants, targetParticipants, remainingTime, product, status } = groupPurchase;
    const progress = Math.min((currentParticipants / targetParticipants) * 100, 100);
    const statusInfo = STATUS_MAP[status] ?? STATUS_MAP.default;
    const canEdit = currentUserId === groupPurchase.userId && status === "RECRUITING";

    return (
        <div className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm mb-4">
            <div className="w-full bg-gray-50 flex items-center justify-center p-4">
                <img
                    src={{product?.imageUrl?.startsWith('http') ? product?.imageUrl : `${import.meta.env.VITE_API_URL || "http://localhost:8081"}${product?.imageUrl}`}}
                    alt={product?.name}
                    className="max-w-full max-h-80 object-contain"
                />
            </div>

            <div className="p-6">
                <div className="flex items-start justify-between gap-3 mb-3">
                    <h2 className="text-xl font-bold text-gray-900">{title}</h2>
                    <span className={`shrink-0 text-xs font-medium px-3 py-1 rounded-full ${statusInfo.className}`}>
                        {statusInfo.label}
                    </span>
                </div>

                <p className="text-sm text-gray-600 leading-relaxed mb-5">{details}</p>

                <div className="flex items-center gap-3 mb-5">
                    <span className="text-sm text-gray-400 line-through">{product?.price?.toLocaleString()}원</span>
                    <span className="text-xl font-bold text-indigo-600">{targetPrice.toLocaleString()}원</span>
                </div>

                <div className="w-full bg-gray-100 rounded-full h-2 mb-2">
                    <div className="bg-indigo-500 h-2 rounded-full transition-all" style={{ width: `${progress}%` }} />
                </div>
                <div className="flex justify-between text-sm text-gray-500 mb-4">
                    <span>{currentParticipants}/{targetParticipants}명 참여</span>
                    <span>{progress.toFixed(0)}%</span>
                </div>

                <p className="text-sm text-gray-400 mb-4">남은 기간: {remainingTime}</p>

                {canEdit && (
                    <button
                        onClick={() => navigate(`/group-purchases/${id}/edit`)}
                        className="px-4 py-2 text-sm font-medium bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
                    >
                        수정하기
                    </button>
                )}
            </div>
        </div>
    );
};

export default GroupPurchaseDetail;
