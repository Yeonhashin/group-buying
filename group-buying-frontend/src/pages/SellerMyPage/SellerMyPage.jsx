import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../../api/apiClient";

const STATUS_MAP = {
    RECRUITING: { label: "모집중", className: "bg-indigo-100 text-indigo-700" },
    COMPLETED: { label: "목표달성", className: "bg-green-100 text-green-700" },
    FAILED: { label: "실패", className: "bg-red-100 text-red-600" },
};

export default function SellerMyPage() {
    const [groupPurchases, setGroupPurchases] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        apiFetch("/api/group-purchases/my", { method: "GET" })
            .then((res) => setGroupPurchases(res.data || []))
            .catch((err) => console.error(err))
            .finally(() => setIsLoading(false));
    }, []);

    if (isLoading) return <div className="text-center py-20 text-gray-500">로딩 중...</div>;

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">판매자 페이지</h1>

            <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
                <div className="px-5 py-4 border-b border-gray-100">
                    <h2 className="text-base font-semibold text-gray-900">내 공동구매 목록</h2>
                </div>

                {groupPurchases.length === 0 ? (
                    <p className="text-center py-12 text-gray-400 text-sm">등록한 공동구매가 없습니다.</p>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead className="bg-gray-50 text-gray-500">
                            <tr>
                                <th className="text-left px-5 py-3 font-medium">공동구매명</th>
                                <th className="text-left px-5 py-3 font-medium">상태</th>
                                <th className="text-left px-5 py-3 font-medium">진행 기간</th>
                                <th className="text-left px-5 py-3 font-medium">참여 인원</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                            {groupPurchases.map((gp) => {
                                const status = STATUS_MAP[gp.status] || { label: gp.status, className: "bg-gray-100 text-gray-500" };
                                return (
                                    <tr
                                        key={gp.id}
                                        className="hover:bg-gray-50 cursor-pointer"
                                        onClick={() => navigate(`/group-purchases/${gp.id}`)}
                                    >
                                        <td className="px-5 py-4 font-medium text-gray-800">
                                            {gp.title}
                                        </td>
                                        <td className="px-5 py-4">
                                                <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${status.className}`}>
                                                    {status.label}
                                                </span>
                                        </td>
                                        <td className="px-5 py-4 text-gray-500">
                                            {gp.startDt} ~ {gp.endDt}
                                        </td>
                                        <td className="px-5 py-4 text-gray-700">
                                            {gp.currentParticipants} / {gp.targetParticipants}명
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}