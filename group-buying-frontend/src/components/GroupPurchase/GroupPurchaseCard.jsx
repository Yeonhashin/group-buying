import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";

const STATUS_MAP = {
    COMPLETED: { label: "달성", className: "bg-green-100 text-green-700" },
    FAILED: { label: "마감", className: "bg-red-100 text-red-600" },
    default: { label: "모집중", className: "bg-indigo-100 text-indigo-700" },
};

const GroupPurchaseCard = ({ groupPurchase }) => {
    const navigate = useNavigate();
    const user = useAuthStore((state) => state.user);
    const { id, title, targetPrice, currentParticipants, targetParticipants, remainingTime, product, status, startDt, isParticipated, userId } = groupPurchase;
    const isMyGroupPurchase = user && userId === user.id;

    const progress = Math.min((currentParticipants / targetParticipants) * 100, 100);
    const todayStr = new Date().toISOString().slice(0, 10);
    const isUpcoming = startDt && startDt > todayStr;
    const statusInfo = isUpcoming
        ? { label: "시작 예정", className: "bg-gray-100 text-gray-400" }
        : (STATUS_MAP[status] ?? STATUS_MAP.default);

    return (
        <div
            onClick={() => navigate(`/group-purchases/${id}`)}
            className={`bg-white border border-gray-200 rounded-xl overflow-hidden cursor-pointer hover:shadow-md transition-shadow ${isUpcoming ? "opacity-70" : ""}`}
        >
            <div className="aspect-square w-full bg-gray-100 overflow-hidden relative">
                <img
                    src={product?.imageUrl?.startsWith('http')
                        ? product?.imageUrl
                        : `${import.meta.env.VITE_API_URL || "http://localhost:8081"}${product?.imageUrl}`}
                    alt={product?.name}
                    className={`w-full h-full object-cover ${isUpcoming ? "grayscale" : ""}`}
                />
                {isParticipated && (
                    <span className="absolute top-2 left-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-green-500 text-white shadow">
                        참여중
                    </span>
                )}
                {isMyGroupPurchase && (
                    <span className="absolute top-2 right-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-indigo-600 text-white shadow">
                        내 공동구매
                    </span>
                )}
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

                {isUpcoming
                    ? <p className="mt-2 text-xs text-gray-400">
                        {`${parseInt(startDt.split("-")[1])}월 ${parseInt(startDt.split("-")[2])}일에 공동구매 시작`}
                      </p>
                    : <p className="mt-2 text-xs text-gray-400">남은 기간: {remainingTime}</p>
                }
            </div>
        </div>
    );
};

export default GroupPurchaseCard;
