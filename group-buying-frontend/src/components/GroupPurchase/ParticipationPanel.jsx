import { useState } from "react";
import toast from "react-hot-toast";
import { useQueryClient } from "@tanstack/react-query";
import { useJoinGroupPurchase, useCancelGroupPurchase } from "../../hooks/useGroupPurchase";
import { useAuthStore } from "../../store/useAuthStore";
import ConfirmModal from "../common/ConfirmModal";

const formatStartDate = (startDt) => {
    const [, month, day] = startDt.split("-");
    return `${parseInt(month)}월 ${parseInt(day)}일에 공동구매 시작`;
};

const ParticipationPanel = ({ groupPurchase }) => {
    const { id, currentParticipants, targetParticipants, status, isParticipated, startDt } = groupPurchase;
    const queryClient = useQueryClient();
    const joinMutation = useJoinGroupPurchase(id);
    const cancelMutation = useCancelGroupPurchase(id);
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
    const [modalType, setModalType] = useState(null);

    const isRecruiting = status === "RECRUITING";
    const isFull = currentParticipants >= targetParticipants;
    const isLoading = joinMutation.isLoading || cancelMutation.isLoading;

    const todayStr = new Date().toISOString().slice(0, 10);
    const isUpcoming = startDt && startDt > todayStr;

    const handleConfirm = () => {
        if (modalType === "join") {
            joinMutation.mutate({}, {
                onSuccess: () => {
                    toast.success("공동구매에 참여했습니다!");
                    queryClient.invalidateQueries({ queryKey: ["groupPurchase", String(id)] });
                },
                onError: (error) => toast.error(error?.response?.data?.message || "참여 중 오류 발생"),
            });
        }
        if (modalType === "cancel") {
            cancelMutation.mutate(null, {
                onSuccess: () => {
                    toast.success("참여를 취소했습니다.");
                    queryClient.invalidateQueries({ queryKey: ["groupPurchase", String(id)] });
                },
                onError: (error) => toast.error(error?.response?.data?.message || "취소 중 오류 발생"),
            });
        }
        setModalType(null);
    };

    let buttonText, buttonClass, onClick, disabled = false;

    if (isUpcoming) {
        buttonText = formatStartDate(startDt);
        buttonClass = "bg-gray-200 text-gray-500 cursor-not-allowed";
        disabled = true;
    } else if (!isLoggedIn) {
        buttonText = "로그인 후 이용 가능";
        buttonClass = "bg-gray-200 text-gray-500 cursor-not-allowed";
        onClick = () => toast.error("로그인이 필요합니다.");
    } else if (isParticipated) {
        buttonText = "참여 취소";
        buttonClass = "bg-red-500 hover:bg-red-600 text-white";
        onClick = () => setModalType("cancel");
    } else if (!isRecruiting || isFull) {
        buttonText = isFull ? "모집 완료" : "마감됨";
        buttonClass = "bg-gray-200 text-gray-400 cursor-not-allowed";
        disabled = true;
    } else {
        buttonText = "참여하기";
        buttonClass = "bg-indigo-600 hover:bg-indigo-700 text-white";
        onClick = () => setModalType("join");
    }

    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm mb-4">
            <h3 className="text-base font-semibold text-gray-900 mb-3">참여하기</h3>
            <button
                onClick={onClick}
                disabled={disabled || isLoading}
                className={`w-full py-3 rounded-lg font-medium text-sm transition-colors disabled:cursor-not-allowed ${buttonClass}`}
            >
                {isLoading ? "처리 중..." : buttonText}
            </button>

            <ConfirmModal
                isOpen={!!modalType}
                message={modalType === "join" ? "공동구매에 참여하시겠습니까?" : "참여를 취소하시겠습니까?"}
                onConfirm={handleConfirm}
                onCancel={() => setModalType(null)}
            />
        </div>
    );
};

export default ParticipationPanel;
