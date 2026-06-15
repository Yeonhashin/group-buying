import { usePayOrder, useCancelOrder } from "../../hooks/useOrder";

function OrderActionButtons({ order }) {
    const { mutate: payOrder, isPending: payLoading } = usePayOrder();
    const { mutate: cancelOrder, isPending: cancelLoading } = useCancelOrder();

    const canPay =
        order.status === "CREATED" &&
        (order.paymentStatus === "READY" ||
            order.paymentStatus === "FAILED");

    const canCancel =
        order.status === "CREATED";

    const handlePay = () => {
        payOrder({
            orderId: order.orderId,
            paymentId: "MOCK_PAYMENT",
        });
    };

    const handleCancel = () => {
        cancelOrder({
            orderId: order.orderId,
        });
    };

    return (
        <div className="order-actions">
            {canPay && (
                <button onClick={handlePay} disabled={payLoading}>
                    {payLoading ? "결제중..." : "결제"}
                </button>
            )}

            {canCancel && (
                <button onClick={handleCancel} disabled={cancelLoading}>
                    {cancelLoading ? "취소중..." : "취소"}
                </button>
            )}
        </div>
    );
}

export default OrderActionButtons;