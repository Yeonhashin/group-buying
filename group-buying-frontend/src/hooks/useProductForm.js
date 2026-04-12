import { useState, useEffect, useRef } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createProduct, updateProduct, getProduct } from "../api/productApi";

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

        if (!name || !details || !price || !stock || (mode === "create" && !file)) {
            alert("미입력 항목 존재");
            return;
        }

        const formData = new FormData();
        formData.append("name", name);
        formData.append("details", details);
        formData.append("price", price);
        formData.append("stock", stock);

        if (file) {
            formData.append("file", file);
        }

        try {
            await mutation.mutateAsync(formData);
            alert(mode === "create" ? "등록 완료" : "수정 완료");
            navigate("/products");
        } catch (e) {
            console.error(e);
            alert("요청 실패");
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