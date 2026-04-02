import { useState } from "react";
import "./SearchBar.css";

export default function SearchBar({ keyword, setKeyword, setPage }) {

    const [input, setInput] = useState(keyword);

    const handleSearch = () => {
        setPage(0);
        setKeyword(input);
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleSearch();
        }
    };

    const handleReset = () => {
        setInput("");
        setPage(0);
        setKeyword("");
    };

    return (
        <div className="search-bar">

            <input
                className="search-input"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="상품 검색"
            />

            <button
                className="search-button"
                onClick={handleSearch}
            >
                검색
            </button>
            <button onClick={handleReset} className="search-reset-button">
                검색어 리셋
            </button>
        </div>

    );
}