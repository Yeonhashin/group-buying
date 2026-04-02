import { BrowserRouter, Routes, Route } from "react-router-dom";

import SignupPage from "./pages/RegisterPage.jsx";
import LoginPage from "./pages/LoginPage";
import ProductListPage from "./pages/ProductListPage/ProductListPage.jsx";
import ProductDetailPage from "./pages/ProductDetailPage";
import ProductCreatePage from "./pages/ProductCreatePage/ProductCreatePage.jsx";
import ProductEditPage from "./pages/ProductEditPage/ProductEditPage.jsx";

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
                <Route
                    path="/products/create"
                    element={
                        <ProtectedRoute>
                            <ProductCreatePage />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/products/edit/:id"
                    element={
                        <ProtectedRoute>
                            <ProductEditPage />
                        </ProtectedRoute>
                    }
                />
            </Routes>


        </BrowserRouter>
    );
}

export default App;