// components/MyPage/MyOrderList.jsx

import OrderStatusBadge from "./OrderStatusBadge";
import OrderActionButtons from "./OrderActionButtons";

function MyOrderList({ order }) {
    return (
        <div className="order-row">
            <div>
                <div>주문번호: {order.orderId}</div>
                <div>상품: {order.productName}</div>
            </div>

            <OrderStatusBadge order={order} />

            <OrderActionButtons order={order} />
        </div>
    );
}

export default MyOrderList;