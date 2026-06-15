import { useState } from "react";

export default function SearchBar({ keyword, setKeyword, setPage }) {
    const [input, setInput] = useState(keyword);

    const handleSearch = () => {
        setPage(0);
        setKeyword(input);
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") handleSearch();
    };

    const handleReset = () => {
        setInput("");
        setPage(0);
        setKeyword("");
    };

    return (
        <div className="flex items-center gap-2">
            <input
                className="flex-1 max-w-md px-4 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent placeholder-gray-400"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="상품명으로 검색"
            />
            <button
                className="px-4 py-2 text-sm font-medium bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
                onClick={handleSearch}
            >
                검색
            </button>
            {input && (
                <button
                    onClick={handleReset}
                    className="px-4 py-2 text-sm font-medium bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition-colors"
                >
                    초기화
                </button>
            )}
        </div>
    );
}
