package com.hayeon.groupbuy.domain.product.service;

import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.product.dto.response.ProductPageResponse;
import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.product.repository.ProductRepository;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupPurchaseRepository groupPurchaseRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        ReflectionTestUtils.setField(productService, "uploadPath", "uploads/images");
    }

    private void setSecurityContext(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getId()).willReturn(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ==================== save() ====================

    @Test
    @DisplayName("정상 등록 성공 - save 호출 확인")
    void save_성공() {
        // given
        setSecurityContext(1L);

        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        User mockUser = mock(User.class);
        given(userRepository.getReferenceById(1L)).willReturn(mockUser);

        // when & then
        assertThatCode(() -> productService.save("상품명", "상세설명", 10000, 5, file))
                .doesNotThrowAnyException();

        then(productRepository).should().save(any(Product.class));
    }

    @Test
    @DisplayName("로그인 안 한 상태로 등록 시 UnauthorizedException")
    void save_로그인안함_예외() {
        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        assertThatThrownBy(() -> productService.save("상품명", "상세설명", 10000, 5, file))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인이 필요합니다");
    }

    @Test
    @DisplayName("파일이 비어있으면 RuntimeException")
    void save_빈파일_예외() {
        setSecurityContext(1L);

        MultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]
        );

        assertThatThrownBy(() -> productService.save("상품명", "상세설명", 10000, 5, emptyFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("업로드 파일이 없습니다");
    }

    // ==================== edit() ====================

    @Test
    @DisplayName("정상 수정 성공")
    void edit_성공() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(owner);
        given(existingProduct.getImageUrl()).willReturn("/images/old.jpg");

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));

        productService.edit(100L, "수정된 상품명", "수정된 설명", 20000, 10, null);

        then(existingProduct).should().update("수정된 상품명", "수정된 설명", "/images/old.jpg", 20000, 10);
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 시 ResourceNotFoundException")
    void edit_상품없음_예외() {
        setSecurityContext(1L);
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.edit(999L, "이름", "설명", 1000, 1, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("상품이 존재하지 않습니다");
    }

    @Test
    @DisplayName("본인 소유가 아닌 상품 수정 시 UnauthorizedException")
    void edit_권한없음_예외() {
        setSecurityContext(1L);

        User otherOwner = mock(User.class);
        given(otherOwner.getId()).willReturn(999L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(otherOwner);

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));

        assertThatThrownBy(() -> productService.edit(100L, "이름", "설명", 1000, 1, null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("수정 권한이 없습니다");
    }

    @Test
    @DisplayName("파일 없이 수정 시 기존 imageUrl 유지")
    void edit_파일없음_기존이미지유지() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(owner);
        given(existingProduct.getImageUrl()).willReturn("/images/existing.jpg");

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));

        productService.edit(100L, "이름", "설명", 1000, 1, null);

        then(existingProduct).should().update("이름", "설명", "/images/existing.jpg", 1000, 1);
    }

    @Test
    @DisplayName("새 파일로 수정 시 imageUrl 갱신")
    void edit_새파일로_이미지갱신() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(owner);

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));

        MultipartFile newFile = new MockMultipartFile(
                "file", "new.jpg", "image/jpeg", "new image content".getBytes()
        );

        productService.edit(100L, "이름", "설명", 1000, 1, newFile);

        // then - fileUpload()가 호출되어 새로운 imageUrl(/images/로 시작)이 update에 전달됐는지 확인
        then(existingProduct).should().update(
                eq("이름"), eq("설명"), startsWith("/images/"), eq(1000), eq(1)
        );
    }

    // ==================== getProductList() ====================

    @Test
    @DisplayName("keyword 없을 때 전체 목록 조회")
    void getProductList_전체조회() {
        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(1L);

        Product product = mock(Product.class);
        given(product.getId()).willReturn(1L);
        given(product.getUser()).willReturn(mockUser);
        given(product.getName()).willReturn("테스트상품");
        given(product.getDetails()).willReturn("설명");
        given(product.getImageUrl()).willReturn("/images/a.jpg");
        given(product.getPrice()).willReturn(10000);
        given(product.getStock()).willReturn(5);
        given(product.getCreateDt()).willReturn(null);

        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        given(productRepository.findAll(any(PageRequest.class))).willReturn(page);

        ProductPageResponse response = productService.getProductList(0, 10, null);

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
        then(productRepository).should(never()).findByNameContainingOrDetailsContaining(any(), any(), any());
    }

    @Test
    @DisplayName("keyword 있을 때 검색 조회")
    void getProductList_검색조회() {
        Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        given(productRepository.findByNameContainingOrDetailsContaining(eq("키워드"), eq("키워드"), any(PageRequest.class)))
                .willReturn(emptyPage);

        ProductPageResponse response = productService.getProductList(0, 10, "키워드");

        assertThat(response.content()).isEmpty();
        then(productRepository).should(never()).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("페이징 정보가 정확히 매핑되는지 확인")
    void getProductList_페이징정보_확인() {
        Page<Product> page = new PageImpl<>(List.of(), PageRequest.of(2, 5), 23);
        given(productRepository.findAll(any(PageRequest.class))).willReturn(page);

        ProductPageResponse response = productService.getProductList(2, 5, null);

        assertThat(response.page()).isEqualTo(2);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalElements()).isEqualTo(23);
    }

    // ==================== getProduct() ====================

    @Test
    @DisplayName("정상 조회 성공")
    void getProduct_성공() {
        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(1L);

        Product product = mock(Product.class);
        given(product.getId()).willReturn(1L);
        given(product.getUser()).willReturn(mockUser);
        given(product.getName()).willReturn("테스트상품");
        given(product.getDetails()).willReturn("설명");
        given(product.getImageUrl()).willReturn("/images/a.jpg");
        given(product.getPrice()).willReturn(10000);
        given(product.getStock()).willReturn(5);
        given(product.getCreateDt()).willReturn(null);

        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(1L);

        assertThat(response.name()).isEqualTo("테스트상품");
        assertThat(response.price()).isEqualTo(10000);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 ResourceNotFoundException")
    void getProduct_상품없음_예외() {
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("존재하지 않는 상품입니다");
    }

    // ==================== delete() ====================

    @Test
    @DisplayName("정상 삭제 성공 - delete() 호출 확인")
    void delete_성공() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(owner);

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));
        given(groupPurchaseRepository.existsByProductId(100L)).willReturn(false);

        productService.delete(100L);

        then(existingProduct).should().delete();
    }

    @Test
    @DisplayName("로그인 안 한 상태로 삭제 시 UnauthorizedException")
    void delete_로그인안함_예외() {
        assertThatThrownBy(() -> productService.delete(100L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인이 필요합니다");
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제 시 ResourceNotFoundException")
    void delete_상품없음_예외() {
        setSecurityContext(1L);
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("존재하지 않는 상품입니다");
    }

    @Test
    @DisplayName("본인 소유가 아닌 상품 삭제 시 UnauthorizedException")
    void delete_권한없음_예외() {
        setSecurityContext(1L);

        User otherOwner = mock(User.class);
        given(otherOwner.getId()).willReturn(999L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(otherOwner);

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));

        assertThatThrownBy(() -> productService.delete(100L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("삭제 권한이 없습니다");
    }

    @Test
    @DisplayName("공동구매에 사용된 상품 삭제 시 ConflictException")
    void delete_공동구매사용중_예외() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        Product existingProduct = mock(Product.class);
        given(existingProduct.getUser()).willReturn(owner);

        given(productRepository.findById(100L)).willReturn(Optional.of(existingProduct));
        given(groupPurchaseRepository.existsByProductId(100L)).willReturn(true);

        assertThatThrownBy(() -> productService.delete(100L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("공동구매에 사용된 상품은 삭제할 수 없습니다");
    }
}