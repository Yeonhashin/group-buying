import { useState, useEffect, useRef } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createProduct, updateProduct, getProduct } from "../api/productApi";
import toast from "react-hot-toast";

export const useProductForm = ({ mode, productId }) => {
    const queryClient = useQueryClient();

    const [name, setName] = useState("");
    const [details, setDetails] = useState("");
    const [price, setPrice] = useState("");
    const [stock, setStock] = useState("");
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);

    const fileInputRef = useRef(null);

    /**
     * 1. 상세 조회
     */
    const { data: product } = useQuery({
        queryKey: ["product", productId],
        queryFn: () => getProduct(productId),
        enabled: mode === "edit" && !!productId,
    });

    /**
     * 2. 기존 데이터 세팅
     */
    useEffect(() => {
        if (!product) return;

        setName(product.name);
        setDetails(product.details);
        setPrice(product.price);
        setStock(product.stock);
        setPreview(product.imageUrl);
    }, [product]);

    /**
     * 3. 파일 처리
     */
    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (!selectedFile) return;

        if (!selectedFile.type.startsWith("image/")) {
            toast.error("이미지 파일만 업로드할 수 있습니다.");
            e.target.value = "";
            return;
        }

        if (preview) URL.revokeObjectURL(preview);

        setFile(selectedFile);
        setPreview(URL.createObjectURL(selectedFile));
    };

    const handleRemoveImage = () => {
        if (preview) URL.revokeObjectURL(preview);

        setFile(null);
        setPreview(null);

        if (fileInputRef.current) {
            fileInputRef.current.value = "";
        }
    };

    /**
     * 4. mutation
     */
    const mutation = useMutation({
        mutationFn: (formData) => {
            if (mode === "create") return createProduct(formData);
            return updateProduct(productId, formData);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["products"] });
            if (mode === "edit") {
                queryClient.invalidateQueries({ queryKey: ["product", productId] });
            }
        },
    });

    /**
     * 5. submit
     */
    const handleSubmit = async (e, navigate) => {
        e.preventDefault();

        if (!name.trim()) { toast.error("상품명을 입력해주세요."); return; }
        if (!details.trim()) { toast.error("상품 설명을 입력해주세요."); return; }
        if (!price || Number(price) <= 0) { toast.error("가격은 1원 이상이어야 합니다."); return; }
        if (stock === "" || Number(stock) < 0) { toast.error("재고 수량은 0개 이상이어야 합니다."); return; }
        if (mode === "create" && !file) { toast.error("상품 이미지를 등록해주세요."); return; }

        const formData = new FormData();
        formData.append("name", name.trim());
        formData.append("details", details.trim());
        formData.append("price", price);
        formData.append("stock", stock);

        if (file) {
            formData.append("file", file);
        }

        try {
            await mutation.mutateAsync(formData);
            toast.success(mode === "create" ? "상품이 등록되었습니다." : "상품이 수정되었습니다.");
            navigate("/products");
        } catch (e) {
            console.error(e);
            toast.error(e?.response?.data?.message || "요청에 실패했습니다.");
        }
    };

    return {
        name, setName,
        details, setDetails,
        price, setPrice,
        stock, setStock,
        preview,
        fileInputRef,
        handleFileChange,
        handleRemoveImage,
        handleSubmit,
        isSubmitting: mutation.isPending,
    };
};