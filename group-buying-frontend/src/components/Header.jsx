import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";

export default function Header() {
    const { isLoggedIn, logout } = useAuthStore();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const isActive = (path) => location.pathname.startsWith(path);

    return (
        <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
            <div className="max-w-5xl mx-auto px-4 h-16 flex items-center justify-between">

                {/* 로고 */}
                <Link to="/products" className="text-xl font-bold text-indigo-600 hover:text-indigo-700">
                    공동구매
                </Link>

                {/* 네비게이션 */}
                <nav className="flex items-center gap-6">
                    <Link
                        to="/products"
                        className={`text-sm font-medium transition-colors ${
                            isActive("/products")
                                ? "text-indigo-600 border-b-2 border-indigo-600 pb-0.5"
                                : "text-gray-600 hover:text-gray-900"
                        }`}
                    >
                        상품 목록
                    </Link>
                    <Link
                        to="/group-purchases"
                        className={`text-sm font-medium transition-colors ${
                            isActive("/group-purchases")
                                ? "text-indigo-600 border-b-2 border-indigo-600 pb-0.5"
                                : "text-gray-600 hover:text-gray-900"
                        }`}
                    >
                        공동구매
                    </Link>
                </nav>

                {/* 우측 액션 */}
                <div className="flex items-center gap-3">
                    {isLoggedIn ? (
                        <>
                            <Link
                                to="/mypage"
                                className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors"
                            >
                                마이페이지
                            </Link>
                            <button
                                onClick={handleLogout}
                                className="text-sm font-medium px-4 py-1.5 rounded-full border border-gray-300 text-gray-600 hover:bg-gray-100 transition-colors"
                            >
                                로그아웃
                            </button>
                        </>
                    ) : (
                        <>
                            <Link
                                to="/login"
                                className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors"
                            >
                                로그인
                            </Link>
                            <Link
                                to="/signup"
                                className="text-sm font-medium px-4 py-1.5 rounded-full bg-indigo-600 text-white hover:bg-indigo-700 transition-colors"
                            >
                                회원가입
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
