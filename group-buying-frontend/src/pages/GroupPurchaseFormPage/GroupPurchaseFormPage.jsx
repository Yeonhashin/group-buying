import { useParams, useNavigate } from "react-router-dom";
import { useGroupPurchaseForm } from "../../hooks/useGroupPurchaseForm";
import GroupPurchaseForm from "../../components/GroupPurchase/GroupPurchaseForm";
import toast from "react-hot-toast";

const GroupPurchaseFormPage = ({ mode }) => {
    const { groupPurchaseId } = useParams();
    const navigate = useNavigate();
    const isEditMode = mode === "edit";

    const { data, isLoading, isError, submit, isSubmitting } = useGroupPurchaseForm({ mode, groupPurchaseId });

    if (isEditMode && isLoading) return <div className="text-center py-20 text-gray-500">로딩 중...</div>;
    if (isError) return <div className="text-center py-20 text-red-500">데이터 조회에 실패했습니다.</div>;

    const handleSubmit = (formData) => {
        submit(formData, {
            onSuccess: (res) => {
                toast.success(isEditMode ? "수정 완료" : "생성 완료");

                if (isEditMode) {
                    navigate(`/group-purchases/${groupPurchaseId}`);
                    return;
                }

                const id = res?.data?.data?.data || res?.data?.data || res?.data;
                if (!id) return;
                navigate(`/group-purchases/${id}`);
            },
        });
    };

    return (
        <div className="max-w-xl mx-auto">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">
                {isEditMode ? "공동구매 수정" : "공동구매 생성"}
            </h1>
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
