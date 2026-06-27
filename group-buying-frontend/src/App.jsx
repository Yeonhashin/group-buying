import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import SignupPage from "./pages/RegisterPage.jsx";
import LoginPage from "./pages/LoginPage";
import ProductListPage from "./pages/ProductListPage/ProductListPage.jsx";
import ProductDetailPage from "./pages/ProductDetailPage/ProductDetailPage.jsx";
import ProductFormPage from "./pages/ProductFormPage/ProductFormPage.jsx";
import GroupPurchaseListPage from "./pages/GroupPurchaseListPage/GroupPurchaseListPage.jsx";
import GroupPurchaseDetailPage from "./pages/GroupPurchaseDetailPage/GroupPurchaseDetailPage.jsx";
import GroupPurchaseFormPage from "./pages/GroupPurchaseFormPage/GroupPurchaseFormPage.jsx";
import MyPage from "./pages/MyPage/MyPage.jsx";
import SellerMyPage from "./pages/SellerMyPage/SellerMyPage.jsx";

import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import { Toaster } from "react-hot-toast";

function App() {
    return (
        <BrowserRouter>
            <Toaster position="top-center" />

            <Routes>
                <Route path="/" element={<Navigate to="/group-purchases" replace />} />

                {/* 헤더 없는 인증 페이지 */}
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/login" element={<LoginPage />} />

                {/* 공통 레이아웃 적용 페이지 */}
                <Route path="/products" element={<Layout><ProductListPage /></Layout>} />
                <Route path="/products/:productId" element={<Layout><ProductDetailPage /></Layout>} />
                <Route path="/group-purchases" element={<Layout><GroupPurchaseListPage /></Layout>} />
                <Route path="/group-purchases/:groupPurchaseId" element={<Layout><GroupPurchaseDetailPage /></Layout>} />

                <Route
                    path="/mypage"
                    element={
                        <ProtectedRoute>
                            <Layout><MyPage /></Layout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/seller/mypage"
                    element={
                        <ProtectedRoute>
                            <Layout><SellerMyPage /></Layout>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/products/create"
                    element={
                        <ProtectedRoute>
                            <Layout><ProductFormPage mode="create" /></Layout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/products/:productId/edit"
                    element={
                        <ProtectedRoute>
                            <Layout><ProductFormPage mode="edit" /></Layout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/group-purchases/create"
                    element={
                        <ProtectedRoute>
                            <Layout><GroupPurchaseFormPage mode="create" /></Layout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/group-purchases/:groupPurchaseId/edit"
                    element={
                        <ProtectedRoute>
                            <Layout><GroupPurchaseFormPage mode="edit" /></Layout>
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;