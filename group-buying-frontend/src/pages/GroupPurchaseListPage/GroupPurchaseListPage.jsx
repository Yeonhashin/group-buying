import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGroupPurchases } from "../../hooks/useGroupPurchases";
import GroupPurchaseCard from "../../components/GroupPurchase/GroupPurchaseCard";
import Pagination from "../../components/Pagination/Pagination";
import { useAuthStore } from "../../store/useAuthStore";

const GroupPurchaseListPage = () => {
    const [page, setPage] = useState(0);
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
    const navigate = useNavigate();

    const { data, isLoading, isError } = useGroupPurchases({ page, size: 9, keyword: "" });

    if (isLoading) return <div className="text-center py-20 text-gray-500">불러오는 중...</div>;
    if (isError) return <div className="text-center py-20 text-red-500">데이터를 불러오지 못했습니다.</div>;

    const groupPurchases = data.content;
    const totalPages = data.totalPages;

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900">공동구매 목록</h1>
                {isLoggedIn && (
                    <button
                        onClick={() => navigate("/group-purchases/create")}
                        className="px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition-colors"
                    >
                        + 공동구매 생성
                    </button>
                )}
            </div>

            {groupPurchases.length === 0 ? (
                <p className="text-center py-20 text-gray-400">진행 중인 공동구매가 없습니다.</p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {groupPurchases.map((gp) => (
                        <GroupPurchaseCard key={gp.id} groupPurchase={gp} />
                    ))}
                </div>
            )}

            <Pagination page={page} totalPages={totalPages} setPage={setPage} />
        </div>
    );
};

export default GroupPurchaseListPage;
