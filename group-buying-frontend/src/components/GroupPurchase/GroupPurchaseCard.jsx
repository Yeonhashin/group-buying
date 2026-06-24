import { useNavigate } from "react-router-dom";

const STATUS_MAP = {
    COMPLETED: { label: "달성", className: "bg-green-100 text-green-700" },
    FAILED: { label: "마감", className: "bg-red-100 text-red-600" },
    default: { label: "모집중", className: "bg-indigo-100 text-indigo-700" },
};

const GroupPurchaseCard = ({ groupPurchase }) => {
    const navigate = useNavigate();
    const { id, title, targetPrice, currentParticipants, targetParticipants, remainingTime, product, status } = groupPurchase;

    const progress = Math.min((currentParticipants / targetParticipants) * 100, 100);
    const statusInfo = STATUS_MAP[status] ?? STATUS_MAP.default;

    return (
        <div
            onClick={() => navigate(`/group-purchases/${id}`)}
            className="bg-white border border-gray-200 rounded-xl overflow-hidden cursor-pointer hover:shadow-md transition-shadow"
        >
            <div className="aspect-square w-full bg-gray-100 overflow-hidden">
                <img
                    src={product?.imageUrl?.startsWith('http')
                        ? product?.imageUrl
                        : `${import.meta.env.VITE_API_URL || "http://localhost:8081"}${product?.imageUrl}`}
                    alt={product?.name}
                    className="w-full h-full object-cover"
                />
            </div>

            <div className="p-4">
                <div className="flex items-start justify-between gap-2 mb-2">
                    <h3 className="text-sm font-semibold text-gray-900 line-clamp-2 leading-snug">{title}</h3>
                    <span className={`shrink-0 text-xs font-medium px-2 py-0.5 rounded-full ${statusInfo.className}`}>
                        {statusInfo.label}
                    </span>
                </div>

                <p className="text-xs text-gray-500 mb-1">{product?.name}</p>

                <div className="flex items-center gap-1 mb-3">
                    <span className="text-xs text-gray-400 line-through">{product?.price?.toLocaleString()}원</span>
                    <span className="text-sm font-bold text-indigo-600">{targetPrice.toLocaleString()}원</span>
                </div>

                {/* 진행률 바 */}
                <div className="w-full bg-gray-100 rounded-full h-1.5 mb-1">
                    <div
                        className="bg-indigo-500 h-1.5 rounded-full transition-all"
                        style={{ width: `${progress}%` }}
                    />
                </div>
                <div className="flex justify-between text-xs text-gray-500">
                    <span>{currentParticipants}/{targetParticipants}명 참여</span>
                    <span>{progress.toFixed(0)}%</span>
                </div>

                <p className="mt-2 text-xs text-gray-400">남은 기간: {remainingTime}</p>
            </div>
        </div>
    );
};

export default GroupPurchaseCard;
