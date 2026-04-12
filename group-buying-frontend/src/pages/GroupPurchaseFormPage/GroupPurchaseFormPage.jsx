import { useParams, useNavigate } from "react-router-dom";
import { useGroupPurchaseForm } from "../../hooks/useGroupPurchaseForm";
import GroupPurchaseForm from "../../components/GroupPurchase/GroupPurchaseForm";
import "./GroupPurchaseFormPage.css";

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
            onSuccess: () => {
                navigate("/group-purchases");
            },
        });
    };

    return (
        <div>
            <h2>{isEditMode ? "공동구매 수정" : "공동구매 생성"}</h2>

            <GroupPurchaseForm
                initialData={data}
                isEditMode={isEditMode}
                onSubmit={handleSubmit}
                isSubmitting={isSubmitting}
            />
        </div>
    );
};

export default GroupPurchaseFormPage;