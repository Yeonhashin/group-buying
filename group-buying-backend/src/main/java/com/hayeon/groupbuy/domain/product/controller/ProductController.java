package com.hayeon.groupbuy.domain.product.controller;

import com.hayeon.groupbuy.domain.product.dto.request.ProductCreateRequest;
import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.service.ProductService;
import com.hayeon.groupbuy.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> save(
            @Valid @RequestBody ProductCreateRequest request
    ) {

        productService.save(request);

        return ResponseEntity.ok(
                CommonResponse.success(null)
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getProductList(
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