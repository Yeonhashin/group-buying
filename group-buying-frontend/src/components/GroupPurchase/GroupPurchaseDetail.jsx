import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";

const GroupPurchaseDetail = ({ groupPurchase }) => {
    if (!groupPurchase) {
        return <div>Loading...</div>;
    }

    const navigate = useNavigate();

    const {
        id,
        title,
        details,
        targetPrice,
        currentParticipants,
        targetParticipants,
        endDt,
        product,
    } = groupPurchase;

    const current = currentParticipants ?? 0;
    const target = targetParticipants ?? 1;
    const progress = Math.min(
        (currentParticipants / targetParticipants) * 100,
        100
    );

    const now = new Date();
    const endDate = new Date(endDt);
    const diff = endDate - now;

    const daysLeft = Math.max(Math.floor(diff / (1000 * 60 * 60 * 24)), 0);

    const { status } = groupPurchase;

    let displayStatus = "모집중";

    if (status === "CLOSED") displayStatus = "마감";
    else if (status === "COMPLETED") displayStatus = "달성";

    /**
     * 수정 가능 여부
     */
    const currentUserId = useAuthStore((state) => state.user?.id);
    const isOwner = currentUserId === groupPurchase.userId;
    console.log(currentUserId, isOwner);
    console.log("currentUserId:", currentUserId);
    console.log("groupPurchase.userId:", groupPurchase?.userId);
    console.log("type userId:", typeof groupPurchase?.userId);
    const canEdit = isOwner && status === "RECRUITING";

    /**
     * 수정 페이지 이동
     */
    const handleEdit = () => {
        navigate(`/group-purchases/${id}/edit`);
    };

    return (
        <div style={{border: "1px solid #ddd", padding: "20px", marginBottom: "20px"}}>
            <img
                src={`http://localhost:8081${product?.imageUrl}` || "/no-image.png"}
                alt="상품 이미지"
            />

            <h2>{title}</h2>
            <p>{details}</p>

            <p>기존 가격: {product?.price?.toLocaleString()}원</p>
            <p>목표 가격: {targetPrice.toLocaleString()}원</p>

            <p>
                참여 인원: {currentParticipants} / {targetParticipants}
            </p>

            <div style={{background: "#eee", height: "10px", borderRadius: "5px"}}>
                <div
                    style={{
                        width: `${progress}%`,
                        height: "100%",
                        background: "#4caf50",
                    }}
                />
            </div>

            <p>진행률: {progress.toFixed(0)}%</p>
            <p>남은 기간: {daysLeft}일</p>
            <p>상태: {displayStatus}</p>

            {canEdit && (
            <button
                onClick={handleEdit}
                style={{
                    marginTop: "12px",
                    padding: "10px",
                    cursor: "pointer",
                    background: "#1976d2",
                    color: "#fff",
                    border: "none",
                }}
            >
                수정하기
            </button>
            )}
        </div>
    );
};

export default GroupPurchaseDetail;