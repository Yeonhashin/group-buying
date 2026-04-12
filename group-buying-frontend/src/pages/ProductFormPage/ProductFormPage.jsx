import { useNavigate, useParams } from "react-router-dom";
import { useProductForm } from "../../hooks/useProductForm";
import "./ProductFormPage.css";

export default function ProductFormPage({ mode }) {
    const navigate = useNavigate();
    const { productId } = useParams();

    const {
        name, setName,
        details, setDetails,
        price, setPrice,
        stock, setStock,
        preview,
        fileInputRef,
        handleFileChange,
        handleRemoveImage,
        handleSubmit,
        isSubmitting,
    } = useProductForm({ mode, productId });

    return (
        <div className="product-form-container">
            <h2 className="product-form-title">
                {mode === "create" ? "상품 등록" : "상품 수정"}
            </h2>

            <form className="product-form" onSubmit={(e) => handleSubmit(e, navigate)}>

                {/* 상품명 */}
                <div className="form-group">
                    <label htmlFor="name">상품명</label>
                    <input
                        id="name"
                        className="product-input"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                </div>

                {/* 설명 */}
                <div className="form-group">
                    <label htmlFor="details">상품 설명</label>
                    <textarea
                        id="details"
                        className="product-textarea"
                        value={details}
                        onChange={(e) => setDetails(e.target.value)}
                    />
                </div>

                {/* 가격 */}
                <div className="form-group">
                    <label htmlFor="price">가격</label>
                    <input
                        id="price"
                        type="number"
                        className="product-input"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                    />
                </div>

                {/* 재고 */}
                <div className="form-group">
                    <label htmlFor="stock">재고 수량</label>
                    <input
                        id="stock"
                        type="number"
                        className="product-input"
                        value={stock}
                        onChange={(e) => setStock(e.target.value)}
                    />
                </div>

                {/* 이미지 */}
                <div className="form-group">
                    <label htmlFor="file">상품 이미지</label>
                    <input
                        id="file"
                        className="product-file"
                        type="file"
                        ref={fileInputRef}
                        onChange={handleFileChange}
                    />

                    {preview && (
                        <div className="product-image-preview">
                            <img
                                className="product-image"
                                src={preview}
                                alt="preview"
                            />
                            <button
                                className="image-remove-btn"
                                type="button"
                                onClick={handleRemoveImage}
                            >
                                이미지 제거
                            </button>
                        </div>
                    )}
                </div>

                {/* 제출 */}
                <button
                    className="submit-btn"
                    type="submit"
                    disabled={isSubmitting}
                >
                    {isSubmitting
                        ? "처리 중..."
                        : mode === "create"
                            ? "등록"
                            : "수정"}
                </button>

            </form>
        </div>
    );
}