import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGroupPurchases } from "../../hooks/useGroupPurchases";
import GroupPurchaseCard from "../../components/GroupPurchase/GroupPurchaseCard";
import Pagination from "../../components/Pagination/Pagination";
import SearchBar from "../../components/SearchBar/SearchBar";
import { useAuthStore } from "../../store/useAuthStore";

const GroupPurchaseListPage = () => {
    const [page, setPage] = useState(0);
    const [keyword, setKeyword] = useState("");
    const [onlyRecruiting, setOnlyRecruiting] = useState(false);
    const [onlyMine, setOnlyMine] = useState(false);

    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
    const role = useAuthStore((state) => state.role);
    const isSeller = role === "SELLER";

    const navigate = useNavigate();

    const { data, isLoading, isError } = useGroupPurchases({ page, size: 9, keyword, onlyRecruiting, onlyMine });

    if (isLoading) return <div className="text-center py-20 text-gray-500">불러오는 중...</div>;
    if (isError) return <div className="text-center py-20 text-red-500">데이터를 불러오지 못했습니다.</div>;

    const groupPurchases = data.content;
    const totalPages = data.totalPages;
    const totalElements = data.totalElements;

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900">공동구매 목록</h1>
                {isLoggedIn && isSeller && (
                    <button
                        onClick={() => navigate("/group-purchases/create")}
                        className="px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 transition-colors"
                    >
                        + 공동구매 생성
                    </button>
                )}
            </div>

            <SearchBar keyword={keyword} setKeyword={setKeyword} setPage={setPage} placeholder="공동구매명 또는 상품명으로 검색" />

            <label className="mt-3 flex items-center gap-2 cursor-pointer select-none text-sm text-gray-600">
                <input
                    type="checkbox"
                    checked={onlyRecruiting}
                    onChange={(e) => { setOnlyRecruiting(e.target.checked); setPage(0); }}
                    className="w-4 h-4 accent-indigo-600"
                />
                진행중인 공동구매만 보기
            </label>

            {isSeller && (
                <label className="mt-2 flex items-center gap-2 cursor-pointer select-none text-sm text-gray-600">
                    <input
                        type="checkbox"
                        checked={onlyMine}
                        onChange={(e) => { setOnlyMine(e.target.checked); setPage(0); }}
                        className="w-4 h-4 accent-indigo-600"
                    />
                    내 공동구매만 보기
                </label>
            )}

            <p className="mt-4 mb-2 text-sm text-gray-500">
                {keyword ? `"${keyword}" 검색 결과 ${totalElements}개` : `총 ${totalElements}개의 공동구매`}
            </p>

            {groupPurchases.length === 0 ? (
                <p className="text-center py-20 text-gray-400">
                    {keyword ? "검색 결과가 없습니다." : "진행 중인 공동구매가 없습니다."}
                </p>
            ) : (
                <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-5">
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