const ParticipantList = ({ participants }) => {
    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm">
            <h3 className="text-base font-semibold text-gray-900 mb-3">
                참여자 목록 <span className="text-indigo-600">({participants.length}명)</span>
            </h3>

            {participants.length === 0 ? (
                <p className="text-sm text-gray-400">아직 참여자가 없습니다.</p>
            ) : (
                <ul className="divide-y divide-gray-100">
                    {participants.map((p) => (
                        <li key={p.userId} className="py-2 text-sm text-gray-700">
                            {p.username}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ParticipantList;
