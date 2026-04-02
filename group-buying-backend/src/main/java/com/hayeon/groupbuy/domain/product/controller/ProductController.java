package com.hayeon.groupbuy.domain.product.controller;

import com.hayeon.groupbuy.domain.product.dto.request.ProductCreateRequest;
import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.dto.response.ProductPageResponse;
import com.hayeon.groupbuy.domain.product.service.ProductService;
import com.hayeon.groupbuy.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> save(
            @RequestParam String name,
            @RequestParam String details,
            @RequestParam Integer price,
            @RequestParam Integer stock,
            @RequestParam MultipartFile file
    ) {
        productService.save(name, details, price, stock, file);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> edit(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String details,
            @RequestParam Integer price,
            @RequestParam Integer stock,
            @RequestParam(required = false) MultipartFile file
    ) {
        productService.edit(id, name, details, price, stock, file);
        return ResponseEntity.ok(CommonResponse.success(null));
    }


    @GetMapping
    public ResponseEntity<CommonResponse<ProductPageResponse>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(
                CommonResponse.success(productService.getProductList(page, size, keyword))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductResponse>> getProduct(
            @PathVariable Long id
    ) {
        ProductResponse product = productService.getProduct(id);
        return ResponseEntity.ok(CommonResponse.success(product));
    }
}