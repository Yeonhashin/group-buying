import { BrowserRouter, Routes, Route } from "react-router-dom";

import SignupPage from "./pages/RegisterPage.jsx";
import LoginPage from "./pages/LoginPage";
import ProductListPage from "./pages/ProductListPage";
import ProductDetailPage from "./pages/ProductDetailPage";

import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/login" element={<LoginPage />} />

                {/* 공개 페이지 */}
                <Route path="/products" element={<ProductListPage />} />
                <Route path="/products/:productId" element={<ProductDetailPage />} />

                {/* 로그인 필요 */}

            </Routes>
        </BrowserRouter>
    );
}

export default App;