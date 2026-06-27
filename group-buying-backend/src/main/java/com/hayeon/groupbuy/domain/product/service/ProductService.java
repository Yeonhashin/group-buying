package com.hayeon.groupbuy.domain.product.service;

import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.dto.response.ProductPageResponse;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.product.repository.ProductRepository;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.security.SecurityUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository; //  추가
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final Cloudinary cloudinary;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Transactional
    public void save(String name, String details, Integer price, Integer stock, MultipartFile file) {

        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        String imageUrl = fileUpload(file);

        User user = userRepository.getReferenceById(userId); // 정상
        Product product = Product.create(user, name, details, imageUrl, price, stock);

        productRepository.save(product);
    }

    @Transactional
    public void edit(Long id, String name, String details, Integer price, Integer stock, MultipartFile file) {

        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품이 존재하지 않습니다."));

        if (!product.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        String imageUrl = product.getImageUrl();

        if (file != null && !file.isEmpty()) {
            imageUrl = fileUpload(file);
        }

        product.update(name, details, imageUrl, price, stock);
    }

    public ProductPageResponse getProductList(int page, int size, String keyword, boolean onlyMine) {

        PageRequest pageRequest =
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDt"));

        Page<Product> products;
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (onlyMine) {
            Long userId = SecurityUtil.getCurrentUserId().orElse(null);
            if (userId == null) {
                return new ProductPageResponse(List.of(), page, size, 0, 0);
            }
            if (hasKeyword) {
                products = productRepository.findByUserIdAndKeyword(userId, keyword, pageRequest);
            } else {
                products = productRepository.findByUserId(userId, pageRequest);
            }
        } else {
            if (hasKeyword) {
                products = productRepository.findByNameContainingOrDetailsContaining(keyword, keyword, pageRequest);
            } else {
                products = productRepository.findAll(pageRequest);
            }
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

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 상품입니다."));

        if (!product.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        if (groupPurchaseRepository.existsByProductId(id)) {
            throw new ConflictException("공동구매에 사용된 상품은 삭제할 수 없습니다.");
        }

        product.delete();
    }

    private String fileUpload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "group-buying")
            );
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

//    private String fileUpload(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new RuntimeException("업로드 파일이 없습니다.");
//        }
//
//        try {
//            String originalFilename = file.getOriginalFilename();
//            String storedFileName = UUID.randomUUID() + "_" + originalFilename;
//
//            Path uploadDir = Paths.get(System.getProperty("user.dir"), uploadPath);
//
//            if (!Files.exists(uploadDir)) {
//                Files.createDirectories(uploadDir);
//            }
//
//            Path filePath = uploadDir.resolve(storedFileName);
//            file.transferTo(filePath.toFile());
//
//            return "/images/" + storedFileName;
//
//        } catch (IOException e) {
//            throw new RuntimeException("파일 업로드 실패", e);
//        }
//    }
}