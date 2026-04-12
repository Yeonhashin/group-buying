import { useEffect, useState } from "react";
import { useProducts } from "../../hooks/useProducts";

const GroupPurchaseForm = ({
                               initialData,
                               isEditMode,
                               onSubmit,
                               isSubmitting,
                           }) => {

    const [form, setForm] = useState({
        productId: "",
        title: "",
        details: "",
        targetPrice: "",
        targetParticipants: "",
        startDt: "",
        endDt: "",
        productImage: "",
    });

    const { data } = useProducts({ page: 0, size: 100 });
    const products = data?.content || [];

    /**
     * 초기값 세팅
     */
    useEffect(() => {
        if (isEditMode && initialData) {
            setForm({
                productId: initialData.product?.id || "",
                title: initialData.title || "",
                details: initialData.details || "",
                targetPrice: initialData.targetPrice || "",
                targetParticipants: initialData.targetParticipants || "",
                startDt: initialData.startDt?.slice(0, 10) || "",
                endDt: initialData.endDt?.slice(0, 10) || "",
                productImage: initialData.product?.imageUrl || "",
            });
        }
    }, [isEditMode, initialData]);

    /**
     * 변경 핸들러
     */
    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === "productId") {
            const selected = products.find(p => p.id === Number(value));

            setForm(prev => ({
                ...prev,
                productId: value,
                targetPrice: selected?.price || "",
                productImage: selected?.imageUrl || "",
            }));
            return;
        }

        setForm(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    /**
     * submit
     */
    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(form);
    };

    return (
        <form className="group-form" onSubmit={handleSubmit}>

            <div className="group-form-group">
                <label className="group-label">상품 선택</label>
                <select
                    className="group-select"
                    name="productId"
                    value={form.productId}
                    onChange={handleChange}
                >
                    <option value="">상품 선택</option>
                    {products.map((p) => (
                        <option key={p.id} value={p.id}>
                            {p.name}
                        </option>
                    ))}
                </select>
            </div>

            <div className="group-form-group">
                <label className="group-label">제목</label>
                <input
                    className="group-input"
                    name="title"
                    value={form.title}
                    onChange={handleChange}
                />
            </div>

            <div className="group-form-group">
                <label className="group-label">설명</label>
                <textarea
                    className="group-textarea"
                    name="details"
                    value={form.details}
                    onChange={handleChange}
                />
            </div>

            <div className="group-form-group">
                <label className="group-label">목표 가격</label>
                <input
                    className="group-input"
                    type="number"
                    name="targetPrice"
                    value={form.targetPrice}
                    onChange={handleChange}
                />
            </div>

            <div className="group-form-group">
                <label className="group-label">목표 인원</label>
                <input
                    className="group-input"
                    type="number"
                    name="targetParticipants"
                    value={form.targetParticipants}
                    onChange={handleChange}
                />
            </div>

            <div className="group-form-group">
                <label className="group-label">시작일</label>
                <input
                    className="group-input"
                    type="date"
                    name="startDt"
                    value={form.startDt}
                    onChange={handleChange}
                />
            </div>

            <div className="group-form-group">
                <label className="group-label">종료일</label>
                <input
                    className="group-input"
                    type="date"
                    name="endDt"
                    value={form.endDt}
                    onChange={handleChange}
                />
            </div>

            {form.productImage && (
                <div className="group-image-preview">
                    <img
                        className="group-image"
                        src={`http://localhost:8081${form.productImage}`}
                    />
                </div>
            )}

            <button
                className="group-submit-btn"
                type="submit"
                disabled={isSubmitting}
            >
                {isEditMode ? "수정" : "생성"}
            </button>

        </form>
    );
};

export default GroupPurchaseForm;