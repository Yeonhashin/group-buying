import {
    useJoinGroupPurchase,
    useCancelGroupPurchase,
} from "../../hooks/useGroupPurchase";
import { useQueryClient } from "@tanstack/react-query";

import { useState } from "react";
import toast from "react-hot-toast";
import ConfirmModal from "../common/ConfirmModal";
import { useAuthStore } from "../../store/useAuthStore";

const ParticipationPanel = ({ groupPurchase }) => {
    const {
        id,
        currentParticipants,
        targetParticipants,
        status,
        isParticipated,
    } = groupPurchase;

    const queryClient = useQueryClient();

    const joinMutation = useJoinGroupPurchase(id);
    const cancelMutation = useCancelGroupPurchase(id);

    const isRecruiting = status === "RECRUITING";
    const isFull = currentParticipants >= targetParticipants;

    const [modalType, setModalType] = useState(null);
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn);

    /**
     * modal open
     */
    const openJoinModal = () => {
        if (!isLoggedIn) {
            toast.error("로그인이 필요합니다.");
            return;
        }

        if (!isRecruiting || isFull) {
            toast.error("이미 마감된 공동구매입니다.");
            return;
        }

        setModalType("join");
    };

    const openCancelModal = () => {
        if (!isLoggedIn) {
            toast.error("로그인이 필요합니다.");
            return;
        }

        setModalType("cancel");
    };

    /**
     * modal confirm
     */
    const handleConfirm = () => {
        if (modalType === "join") {
            joinMutation.mutate(
                {},
                {
                    onSuccess: () => {
                        toast.success("성공적으로 공동구매에 참여하였습니다!");

                        queryClient.invalidateQueries({
                            queryKey: ["groupPurchase", String(id)],
                        });
                    },
                    onError: (error) => {
                        toast.error(
                            error?.response?.data?.message ||
                            "참여 중 오류 발생"
                        );
                    },
                }
            );
        }

        if (modalType === "cancel") {
            cancelMutation.mutate(null, {
                onSuccess: () => {
                    toast.success("참여를 취소했습니다.");

                    queryClient.invalidateQueries({
                        queryKey: ["groupPurchase", String(id)],
                    });
                },
                onError: (error) => {
                    toast.error(
                        error?.response?.data?.message ||
                        "취소 중 오류 발생"
                    );
                },
            });
        }

        setModalType(null);
    };

    const handleCancelModal = () => {
        setModalType(null);
    };

    /**
     * 버튼 상태 결정
     */
    let buttonText = "";
    let onClickHandler = null;
    let disabled = false;
    const isLoading =
        joinMutation.isLoading || cancelMutation.isLoading;

    let backgroundColor = "#007bff"; // 기본 (참여하기 - 파랑)

    if (disabled || isLoading) {
        backgroundColor = "#ccc";
    } else if (isParticipated) {
        backgroundColor = "#dc3545"; // 🔥 빨강 (취소)
    }

    if (!isLoggedIn) {
        buttonText = "로그인 후 이용 가능";
        onClickHandler = () => toast.error("로그인이 필요합니다.");
    } else if (isParticipated) {
        buttonText = "참여취소하기";
        onClickHandler = openCancelModal;
    } else if (!isRecruiting) {
        buttonText = "마감됨";
        disabled = true;
    } else if (isFull) {
        buttonText = "모집 완료";
        disabled = true;
    } else {
        buttonText = "참여하기";
        onClickHandler = openJoinModal;
    }


    return (
        <div
            style={{
                border: "1px solid #aaa",
                padding: "20px",
                marginBottom: "20px",
            }}
        >
            <h3>참여 상태</h3>

            <button
                onClick={onClickHandler}
                disabled={disabled || isLoading}
                style={{
                    padding: "10px 20px",
                    background: backgroundColor,
                    color: "#fff",
                    border: "none",
                    cursor:
                        disabled || isLoading
                            ? "not-allowed"
                            : "pointer",
                }}
            >
                {isLoading ? "처리 중..." : buttonText}
            </button>

            {/* ✅ Confirm Modal */}
            <ConfirmModal
                isOpen={!!modalType}
                message={
                    modalType === "join"
                        ? "공동구매에 참여하시겠습니까?"
                        : "참여를 취소하시겠습니까?"
                }
                onConfirm={handleConfirm}
                onCancel={handleCancelModal}
            />
        </div>
    );
};

export default ParticipationPanel;