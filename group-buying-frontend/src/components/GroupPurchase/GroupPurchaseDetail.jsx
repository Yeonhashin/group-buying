import { useNavigate } from "react-router-dom";

const GroupPurchaseDetail = ({ groupPurchase }) => {
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

    const progress = Math.min(
        (currentParticipants / targetParticipants) * 100,
        100
    );

    const now = new Date();
    const endDate = new Date(endDt);
    const diff = endDate - now;

    const daysLeft = Math.max(Math.floor(diff / (1000 * 60 * 60 * 24)), 0);

    let status = "모집중";
    if (currentParticipants >= targetParticipants) status = "달성";
    else if (diff <= 0) status = "마감";

    /**
     * 수정 가능 여부 (중요)
     */
    const canEdit = status === "모집중";

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
            <p>상태: {status}</p>

            {/* 🔥 수정 버튼 추가 */}
            <button
                onClick={handleEdit}
                disabled={!canEdit}
                style={{
                    marginTop: "12px",
                    padding: "10px",
                    cursor: canEdit ? "pointer" : "not-allowed",
                    background: canEdit ? "#1976d2" : "#ccc",
                    color: "#fff",
                    border: "none",
                }}
            >
                수정하기
            </button>
        </div>
    );
};

export default GroupPurchaseDetail;