const ORDER_STATUS_MAP = {
    CREATED: { label: "주문완료", className: "bg-indigo-100 text-indigo-700" },
    CANCELED: { label: "취소됨", className: "bg-red-100 text-red-600" },
};

const PAYMENT_STATUS_MAP = {
    READY: { label: "결제대기", className: "bg-gray-100 text-gray-500" },
    PAID: { label: "결제완료", className: "bg-green-100 text-green-700" },
    FAILED: { label: "결제실패", className: "bg-red-100 text-red-600" },
};

function OrderStatusBadge({ status, paymentStatus }) {
    const orderStatus = ORDER_STATUS_MAP[status] ?? { label: status, className: "bg-gray-100 text-gray-500" };
    const payment = paymentStatus ? PAYMENT_STATUS_MAP[paymentStatus] : null;

    return (
        <div className="flex items-center gap-1.5">
            <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${orderStatus.className}`}>
                {orderStatus.label}
            </span>
            {payment && (
                <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${payment.className}`}>
                    {payment.label}
                </span>
            )}
        </div>
    );
}

export default OrderStatusBadge;
