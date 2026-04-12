const ParticipantList = ({ participants }) => {
    return (
        <div style={{ border: "1px solid #ddd", padding: "20px" }}>
            <h3>참여자 목록</h3>

            {participants.length === 0 ? (
                <p>참여자가 없습니다.</p>
            ) : (
                <ul>
                    {participants.map((p) => (
                        <li key={p.userId}>
                            {p.username}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ParticipantList;