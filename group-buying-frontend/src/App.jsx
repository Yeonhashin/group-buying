import { BrowserRouter, Routes, Route } from "react-router-dom";

import SignupPage from "./pages/RegisterPage.jsx";
import LoginPage from "./pages/LoginPage";
import ProductListPage from "./pages/ProductListPage/ProductListPage.jsx";
import ProductDetailPage from "./pages/ProductDetailPage/ProductDetailPage.jsx";
import ProductFormPage from "./pages/ProductFormPage/ProductFormPage.jsx";
import GroupPurchaseListPage from "./pages/GroupPurchaseListPage/GroupPurchaseListPage.jsx";
import GroupPurchaseDetailPage from "./pages/GroupPurchaseDetailPage/GroupPurchaseDetailPage.jsx";
import GroupPurchaseFormPage from "./pages/GroupPurchaseFormPage/GroupPurchaseFormPage.jsx";

import ProtectedRoute from "./components/ProtectedRoute";
import { Toaster } from "react-hot-toast";

function App() {
    return (
        <BrowserRouter>
            <Toaster position="top-center" />

            <Routes>
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/login" element={<LoginPage />} />

                {/* 공개 페이지 */}
                <Route path="/products" element={<ProductListPage />} />
                <Route path="/products/:productId" element={<ProductDetailPage />} />

                <Route path="/group-purchases" element={<GroupPurchaseListPage />} />
                <Route path="/group-purchases/:groupPurchaseId" element={<GroupPurchaseDetailPage />} />

                {/* 로그인 필요 */}
                <Route
                    path="/products/create"
                    element={
                        <ProtectedRoute>
                            <ProductFormPage mode="create" />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/products/:productId/edit"
                    element={
                        <ProtectedRoute>
                            <ProductFormPage mode="edit" />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/group-purchases/create"
                    element={
                        <ProtectedRoute>
                            <GroupPurchaseFormPage mode="create" />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/group-purchases/:groupPurchaseId/edit"
                    element={
                        <ProtectedRoute>
                            <GroupPurchaseFormPage mode="edit" />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;