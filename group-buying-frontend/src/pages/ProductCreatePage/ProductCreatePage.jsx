import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { createProduct } from "../../api/productApi.js";

export default function ProductCreatePage() {

    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [details, setDetails] = useState("");
    const [file, setFile] = useState(null);
    const [price, setPrice] = useState("");
    const [stock, setStock] = useState("");
    const [preview, setPreview] = useState(null);
    const [loading, setLoading] = useState(false);

    const fileInputRef = useRef(null);

    const handleFileChange = (e) => {

        const selectedFile = e.target.files[0];

        if (!selectedFile) return;

        // 기존 미리보기 URL 제거
        if (preview) {
            URL.revokeObjectURL(preview);
        }

        setFile(selectedFile);
        setPreview(URL.createObjectURL(selectedFile));
    };

    const handleRemoveImage = () => {

        if (preview) {
            URL.revokeObjectURL(preview);
        }

        setFile(null);
        setPreview(null);

        if (fileInputRef.current) {
            fileInputRef.current.value = "";
        }
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!name || !details || !file || !price || !stock) {
            alert("미입력 항목이 있습니다.");
            return;
        }

        const formData = new FormData();
        formData.append("name", name);
        formData.append("details", details);
        formData.append("price", price);
        formData.append("stock", stock);
        formData.append("file", file);

        setLoading(true);

        try {

            await createProduct(formData);

            alert("성공적으로 상품을 등록하였습니다.");
            navigate("/products");

        } catch (err) {

            console.error(err);

            if (err.message === "UNAUTHORIZED") {
                alert("로그인이 필요합니다.");
                navigate("/login");
                return;
            }

            alert("상품 등록을 실패하였습니다. 다시 시도해주세요.");

        } finally {

            setLoading(false);

        }
    };

    return (

        <div style={{ maxWidth: "600px", margin: "40px auto" }}>

            <h2>상품 등록</h2>

            <form onSubmit={handleSubmit}>

                <div>
                    <label>상품명</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="상품명을 입력하세요"
                    />
                </div>

                <div>
                    <label>상품 설명</label>
                    <textarea
                        value={details}
                        onChange={(e) => setDetails(e.target.value)}
                        placeholder="상품 설명을 입력하세요"
                    />
                </div>

                <div>
                    <label>가격</label>
                    <input
                        type="number"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        placeholder="가격을 입력하세요"
                    />
                </div>

                <div>
                    <label>재고</label>
                    <input
                        type="number"
                        value={stock}
                        onChange={(e) => setStock(e.target.value)}
                        placeholder="재고 수량을 입력하세요"
                    />
                </div>

                <div>

                    <label>상품 이미지</label>

                    <input
                        type="file"
                        accept="image/*"
                        ref={fileInputRef}
                        onChange={handleFileChange}
                    />

                    {preview && (

                        <div style={{marginTop: "10px"}}>

                            <img
                                src={preview}
                                alt="preview"
                                style={{
                                    width: "200px",
                                    borderRadius: "8px"
                                }}
                            />

                            <div>

                                <button
                                    type="button"
                                    onClick={handleRemoveImage}
                                    style={{
                                        marginTop: "10px",
                                        background: "#ff4d4f",
                                        color: "white",
                                        border: "none",
                                        padding: "6px 12px",
                                        cursor: "pointer"
                                    }}
                                >
                                    이미지 삭제
                                </button>

                            </div>

                        </div>

                    )}

                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "등록 중..." : "상품 등록"}
                </button>

            </form>

        </div>

    );

}