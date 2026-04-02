import { useEffect, useState, useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useProductEdit } from "../../hooks/useProductEdit";

export default function ProductEditPage() {

    const { id } = useParams();
    const navigate = useNavigate();

    const { product, editProduct } = useProductEdit(id);
    const [name, setName] = useState("");
    const [details, setDetails] = useState("");
    const [price, setPrice] = useState(0);
    const [stock, setStock] = useState(0);
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);

    const fileInputRef = useRef(null);

    // 기존 데이터 세팅
    useEffect(() => {

        if (!product) return;

        setName(product.name);
        setDetails(product.details);
        setPrice(product.price);
        setStock(product.stock);
        setPreview(product.imageUrl);

    }, [product]);

    // 파일 변경
    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (!selectedFile) return;

        if (preview) URL.revokeObjectURL(preview);

        setFile(selectedFile);
        setPreview(URL.createObjectURL(selectedFile));
    };

    // 이미지 제거
    const handleRemoveImage = () => {
        if (preview) URL.revokeObjectURL(preview);

        setFile(null);
        setPreview(null);

        if (fileInputRef.current) {
            fileInputRef.current.value = "";
        }
    };

    // 수정 요청
    const handleSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData();
        formData.append("id", id);
        formData.append("name", name);
        formData.append("details", details);
        formData.append("price", Number(price));
        formData.append("stock", Number(stock));

        if (file) {
            formData.append("file", file);
        }

        try {
            await editProduct(formData);
            alert("상품 수정 완료");
            navigate("/products");
        } catch (e) {
            console.error(e);
            alert("상품 수정 실패");
        }
    };

    return (
        <div style={{ maxWidth: "600px", margin: "40px auto" }}>
            <h2>상품 수정</h2>

            <form onSubmit={handleSubmit}>

                <input value={name} onChange={(e) => setName(e.target.value)} />

                <textarea value={details} onChange={(e) => setDetails(e.target.value)} />

                <input type="number" value={price} onChange={(e) => setPrice(e.target.value)} />

                <input type="number" value={stock} onChange={(e) => setStock(e.target.value)} />

                <input type="file" ref={fileInputRef} onChange={handleFileChange} />

                {preview && (
                    <div>
                        <img src={preview} style={{ width: "200px" }} />
                        <button type="button" onClick={handleRemoveImage}>
                            이미지 제거
                        </button>
                    </div>
                )}

                <button type="submit">수정하기</button>
            </form>
        </div>
    );
}