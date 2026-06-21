package com.hayeon.groupbuy.domain.groupPurchase.service;

import com.hayeon.groupbuy.domain.groupPurchase.dto.request.CreateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.UpdateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchaseEditResponse;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchasePageResponse;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchaseResponse;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GroupPurchaseServiceTest {

    @InjectMocks
    private GroupPurchaseService groupPurchaseService;

    @Mock private GroupPurchaseRepository groupPurchaseRepository;
    @Mock private GroupPurchaseParticipationRepository groupPurchaseParticipationRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private GroupPurchaseCountRedisRepository redisRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getId()).willReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, null)
        );
    }

    // ==================== save() ====================

    @Test
    @DisplayName("공동구매 등록 성공")
    void save_성공() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        Product product = mock(Product.class);
        given(product.getUser()).willReturn(owner);

        CreateGroupPurchaseRequest request = mock(CreateGroupPurchaseRequest.class);
        given(request.getProductId()).willReturn(10L);
        given(request.getTitle()).willReturn("테스트 공동구매");
        given(request.getDetails()).willReturn("설명");
        given(request.getTargetPrice()).willReturn(10000);
        given(request.getTargetParticipants()).willReturn(5);
        given(request.getStartDt()).willReturn(LocalDate.now());
        given(request.getEndDt()).willReturn(LocalDate.now().plusDays(7));

        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        given(userRepository.getReferenceById(1L)).willReturn(owner);

        GroupPurchase saved = mock(GroupPurchase.class);
        given(saved.getId()).willReturn(100L);
        given(groupPurchaseRepository.save(any())).willReturn(saved);

        Long result = groupPurchaseService.save(request);

        assertThat(result).isEqualTo(100L);
    }

    @Test
    @DisplayName("로그인 안 한 상태로 등록 시 UnauthorizedException")
    void save_로그인안함_예외() {
        CreateGroupPurchaseRequest request = mock(CreateGroupPurchaseRequest.class);

        assertThatThrownBy(() -> groupPurchaseService.save(request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 등록 시 ResourceNotFoundException")
    void save_상품없음_예외() {
        setSecurityContext(1L);

        CreateGroupPurchaseRequest request = mock(CreateGroupPurchaseRequest.class);
        given(request.getProductId()).willReturn(999L);
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupPurchaseService.save(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("상품 소유자가 아닌 경우 UnauthorizedException")
    void save_상품소유자아님_예외() {
        setSecurityContext(1L);

        User otherOwner = mock(User.class);
        given(otherOwner.getId()).willReturn(999L);

        Product product = mock(Product.class);
        given(product.getUser()).willReturn(otherOwner);

        CreateGroupPurchaseRequest request = mock(CreateGroupPurchaseRequest.class);
        given(request.getProductId()).willReturn(10L);
        given(productRepository.findById(10L)).willReturn(Optional.of(product));

        assertThatThrownBy(() -> groupPurchaseService.save(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("공동 구매 작성 권한이 없습니다");
    }

    // ==================== edit() ====================

    @Test
    @DisplayName("공동구매 수정 성공")
    void edit_성공() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getUser()).willReturn(owner);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.RECRUITING);

        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        UpdateGroupPurchaseRequest request = mock(UpdateGroupPurchaseRequest.class);
        groupPurchaseService.edit(100L, request);

        then(gp).should().updateFromDto(request);
    }

    @Test
    @DisplayName("로그인 안 한 상태로 수정 시 UnauthorizedException")
    void edit_로그인안함_예외() {
        assertThatThrownBy(() -> groupPurchaseService.edit(100L, mock(UpdateGroupPurchaseRequest.class)))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 공동구매 수정 시 ResourceNotFoundException")
    void edit_공동구매없음_예외() {
        setSecurityContext(1L);
        given(groupPurchaseRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupPurchaseService.edit(999L, mock(UpdateGroupPurchaseRequest.class)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("본인 소유가 아닌 공동구매 수정 시 UnauthorizedException")
    void edit_권한없음_예외() {
        setSecurityContext(1L);

        User otherOwner = mock(User.class);
        given(otherOwner.getId()).willReturn(999L);

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getUser()).willReturn(otherOwner);

        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        assertThatThrownBy(() -> groupPurchaseService.edit(100L, mock(UpdateGroupPurchaseRequest.class)))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("수정 권한이 없습니다");
    }

    @Test
    @DisplayName("RECRUITING 상태가 아닌 공동구매 수정 시 ConflictException")
    void edit_진행중상태_예외() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getUser()).willReturn(owner);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.COMPLETED);

        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        assertThatThrownBy(() -> groupPurchaseService.edit(100L, mock(UpdateGroupPurchaseRequest.class)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("진행 중이거나 종료된 공동구매는 수정할 수 없습니다");
    }

    // ==================== getGroupPurchases() ====================

    @Test
    @DisplayName("키워드 없을 때 전체 목록 조회")
    void getGroupPurchases_전체조회() {
        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getId()).willReturn(1L);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.RECRUITING);
        given(gp.getUser()).willReturn(mock(User.class));
        given(gp.getProduct()).willReturn(mock(Product.class));

        Page<GroupPurchase> page = new PageImpl<>(List.of(gp), PageRequest.of(0, 10), 1);
        given(groupPurchaseRepository.findAll(any(PageRequest.class))).willReturn(page);
        given(redisRepository.getCountOrDefault(1L)).willReturn(0L);
        given(gp.getEndDt()).willReturn(LocalDate.now().plusDays(7));
        given(gp.getStartDt()).willReturn(LocalDate.now());

        GroupPurchasePageResponse response = groupPurchaseService.getGroupPurchases(0, 10, null);

        assertThat(response.content()).hasSize(1);
    }

    @Test
    @DisplayName("키워드 있을 때 검색 조회")
    void getGroupPurchases_검색조회() {
        Page<GroupPurchase> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        given(groupPurchaseRepository.findByTitleContainingOrDetailsContaining(
                eq("키워드"), eq("키워드"), any(PageRequest.class))).willReturn(emptyPage);

        GroupPurchasePageResponse response = groupPurchaseService.getGroupPurchases(0, 10, "키워드");

        assertThat(response.content()).isEmpty();
        then(groupPurchaseRepository).should(never()).findAll(any(PageRequest.class));
    }

    // ==================== findGroupPurchaseById() ====================

    @Test
    @DisplayName("공동구매 단건 조회 성공")
    void findGroupPurchaseById_성공() {
        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getId()).willReturn(1L);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.RECRUITING);
        given(gp.getUser()).willReturn(mock(User.class));
        given(gp.getProduct()).willReturn(mock(Product.class));
        given(gp.getEndDt()).willReturn(LocalDate.now().plusDays(7));
        given(gp.getStartDt()).willReturn(LocalDate.now());

        given(groupPurchaseRepository.findDetailById(1L)).willReturn(Optional.of(gp));
        given(redisRepository.getCountOrDefault(1L)).willReturn(3L);

        GroupPurchaseResponse response = groupPurchaseService.findGroupPurchaseById(1L);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 공동구매 조회 시 ResourceNotFoundException")
    void findGroupPurchaseById_없음_예외() {
        given(groupPurchaseRepository.findDetailById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupPurchaseService.findGroupPurchaseById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ==================== getEditData() ====================

    @Test
    @DisplayName("수정 데이터 조회 성공")
    void getEditData_성공() {
        setSecurityContext(1L);

        User owner = mock(User.class);
        given(owner.getId()).willReturn(1L);

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getUser()).willReturn(owner);
        given(gp.getProduct()).willReturn(mock(Product.class));

        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        GroupPurchaseEditResponse response = groupPurchaseService.getEditData(100L);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("로그인 안 한 상태로 수정 데이터 조회 시 UnauthorizedException")
    void getEditData_로그인안함_예외() {
        assertThatThrownBy(() -> groupPurchaseService.getEditData(100L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 공동구매 수정 데이터 조회 시 ResourceNotFoundException")
    void getEditData_없음_예외() {
        setSecurityContext(1L);
        given(groupPurchaseRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupPurchaseService.getEditData(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("본인 소유가 아닌 공동구매 수정 데이터 조회 시 UnauthorizedException")
    void getEditData_권한없음_예외() {
        setSecurityContext(1L);

        User otherOwner = mock(User.class);
        given(otherOwner.getId()).willReturn(999L);

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getUser()).willReturn(otherOwner);

        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        assertThatThrownBy(() -> groupPurchaseService.getEditData(100L))
                .isInstanceOf(UnauthorizedException.class);
    }
}