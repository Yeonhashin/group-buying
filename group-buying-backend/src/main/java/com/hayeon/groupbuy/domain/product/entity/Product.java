package com.hayeon.groupbuy.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    public static Product create(Long userId, String name, String details, String imageUrl, Integer price, Integer stock) {
        Product product = new Product();
        product.userId = userId;
        product.name = name;
        product.details = details;
        product.imageUrl = imageUrl;
        product.price = price;
        product.stock = stock;
        product.createDt = LocalDateTime.now();
        return product;
    }

    public void update(
            String name,
            String details,
            String imageUrl,
            Integer price,
            Integer stock
    ) {
        this.name = name;
        this.details = details;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
        this.updateDt = LocalDateTime.now();
    }
}