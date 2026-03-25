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

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    public static Product create(Long userId, String name, String details, String imageUrl) {
        Product product = new Product();
        product.userId = userId;
        product.name = name;
        product.details = details;
        product.imageUrl = imageUrl;
        product.createDt = LocalDateTime.now();
        return product;
    }
}