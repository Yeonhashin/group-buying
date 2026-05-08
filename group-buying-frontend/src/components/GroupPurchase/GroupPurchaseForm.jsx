import { useEffect, useState, useMemo } from "react";
import { useProducts } from "../../hooks/useProducts";

const GroupPurchaseForm = ({
                               initialData,
                               isEditMode,
                               onSubmit,
                               isSubmitting,
                           }) => {

    /**
     * 1. form state (input 전용)
     */
    const [form, setForm] = useState({
        productId: "",
        title: "",
        details: "",
        targetPrice: "",
        targetParticipants: "",
        startDt: "",
        endDt: "",
    });

    const { data } = useProducts({ page: 0, size: 100 });
    const products = data?.content || [];

    /**
     * 2. edit 초기값 세팅
     */
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

    /**
     * 3. product derived data
     */
    const selectedProduct = useMemo(() => {
        return products.find(
            (p) => p.id === Number(form.productId)
        );
    }, [form.productId, products]);

    const productImage = selectedProduct?.imageUrl || "";

    /**
     * 4. change handler
     */
    const handleChange = (e) => {
        const { name, value } = e.target;

        setForm((prev) => {
            if (name === "productId") {
                const selected = products.find(
                    (p) => p.id === Number(value)
                );

                return {
                    ...prev,
                    productId: value,
                    targetPrice: selected?.price || "",
                };
            }

            return {
                ...prev,
                [name]: value,
            };
        });
    };

    /**
     * 5. submit (핵심: preventDefault + onSubmit만 호출)
     */
    const handleSubmit = (e) => {
        e.preventDefault(); // ❗ 필수

        onSubmit(form);
    };

    return (
        <form className="group-form" onSubmit={handleSubmit}>

            <div>
                <label>상품 선택</label>
                <select
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

            <div>
                <label>제목</label>
                <input
                    name="title"
                    value={form.title}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label>설명</label>
                <textarea
                    name="details"
                    value={form.details}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label>목표 가격</label>
                <input
                    type="number"
                    name="targetPrice"
                    value={form.targetPrice}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label>목표 인원</label>
                <input
                    type="number"
                    name="targetParticipants"
                    value={form.targetParticipants}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label>시작일</label>
                <input
                    type="date"
                    name="startDt"
                    value={form.startDt}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label>종료일</label>
                <input
                    type="date"
                    name="endDt"
                    value={form.endDt}
                    onChange={handleChange}
                />
            </div>

            {productImage && (
                <img
                    src={`http://localhost:8081${productImage}`}
                    alt="product"
                />
            )}

            <button type="submit" disabled={isSubmitting}>
                {isEditMode ? "수정" : "생성"}
            </button>

        </form>
    );
};

export default GroupPurchaseForm;