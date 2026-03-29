import { useParams } from "react-router-dom";
import { useProductDetail } from "../hooks/useProductDetail";

function ProductDetailPage() {

    const { productId } = useParams();
    const { product, loading, error } = useProductDetail(productId);

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
                    src={product.imageUrl}
                    alt={product.name}
                    style={styles.image}
                />

                <h1 style={styles.name}>
                    {product.name}
                </h1>

                <p style={styles.description}>
                    {product.details}
                </p>

            </div>

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