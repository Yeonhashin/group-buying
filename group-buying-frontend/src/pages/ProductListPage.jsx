import ProductCard from "../components/ProductCard";
import { useProducts } from "../hooks/useProducts.js";

const ProductListPage = () => {

    const { products, loading, error } = useProducts();

    if (loading) {
        return <div>상품을 불러오는 중...</div>;
    }

    if (error) {
        return <div>상품 조회 실패</div>;
    }

    return (

        <div style={styles.container}>

            <h1>상품 목록</h1>

            <div style={styles.list}>

                {products.map((product) => (
                    <ProductCard
                        key={product.id}
                        product={product}
                    />
                ))}

            </div>

        </div>

    );
};

const styles = {
    container: {
        padding: "40px"
    },

    list: {
        display: "flex",
        flexWrap: "wrap"
    }
};

export default ProductListPage;