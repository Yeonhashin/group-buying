import { useState } from "react";
import { FaBell } from "react-icons/fa";
import NotificationItem from "./NotificationItem";
import { useMarkAllAsRead } from "../../hooks/useNotification";

function NotificationPanel({ notifications }) {
    const [showRead, setShowRead] = useState(false);
    const { mutate: markAllAsRead } = useMarkAllAsRead();

    const sorted = [...notifications].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    const unread = sorted.filter((n) => !n.isRead);
    const read = sorted.filter((n) => n.isRead).slice(0, 30);

    return (
        <div className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm mb-6">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                    <FaBell className="text-indigo-500" />
                    읽지 않은 알림
                    <span className="text-indigo-600">({unread.length})</span>
                </h3>
                <button
                    onClick={() => unread.length > 0 && markAllAsRead()}
                    className="text-sm text-gray-500 hover:text-gray-800 transition-colors"
                >
                    모두 읽음 처리
                </button>
            </div>

            {unread.length === 0 ? (
                <div className="px-5 py-8 text-sm text-gray-400">읽지 않은 알림이 없습니다.</div>
            ) : (
                unread.map((n) => <NotificationItem key={n.id} notification={n} />)
            )}

            <div
                onClick={() => setShowRead(!showRead)}
                className="flex items-center gap-2 px-5 py-3 border-t border-gray-100 bg-gray-50 cursor-pointer hover:bg-gray-100 transition-colors text-sm font-medium text-gray-600"
            >
                <span>{showRead ? "▼" : "▶"}</span>
                읽은 알림 ({read.length})
            </div>

            {showRead && read.map((n) => <NotificationItem key={n.id} notification={n} />)}
        </div>
    );
}

export default NotificationPanel;
