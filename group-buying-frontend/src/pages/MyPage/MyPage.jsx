import { useState } from "react";
import toast from "react-hot-toast";
import ConfirmModal from "../../components/common/ConfirmModal";
import { useOrders, usePayOrder, useCancelOrder } from "../../hooks/useOrder";
import { useNotifications } from "../../hooks/useNotification";
import MyOrderItem from "../../components/MyPage/MyOrderItem";
import NotificationPanel from "../../components/MyPage/NotificationPanel";

function MyPage() {
    const { data: orders = [], isLoading: orderLoading } = useOrders();
    const { data: notifications = [] } = useNotifications();
    const payMutation = usePayOrder();
    const cancelMutation = useCancelOrder();

    const [modalOpen, setModalOpen] = useState(false);
    const [modalType, setModalType] = useState(null);
    const [selectedOrderId, setSelectedOrderId] = useState(null);

    const handlePay = (orderId) => { setSelectedOrderId(orderId); setModalType("pay"); setModalOpen(true); };
    const handleCancel = (orderId) => { setSelectedOrderId(orderId); setModalType("cancel"); setModalOpen(true); };

    const handleConfirm = () => {
        setModalOpen(false);
        if (modalType === "pay") {
            payMutation.mutate({ orderId: selectedOrderId, paymentId: "TEST_PAYMENT_ID" }, {
                onSuccess: () => toast.success("결제가 완료되었습니다."),
                onError: () => toast.error("결제 처리 중 오류가 발생했습니다."),
            });
        }
        if (modalType === "cancel") {
            cancelMutation.mutate({ orderId: selectedOrderId }, {
                onSuccess: () => toast.success("결제 취소가 완료되었습니다."),
                onError: () => toast.error("결제 취소 중 오류가 발생했습니다."),
            });
        }
    };

    if (orderLoading && orders.length === 0) {
        return <div className="text-center py-20 text-gray-500">로딩 중...</div>;
    }

    return (
        <div className="max-w-3xl mx-auto">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">마이페이지</h1>
            <NotificationPanel notifications={notifications} />
            <MyOrderItem orders={orders} onPay={handlePay} onCancel={handleCancel} />
            <ConfirmModal
                isOpen={modalOpen}
                title={modalType === "pay" ? "결제 확인" : "결제 취소 확인"}
                message={modalType === "pay" ? "결제를 진행하시겠습니까?" : "결제를 취소하시겠습니까?"}
                onConfirm={handleConfirm}
                onCancel={() => setModalOpen(false)}
            />
        </div>
    );
}

export default MyPage;
