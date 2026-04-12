import { useNavigate } from "react-router-dom";

const GroupPurchaseCard = ({ groupPurchase }) => {
    const navigate = useNavigate();

    const {
        id,
        title,
        targetPrice,
        currentParticipants,
        targetParticipants,
        endDt,
        product,
    } = groupPurchase;

    // 진행률 계산
    const progress = Math.min(
        (currentParticipants / targetParticipants) * 100,
        100
    );

    // 남은 시간 계산
    const now = new Date();
    const endDate = new Date(endDt);
    const diff = endDate - now;

    const daysLeft = Math.max(Math.floor(diff / (1000 * 60 * 60 * 24)), 0);

    // 상태 판단
    let status = "모집중";
    if (currentParticipants >= targetParticipants) {
        status = "달성";
    } else if (diff <= 0) {
        status = "마감";
    }

    return (
        <div
            onClick={() => navigate(`/group-purchases/${id}`)}
            style={{
                width: "300px",
                border: "1px solid #ddd",
                borderRadius: "10px",
                padding: "16px",
                cursor: "pointer",
            }}
        >
            <img
                src={`http://localhost:8081${product?.imageUrl}` || "/no-image.png"}
                alt="상품 이미지"
            />
            <h3>{title}</h3>

            <h3>상품명: {product?.name}</h3>
            <p>
                기존 가격: {product?.price?.toLocaleString()}원
            </p>
            <p>목표 가격: {targetPrice.toLocaleString()}원</p>

            <p>
                참여 인원: {currentParticipants} / {targetParticipants}
            </p>

            {/* 진행률 */}
            <div
                style={{
                    background: "#eee",
                    borderRadius: "5px",
                    height: "10px",
                    marginBottom: "8px",
                }}
            >
                <div
                    style={{
                        width: `${progress}%`,
                        background: "#4caf50",
                        height: "100%",
                        borderRadius: "5px",
                    }}
                />
            </div>

            <p>진행률: {progress.toFixed(0)}%</p>

            <p>남은 기간: {daysLeft}일</p>

            <p>
                상태:{" "}
                <strong
                    style={{
                        color:
                            status === "달성"
                                ? "green"
                                : status === "마감"
                                    ? "red"
                                    : "black",
                    }}
                >
                    {status}
                </strong>
            </p>
        </div>
    );
};

export default GroupPurchaseCard;