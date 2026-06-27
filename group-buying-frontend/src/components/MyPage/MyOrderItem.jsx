import { useNavigate } from "react-router-dom";

function MyOrderItem({ orders, onPay, onCancel }) {
    const navigate = useNavigate();

    const isExpired = (order) => {
        const deadline = new Date(order.orderCreateDt);
        deadline.setHours(deadline.getHours() + 24);
        return new Date() > deadline;
    };

    const getPaymentDeadline = (orderCreateDt) => {
        if (!orderCreateDt) return "";
        const deadline = new Date(orderCreateDt);
        deadline.setHours(deadline.getHours() + 24);
        return deadline.toLocaleString("ko-KR");
    };

    const formatDate = (dateString) => {
        if (!dateString) return "-";
        return new Date(dateString).toLocaleDateString("ko-KR");
    };

    const getStatus = (order) => {
        if (order.groupPurchaseStatus === "FAILED") return { label: "공동구매 실패", className: "bg-red-100 text-red-600" };
        if (order.groupPurchaseStatus === "RECRUITING") return { label: "모집중", className: "bg-indigo-100 text-indigo-700" };

        if (order.groupPurchaseStatus === "COMPLETED" && !order.orderId) {
            return { label: "공동구매 참여 완료", className: "bg-blue-100 text-blue-700" };
        }

        if (order.participationStatus === "CANCELED") return { label: "참여 취소", className: "bg-gray-100 text-gray-500" };
        if (order.orderStatus === "CANCELED") return { label: "자동 취소", className: "bg-gray-100 text-gray-500" };
        if (order.paymentStatus === "FAILED" && order.paidDt) return { label: "결제 취소", className: "bg-orange-100 text-orange-600" };
        if (order.paymentStatus === "PAID") return { label: "결제 완료", className: "bg-green-100 text-green-700" };
        if (order.orderId && order.orderStatus === "CREATED" && isExpired(order)) return { label: "결제 기한 만료", className: "bg-gray-100 text-gray-500" };
        if (order.orderId && order.orderStatus === "CREATED") return { label: "결제 필요", className: "bg-yellow-100 text-yellow-700" };
        return { label: "-", className: "bg-gray-100 text-gray-400" };
    };

    const getAction = (order) => {
        if (["FAILED", "RECRUITING"].includes(order.groupPurchaseStatus)) return null;

        if (order.groupPurchaseStatus === "COMPLETED" && !order.orderId) {
            return <span className="text-xs text-blue-500 font-medium">주문 생성 대기중</span>;
        }

        if (order.participationStatus === "CANCELED" || order.orderStatus === "CANCELED") return null;

        if (order.paymentStatus === "PAID" && !isExpired(order)) {
            return (
                <div className="flex flex-col items-start gap-1">
                    <button onClick={() => onCancel(order.orderId)} className="text-xs px-3 py-1.5 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors">
                        결제취소
                    </button>
                    <p className="text-xs text-red-500 font-medium">~{getPaymentDeadline(order.orderCreateDt)}</p>
                </div>
            );
        }

        if (order.orderId && isExpired(order)) {
            return <span className="text-xs text-gray-400">결제 불가</span>;
        }

        if (order.orderId && order.orderStatus === "CREATED" && order.paymentStatus !== "PAID") {
            return (
                <div className="flex flex-col items-start gap-1">
                    <button onClick={() => onPay(order.orderId)} className="text-xs px-3 py-1.5 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors">
                        결제하기
                    </button>
                    <p className="text-xs text-red-500 font-medium">~{getPaymentDeadline(order.orderCreateDt)}</p>
                </div>
            );
        }

        return null;
    };

    const visibleOrders = orders.filter((order) => order.participationStatus !== "CANCELED");

    if (!visibleOrders.length) {
        return <p className="text-center py-12 text-gray-400 text-sm">주문 내역이 없습니다.</p>;
    }

    return (
        <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
            <div className="px-5 py-4 border-b border-gray-100">
                <h2 className="text-base font-semibold text-gray-900">내 주문</h2>
            </div>

            <div className="overflow-x-auto">
                <table className="w-full text-sm">
                    <thead className="bg-gray-50 text-gray-500">
                        <tr>
                            <th className="text-left px-5 py-3 font-medium">공동구매</th>
                            <th className="text-left px-5 py-3 font-medium">참여일</th>
                            <th className="text-left px-5 py-3 font-medium">상태</th>
                            <th className="text-left px-5 py-3 font-medium">액션</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {visibleOrders.map((order, index) => {
                            const status = getStatus(order);
                            return (
                                <tr key={`${order.groupPurchaseId}-${index}`} className="hover:bg-gray-50">
                                    <td className="px-5 py-4">
                                        <button
                                            onClick={() => navigate(`/group-purchases/${order.groupPurchaseId}`)}
                                            className="font-medium text-gray-800 hover:text-indigo-600 hover:underline text-left"
                                        >
                                            {order.title}
                                        </button>
                                    </td>
                                    <td className="px-5 py-4 text-gray-500">{formatDate(order.participationDt)}</td>
                                    <td className="px-5 py-4">
                                        <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${status.className}`}>
                                            {status.label}
                                        </span>
                                    </td>
                                    <td className="px-5 py-4">{getAction(order) ?? <span className="text-gray-300">-</span>}</td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default MyOrderItem;
