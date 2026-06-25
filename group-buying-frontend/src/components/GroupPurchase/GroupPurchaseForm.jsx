import { useEffect, useState, useMemo } from "react";
import { useProducts } from "../../hooks/useProducts";
import { useAuthStore } from "../../store/useAuthStore";
import toast from "react-hot-toast";

const inputClass = "w-full px-4 py-2.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent";
const labelClass = "block text-sm font-medium text-gray-700 mb-1";

const GroupPurchaseForm = ({ initialData, isEditMode, onSubmit, isSubmitting }) => {
    const [form, setForm] = useState({
        productId: "", title: "", details: "",
        targetPrice: "", targetParticipants: "", startDt: "", endDt: "",
    });

    const user = useAuthStore((state) => state.user);
    const { data } = useProducts({ page: 0, size: 100 });
    const products = (data?.content || []).filter((p) => p.userId === user?.id);

    useEffect(() => {
        if (isEditMode && initialData) {
            setForm({
                productId: initialData.productId || "",
                title: initialData.title || "",
                details: initialData.details || "",
                targetPrice: initialData.targetPrice || "",
                targetParticipants: initialData.targetParticipants || "",
                startDt: initialData.startDt?.slice(0, 10) || "",
                endDt: initialData.endDt?.slice(0, 10) || "",
            });
        }
    }, [isEditMode, initialData]);

    const selectedProduct = useMemo(
        () => products.find((p) => p.id === Number(form.productId)),
        [form.productId, products]
    );

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => {
            if (name === "productId") {
                const selected = products.find((p) => p.id === Number(value));
                return { ...prev, productId: value, targetPrice: selected?.price || "" };
            }
            return { ...prev, [name]: value };
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!form.productId) { toast.error("상품을 선택해주세요."); return; }
        if (!form.title.trim()) { toast.error("제목을 입력해주세요."); return; }
        if (!form.details.trim()) { toast.error("설명을 입력해주세요."); return; }
        if (!form.targetPrice || Number(form.targetPrice) <= 0) { toast.error("목표 가격은 1원 이상이어야 합니다."); return; }
        if (!form.targetParticipants || Number(form.targetParticipants) < 2) { toast.error("목표 인원은 2명 이상이어야 합니다."); return; }
        if (!form.startDt) { toast.error("시작일을 입력해주세요."); return; }
        if (!form.endDt) { toast.error("종료일을 입력해주세요."); return; }

        const today = new Date().toISOString().slice(0, 10);
        if (!isEditMode && form.startDt < today) { toast.error("시작일은 오늘 이후여야 합니다."); return; }
        if (form.endDt <= form.startDt) { toast.error("종료일은 시작일보다 이후여야 합니다."); return; }

        onSubmit({ ...form, title: form.title.trim(), details: form.details.trim() });
    };

    return (
        <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
            <form className="flex flex-col gap-5" onSubmit={handleSubmit}>

                <div>
                    <label className={labelClass}>상품 선택</label>
                    <select name="productId" value={form.productId} onChange={handleChange} className={inputClass} disabled={products.length === 0}>
                        <option value="">{products.length === 0 ? "등록된 내 상품이 없습니다" : "상품을 선택하세요"}</option>
                        {products.map((p) => (
                            <option key={p.id} value={p.id}>{p.name}</option>
                        ))}
                    </select>
                    {products.length === 0 && (
                        <p className="mt-1 text-xs text-gray-400">공동구매를 만들려면 먼저 상품을 등록해주세요.</p>
                    )}
                </div>

                {selectedProduct && (
                    <div className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg">
                        <img
                            src={selectedProduct?.imageUrl?.startsWith('http')
                                ? selectedProduct?.imageUrl
                                : `${import.meta.env.VITE_API_URL || "http://localhost:8081"}${selectedProduct?.imageUrl}`}
                            alt={selectedProduct.name}
                            className="w-16 h-16 object-cover rounded-lg border border-gray-200"
                        />
                        <div>
                            <p className="text-sm font-medium text-gray-800">{selectedProduct.name}</p>
                            <p className="text-sm text-gray-500">{selectedProduct.price?.toLocaleString()}원</p>
                        </div>
                    </div>
                )}

                <div>
                    <label className={labelClass}>제목</label>
                    <input name="title" value={form.title} onChange={handleChange} className={inputClass} placeholder="공동구매 제목을 입력하세요" />
                </div>

                <div>
                    <label className={labelClass}>설명</label>
                    <textarea name="details" value={form.details} onChange={handleChange} className={`${inputClass} min-h-24 resize-vertical`} placeholder="공동구매에 대해 설명해주세요" />
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className={labelClass}>목표 가격 (원)</label>
                        <input type="number" name="targetPrice" value={form.targetPrice} onChange={handleChange} className={inputClass} />
                    </div>
                    <div>
                        <label className={labelClass}>목표 인원 (명)</label>
                        <input type="number" name="targetParticipants" value={form.targetParticipants} onChange={handleChange} className={inputClass} />
                    </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className={labelClass}>시작일</label>
                        <input type="date" name="startDt" value={form.startDt} onChange={handleChange} className={inputClass} />
                    </div>
                    <div>
                        <label className={labelClass}>종료일</label>
                        <input type="date" name="endDt" value={form.endDt} onChange={handleChange} className={inputClass} />
                    </div>
                </div>

                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="mt-2 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
                >
                    {isSubmitting ? "처리 중..." : isEditMode ? "수정하기" : "생성하기"}
                </button>
            </form>
        </div>
    );
};

export default GroupPurchaseForm;
