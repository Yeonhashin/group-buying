import { useMarkAsRead } from "../../hooks/useNotification";
import { formatDistanceToNow } from "date-fns";
import { ko } from "date-fns/locale";

function NotificationItem({ notification }) {
    const timeAgo = formatDistanceToNow(new Date(notification.createdAt), { addSuffix: true, locale: ko });
    const { mutate: markAsRead } = useMarkAsRead();

    return (
        <div
            onClick={() => !notification.isRead && markAsRead(notification.id)}
            className={`px-5 py-3 border-b border-gray-100 cursor-pointer hover:bg-gray-50 transition-colors ${
                notification.isRead ? "bg-gray-50" : "bg-white"
            }`}
        >
            <p className={`text-sm ${notification.isRead ? "text-gray-500 font-normal" : "text-gray-900 font-medium"}`}>
                {notification.message}
            </p>
            <p className="text-xs text-gray-400 mt-1">{timeAgo}</p>
        </div>
    );
}

export default NotificationItem;
