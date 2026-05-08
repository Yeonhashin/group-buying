import { useParams, useNavigate } from "react-router-dom";
import { useGroupPurchaseForm } from "../../hooks/useGroupPurchaseForm";
import GroupPurchaseForm from "../../components/GroupPurchase/GroupPurchaseForm";
import toast from "react-hot-toast";

const GroupPurchaseFormPage = ({ mode }) => {
    const { groupPurchaseId } = useParams();
    const navigate = useNavigate();

    const {
        data,
        isLoading,
        isError,
        submit,
        isSubmitting,
    } = useGroupPurchaseForm({
        mode,
        groupPurchaseId,
    });

    const isEditMode = mode === "edit";

    if (isEditMode && isLoading) {
        return <div>Loading...</div>;
    }

    if (isError) {
        return <div>데이터 조회 실패</div>;
    }

    const handleSubmit = (formData) => {
        submit(formData, {
            onSuccess: (res) => {

                const message = isEditMode
                    ? "수정 완료"
                    : "신규 등록 완료";

                toast.success(message);

                // ✅ 수정 케이스
                if (isEditMode) {
                    navigate(`/group-purchases/${groupPurchaseId}`);
                    return;
                }

                // ✅ create 응답 구조 FIX (핵심)
                const id =
                    res?.data?.data?.data ||   // (중첩 CommonResponse 케이스 방어)
                    res?.data?.data ||         // 정상 케이스
                    res?.data;                 // fallback

                if (!id) {
                    console.error("❌ id 추출 실패 - response 구조 확인 필요", res);
                    return;
                }

                navigate(`/group-purchases/${id}`);
            },
        });
    };

    return (
        <div>
            <h2>{isEditMode ? "공동구매 수정" : "공동구매 생성"}</h2>

            <GroupPurchaseForm
                initialData={data?.data}
                isEditMode={isEditMode}
                onSubmit={handleSubmit}
                isSubmitting={isSubmitting}
            />
        </div>
    );
};

export default GroupPurchaseFormPage;