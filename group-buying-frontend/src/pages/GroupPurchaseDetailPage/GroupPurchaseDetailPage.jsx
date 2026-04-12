import { useParams } from "react-router-dom";
import { useGroupPurchase } from "../../hooks/useGroupPurchase";

import GroupPurchaseDetail from "../../components/GroupPurchase/GroupPurchaseDetail";
import ParticipantList from "../../components/GroupPurchase/ParticipantList";
import ParticipationPanel from "../../components/GroupPurchase/ParticipationPanel";

const GroupPurchaseDetailPage = () => {
    const { groupPurchaseId } = useParams();

    const { data, isLoading, isError } = useGroupPurchase(groupPurchaseId);

    if (isLoading) return <div>로딩 중...</div>;
    if (isError) return <div>에러 발생</div>;

    return (
        <div style={{ padding: "20px" }}>
            <GroupPurchaseDetail groupPurchase={data} />

            <ParticipationPanel groupPurchase={data} />

            <ParticipantList participants={data.participants || []} />
        </div>
    );
};

export default GroupPurchaseDetailPage;