import { useParams } from "react-router-dom";
import { useProductDetail } from "../hooks/useProductDetail";
import { useNavigate } from "react-router-dom";

function ProductDetailPage() {

    const { productId } = useParams();
    const { product, loading, error } = useProductDetail(productId);
    const token = localStorage.getItem("accessToken");
    const navigate = useNavigate();

    if (loading) {
        return <div style={styles.center}>로딩중...</div>;
    }

    if (error) {
        return <div style={styles.center}>상품 조회 실패</div>;
    }

    if (!product) {
        return <div style={styles.center}>상품이 존재하지 않습니다.</div>;
    }

    return (
        <div style={styles.container}>

            <div style={styles.card}>

                <img
                    src={`http://localhost:8081${product.imageUrl}`}
                    alt={product.name}
                    style={styles.image}
                />

                <h1 style={styles.name}>
                    {product.name}
                </h1>

                <p style={styles.description}>
                    {product.details}
                </p>

                <p>가격 : {product.price.toLocaleString()}원</p>
                <p>재고 : {product.stock}개</p>

            </div>

            {token && (
                <button onClick={() => navigate(`/products/edit/${productId}`)}>
                    수정하기
                </button>
            )}

        </div>
    );
}

const styles = {

    container: {
        display: "flex",
        justifyContent: "center",
        marginTop: "40px"
    },

    card: {
        width: "500px",
        border: "1px solid #ddd",
        borderRadius: "12px",
        padding: "24px"
    },

    image: {
        width: "100%",
        height: "320px",
        objectFit: "cover",
        borderRadius: "8px"
    },

    name: {
        marginTop: "20px"
    },

    price: {
        fontSize: "22px",
        fontWeight: "bold",
        marginTop: "10px"
    },

    description: {
        marginTop: "20px",
        lineHeight: "1.6"
    },

    center: {
        textAlign: "center",
        marginTop: "40px"
    }

};

export default ProductDetailPage;