import ProductCard from "../ProductCard/ProductCard.jsx";

const ProductGrid = ({ products }) => {
    if (products.length === 0) {
        return <p className="text-center py-20 text-gray-400">상품이 없습니다.</p>;
    }

    return (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-5">
            {products.map(product => (
                <ProductCard key={product.id} product={product} />
            ))}
        </div>
    );
};

export default ProductGrid;
