import "./Pagination.css";

const Pagination = ({ page, totalPages, setPage }) => {

    const pages = [];

    for (let i = 0; i < totalPages; i++) {
        pages.push(i);
    }

    return (
        <div className="pagination">

            {pages.map(p => (
                <button
                    key={p}
                    className={`page-btn ${p === page ? "active" : ""}`}
                    onClick={() => setPage(p)}
                >
                    {p + 1}
                </button>
            ))}

        </div>
    );
};

export default Pagination;