package com.hayeon.groupbuy.domain.product.entity;

import com.hayeon.groupbuy.domain.user.entity.User; // 🔥 추가

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDt = LocalDateTime.now();
    }

    public void delete() {
        this.deleteDt = LocalDateTime.now();
    }

    public static Product create(User user, String name, String details, String imageUrl, Integer price, Integer stock) {
        Product product = new Product();
        product.user = user;
        product.name = name;
        product.details = details;
        product.imageUrl = imageUrl;
        product.price = price;
        product.stock = stock;
        return product;
    }

    public void update(String name, String details, String imageUrl, Integer price, Integer stock) {
        this.name = name;
        this.details = details;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
    }
}