package com.hayeon.groupbuy.domain.product.service;

import com.hayeon.groupbuy.config.TestRedisConfig;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.product.dto.response.ProductPageResponse;
import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.product.repository.ProductRepository;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
class ProductIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupPurchaseRepository groupPurchaseRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        savedUser = User.create("integration@test.com", "encodedPassword", "통합테스트유저");
        userRepository.save(savedUser);

        setSecurityContext(savedUser.getId());
    }

    private void setSecurityContext(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getId()).willReturn(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("상품 등록 후 DB 실제 저장 확인")
    void 상품등록_DB저장_확인() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test content".getBytes()
        );

        // when
        productService.save("통합테스트상품", "상세설명", 15000, 10, file);

        // then
        var products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("통합테스트상품");
        assertThat(products.get(0).getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("상품 수정 후 DB 반영 확인")
    void 상품수정_DB반영_확인() {
        // given
        Product product = Product.create(savedUser, "원래상품", "원래설명", "/images/old.jpg", 10000, 5);
        productRepository.save(product);

        // when
        productService.edit(product.getId(), "수정된상품", "수정된설명", 20000, 3, null);

        // then
        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("수정된상품");
        assertThat(updated.getPrice()).isEqualTo(20000);
        assertThat(updated.getImageUrl()).isEqualTo("/images/old.jpg"); // 파일 없을 때 기존 유지
    }

    @Test
    @DisplayName("상품 단건 조회 - User 연관관계 매핑 확인")
    void 상품단건조회_연관관계_확인() {
        // given
        Product product = Product.create(savedUser, "조회용상품", "설명", "/images/a.jpg", 5000, 2);
        productRepository.save(product);

        // when
        ProductResponse response = productService.getProduct(product.getId());

        // then
        assertThat(response.name()).isEqualTo("조회용상품");
        assertThat(response.userId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("상품 목록 조회 - 페이징 동작 확인")
    void 상품목록조회_페이징_확인() {
        // given - 상품 3개 저장
        for (int i = 1; i <= 3; i++) {
            productRepository.save(
                    Product.create(savedUser, "상품" + i, "설명" + i, "/images/" + i + ".jpg", 1000 * i, i)
            );
        }

        // when - size 2로 첫 페이지 조회
        ProductPageResponse response = productService.getProductList(0, 2, null);

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.totalElements()).isEqualTo(3);
        assertThat(response.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 삭제 - soft delete 확인")
    void 상품삭제_softDelete_확인() {
        // given
        Product product = Product.create(savedUser, "삭제될상품", "설명", "/images/a.jpg", 1000, 1);
        productRepository.save(product);

        // when
        productService.delete(product.getId());

        // then
        Product deleted = productRepository.findById(product.getId()).orElseThrow();
        assertThat(deleted.getDeleteDt()).isNotNull(); // soft delete라 row는 남아있음
    }

    @Test
    @DisplayName("공동구매에 사용된 상품 삭제 시도 시 ConflictException")
    void 상품삭제_공동구매사용중_예외() {
        // given
        Product product = Product.create(savedUser, "공동구매용상품", "설명", "/images/a.jpg", 1000, 1);
        productRepository.save(product);

        GroupPurchase groupPurchase = GroupPurchase.create(
                savedUser, product, "테스트 공동구매", "공동구매 설명",
                900, 5,
                java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(7)
        );
        groupPurchaseRepository.save(groupPurchase);

        // when & then
        assertThatThrownBy(() -> productService.delete(product.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("공동구매에 사용된 상품은 삭제할 수 없습니다");
    }
}