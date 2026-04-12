const ParticipationPanel = ({ groupPurchase }) => {
    const {
        id,
        currentParticipants,
        targetParticipants,
        endDt,
    } = groupPurchase;

    const now = new Date();
    const endDate = new Date(endDt);

    const isClosed = now > endDate;
    const isFull = currentParticipants >= targetParticipants;

    const handleParticipate = () => {
        alert(`공동구매 참여 요청: ${id}`);
        // TODO: useParticipateGroupPurchase 연결
    };

    return (
        <div style={{ border: "1px solid #aaa", padding: "20px", marginBottom: "20px" }}>
            <h3>참여</h3>

            <button
                onClick={handleParticipate}
                disabled={isClosed || isFull}
                style={{
                    padding: "10px 20px",
                    background: isClosed || isFull ? "#ccc" : "#007bff",
                    color: "#fff",
                    border: "none",
                    cursor: isClosed || isFull ? "not-allowed" : "pointer",
                }}
            >
                {isClosed
                    ? "마감됨"
                    : isFull
                        ? "모집 완료"
                        : "참여하기"}
            </button>
        </div>
    );
};

export default ParticipationPanel;