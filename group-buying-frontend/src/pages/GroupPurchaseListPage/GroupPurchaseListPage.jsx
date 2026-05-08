import { useState } from "react";
import { useGroupPurchases } from "../../hooks/useGroupPurchases";
import GroupPurchaseCard from "../../components/GroupPurchase/GroupPurchaseCard";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";

const GroupPurchaseListPage = () => {
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);

    const navigate = useNavigate();

    const handleCreate = () => {
        navigate("/group-purchases/create");
    };

    const [page, setPage] = useState(0);
    const size = 9;

    const { data, isLoading, isError } = useGroupPurchases({
        page,
        size,
        keyword: "",
    });

    if (isLoading) return <div>로딩 중...</div>;
    if (isError) return <div>에러 발생</div>;

    const groupPurchases = data.content;
    const totalPages = data.totalPages;

    return (
        <div style={{ padding: "20px" }}>
            <h2>공동 구매 목록</h2>

            {isLoggedIn && (
                <button onClick={handleCreate}>
                    공동구매 생성
                </button>
            )}

            <div style={{ display: "flex", flexWrap: "wrap", gap: "16px" }}>
                {groupPurchases.map((gp) => (
                    <GroupPurchaseCard key={gp.id} groupPurchase={gp} />
                ))}
            </div>

            {/* 페이지네이션 */}
            <div style={{ marginTop: "20px" }}>
                <button
                    disabled={page === 0}
                    onClick={() => setPage((prev) => prev - 1)}
                >
                    이전
                </button>

                <span style={{ margin: "0 10px" }}>
                    {page + 1} / {totalPages}
                </span>

                <button
                    disabled={page + 1 >= totalPages}
                    onClick={() => setPage((prev) => prev + 1)}
                >
                    다음
                </button>
            </div>
        </div>
    );
};

export default GroupPurchaseListPage;