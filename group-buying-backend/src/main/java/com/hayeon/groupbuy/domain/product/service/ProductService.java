package com.hayeon.groupbuy.domain.product.service;

import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.dto.response.ProductPageResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    @Value("${file.upload.path}")
    private String uploadPath;

    @Transactional
    public void save(String name, String details, Integer price, Integer stock, MultipartFile file) {
        // 1. 로그인 확인
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        // 2. 파일 저장
        String imageUrl = fileUpload(file);

        // 3. 상품 생성
        Product product = Product.create(userId, name, details, imageUrl, price, stock);

        // 4. DB 저장
        productRepository.save(product);
    }

    @Transactional
    public void edit(Long id, String name, String details, Integer price, Integer stock, MultipartFile file) {
        // 1. 로그인 확인
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        // 2. 상품의 존재 여부 확인
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품이 존재하지 않습니다."));

        // 3. 권한 확인 (작성자만 수정 가능)
        if(!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        // 4. 파일 저장
        String imageUrl = product.getImageUrl();

        if (file != null && !file.isEmpty()) {
            imageUrl = fileUpload(file);
        }

        // 5. 엔티티 수정
        product.update(name, details, imageUrl, price, stock);
    }

    public ProductPageResponse getProductList(int page, int size, String keyword) {
        PageRequest pageRequest =
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDt"));
        Page<Product> products;

        // 검색어가 존재할 경우 > 검색 기능 실시
        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByNameContainingOrDetailsContaining(keyword, keyword, pageRequest);
        } else {
            products = productRepository.findAll(pageRequest);
        }

        List<ProductResponse> content = products.getContent()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return new ProductPageResponse(
                content,
                products.getNumber(),
                products.getSize(),
                products.getTotalPages(),
                products.getTotalElements()
        );
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 상품입니다."));

        return ProductResponse.from(product);
    }

    // 파일 업로드용
    private String fileUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("업로드 파일이 없습니다.");
        }

        try {

            String originalFilename = file.getOriginalFilename();
            String storedFileName = UUID.randomUUID() + "_" + originalFilename;

            Path uploadDir = Paths.get(System.getProperty("user.dir"), uploadPath);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(storedFileName);

            file.transferTo(filePath.toFile());

            return "/images/" + storedFileName;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}