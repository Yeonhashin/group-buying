import { apiFetch } from "./apiClient";

export async function getProducts() {

    return apiFetch("/api/products", {
        method: "GET",
    });

}