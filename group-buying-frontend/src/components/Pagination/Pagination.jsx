const Pagination = ({ page, totalPages, setPage }) => {
    if (totalPages <= 1) return null;

    return (
        <div className="flex justify-center items-center gap-1 mt-12 mb-8">
            <button
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                className="px-3 py-1.5 text-sm rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            >
                이전
            </button>

            {Array.from({ length: totalPages }, (_, i) => (
                <button
                    key={i}
                    onClick={() => setPage(i)}
                    className={`w-9 h-9 text-sm rounded-lg font-medium transition-colors ${
                        i === page
                            ? "bg-indigo-600 text-white border border-indigo-600"
                            : "border border-gray-300 text-gray-600 hover:bg-gray-100"
                    }`}
                >
                    {i + 1}
                </button>
            ))}

            <button
                disabled={page + 1 >= totalPages}
                onClick={() => setPage(page + 1)}
                className="px-3 py-1.5 text-sm rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            >
                다음
            </button>
        </div>
    );
};

export default Pagination;
