package com.hayeon.groupbuy.domain.product.service;


import com.hayeon.groupbuy.domain.product.dto.request.ProductCreateRequest;
import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.product.repository.ProductRepository;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void save(ProductCreateRequest request) {
        // 1. 로그인 유저 확인
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        // 2. 상품 생성
        Product product = Product.create(
                userId,
                request.getName(),
                request.getDetails(),
                request.getImageUrl()
        );

        // 3. DB 저장
        productRepository.save(product);
    }

    public List<ProductResponse> getProductList(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> products;

        // 검색어가 존재할 경우 > 검색 기능 실시
        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByNameContainingOrDetailsContaining(keyword, keyword, pageRequest);
        } else {
            products = productRepository.findAll(pageRequest);
        }

        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 상품입니다."));

        return ProductResponse.from(product);
    }
}