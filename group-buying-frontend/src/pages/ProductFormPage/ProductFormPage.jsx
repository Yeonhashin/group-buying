import { useNavigate, useParams } from "react-router-dom";
import { useProductForm } from "../../hooks/useProductForm";

const inputClass = "w-full px-4 py-2.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent";
const labelClass = "block text-sm font-medium text-gray-700 mb-1";

export default function ProductFormPage({ mode }) {
    const navigate = useNavigate();
    const { productId } = useParams();

    const {
        name, setName, details, setDetails, price, setPrice, stock, setStock,
        preview, fileInputRef, handleFileChange, handleRemoveImage, handleSubmit, isSubmitting,
    } = useProductForm({ mode, productId });

    return (
        <div className="max-w-xl mx-auto">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">
                {mode === "create" ? "상품 등록" : "상품 수정"}
            </h1>

            <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
                <form className="flex flex-col gap-5" onSubmit={(e) => handleSubmit(e, navigate)}>

                    <div>
                        <label className={labelClass}>상품명</label>
                        <input className={inputClass} value={name} onChange={(e) => setName(e.target.value)} />
                    </div>

                    <div>
                        <label className={labelClass}>상품 설명</label>
                        <textarea
                            className={`${inputClass} min-h-28 resize-vertical`}
                            value={details}
                            onChange={(e) => setDetails(e.target.value)}
                        />
                    </div>

                    <div>
                        <label className={labelClass}>가격 (원)</label>
                        <input type="number" className={inputClass} value={price} onChange={(e) => setPrice(e.target.value)} />
                    </div>

                    <div>
                        <label className={labelClass}>재고 수량</label>
                        <input type="number" className={inputClass} value={stock} onChange={(e) => setStock(e.target.value)} />
                    </div>

                    <div>
                        <label className={labelClass}>상품 이미지</label>
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            className="text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-medium file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
                        />
                        {preview && (
                            <div className="mt-3 flex flex-col gap-2">
                                <img src={preview} alt="미리보기" className="w-40 h-40 object-cover rounded-lg border border-gray-200" />
                                <button
                                    type="button"
                                    onClick={handleRemoveImage}
                                    className="w-fit text-xs px-3 py-1.5 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors"
                                >
                                    이미지 제거
                                </button>
                            </div>
                        )}
                    </div>

                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className="mt-2 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
                    >
                        {isSubmitting ? "처리 중..." : mode === "create" ? "등록하기" : "수정하기"}
                    </button>
                </form>
            </div>
        </div>
    );
}
