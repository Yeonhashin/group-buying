import { useEffect, useState } from "react";
import { getProducts } from "../api/productApi";

export default function ProductListPage() {

    const [products, setProducts] = useState([]);

    useEffect(() => {

        async function fetchProducts() {

            try {

                const res = await getProducts();

                setProducts(res.data);

            } catch (err) {

                console.error(err);

            }

        }

        fetchProducts();

    }, []);

    return (
        <div>
            <h1>상품 목록</h1>

            {products.map((p) => (
                <div key={p.id}>{p.name}</div>
            ))}

        </div>
    );
}