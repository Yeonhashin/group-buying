import { BrowserRouter, Routes, Route } from "react-router-dom";

import SignupPage from "./pages/RegisterPage.jsx";
import LoginPage from "./pages/LoginPage";
import ProductListPage from "./pages/ProductListPage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route
                    path="/products"
                    element={
                        <ProtectedRoute>
                            <ProductListPage />
                        </ProtectedRoute>
                    }
                />

            </Routes>
        </BrowserRouter>
    );
}

export default App;