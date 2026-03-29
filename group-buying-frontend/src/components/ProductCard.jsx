import { useNavigate } from "react-router-dom";

const ProductCard = ({ product }) => {
    const navigate = useNavigate();
    console.log(product);
    return (
        <div
            style={styles.card}
            onClick={() => navigate(`/products/${product.id}`)}
        >

            <img
                src={product.imageUrl}
                alt={product.name}
                style={styles.image}
            />

            <h3>{product.name}</h3>

            <p>{product.details}</p>

        </div>
    );
};

const styles = {
    card: {
        border: "1px solid #ddd",
        borderRadius: "10px",
        padding: "16px",
        width: "250px",
        margin: "10px",
        cursor: "pointer",
        transition: "transform 0.2s"
    },

    image: {
        width: "100%",
        height: "180px",
        objectFit: "cover"
    }
};

export default ProductCard;