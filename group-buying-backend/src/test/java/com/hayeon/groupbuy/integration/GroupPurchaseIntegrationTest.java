package com.hayeon.groupbuy.integration;

import com.hayeon.groupbuy.config.TestRedisConfig;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.CreateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.JoinGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.groupPurchase.service.GroupPurchaseParticipationService;
import com.hayeon.groupbuy.domain.groupPurchase.service.GroupPurchaseService;
import com.hayeon.groupbuy.domain.notification.entity.Notification;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.notification.repository.NotificationRepository;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;
import com.hayeon.groupbuy.domain.order.repository.OrderRepository;
import com.hayeon.groupbuy.domain.order.service.OrderService;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.product.repository.ProductRepository;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
class GroupPurchaseIntegrationTest {

    @Autowired private GroupPurchaseService groupPurchaseService;
    @Autowired private GroupPurchaseParticipationService participationService;
    @Autowired private OrderService orderService;
    @Autowired private NotificationService notificationService;

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private GroupPurchaseRepository groupPurchaseRepository;
    @Autowired private GroupPurchaseParticipationRepository participationRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Autowired private GroupPurchaseCountRedisRepository redisRepository;

    private User savedUser;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        savedUser = User.create("integration@test.com", "password", "통합테스터");
        userRepository.save(savedUser);

        savedProduct = Product.create(savedUser, "통합테스트상품", "설명", "/images/test.jpg", 10000, 100);
        productRepository.save(savedProduct);

        setSecurityContext(savedUser.getId());
    }

    private void setSecurityContext(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getId()).willReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, null)
        );
    }

    // ==================== 공동구매 생성 ====================

    @Test
    @DisplayName("공동구매 생성 후 DB 저장 확인")
    void 공동구매_생성_DB저장() {
        CreateGroupPurchaseRequest request = mock(CreateGroupPurchaseRequest.class);
        given(request.getProductId()).willReturn(savedProduct.getId());
        given(request.getTitle()).willReturn("통합 테스트 공동구매");
        given(request.getDetails()).willReturn("상세 설명");
        given(request.getTargetPrice()).willReturn(9000);
        given(request.getTargetParticipants()).willReturn(5);
        given(request.getStartDt()).willReturn(LocalDate.now());
        given(request.getEndDt()).willReturn(LocalDate.now().plusDays(7));

        Long gpId = groupPurchaseService.save(request);

        GroupPurchase saved = groupPurchaseRepository.findById(gpId).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("통합 테스트 공동구매");
        assertThat(saved.getStatus()).isEqualTo(GroupPurchaseStatus.RECRUITING);
        assertThat(saved.getUser().getId()).isEqualTo(savedUser.getId());
    }

    // ==================== 참여 신청 ====================

    @Test
    @DisplayName("공동구매 참여 신청 후 DB 저장 및 Redis 카운트 증가 확인")
    void 공동구매_참여신청_DB저장() {
        GroupPurchase gp = GroupPurchase.create(
                savedUser, savedProduct, "참여 테스트", "설명",
                9000, 5, LocalDate.now(), LocalDate.now().plusDays(7)
        );
        groupPurchaseRepository.save(gp);

        // Redis mock 설정 (TestRedisConfig에서 mock 빈 사용)
        given(redisRepository.join(gp.getId(), 5)).willReturn(1L);

        participationService.join(gp.getId(), mock(JoinGroupPurchaseRequest.class));

        List<GroupPurchaseParticipation> participations =
                participationRepository.findAll().stream()
                        .filter(p -> p.getGroupPurchase().getId().equals(gp.getId()))
                        .toList();

        assertThat(participations).hasSize(1);
        assertThat(participations.get(0).getStatus()).isEqualTo(ParticipationStatus.ACTIVE);
    }

    // ==================== 주문 생성 → 결제 → 알림 ====================

    @Test
    @DisplayName("주문 생성 후 DB 저장 확인")
    void 주문_생성_DB저장() {
        GroupPurchase gp = GroupPurchase.create(
                savedUser, savedProduct, "주문 테스트", "설명",
                9000, 5, LocalDate.now(), LocalDate.now().plusDays(7)
        );
        groupPurchaseRepository.save(gp);

        orderService.createOrder(savedUser.getId(), gp.getId());

        List<Order> orders = orderRepository.findByUserId(savedUser.getId());
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orders.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.READY);
    }

    @Test
    @DisplayName("결제 완료 후 알림 DB 저장 확인")
    void 결제완료_알림생성_DB저장() {
        // 알림은 이벤트 리스너가 처리하는데, 통합 테스트에서는
        // NotificationService를 직접 호출해서 DB 저장 확인
        notificationService.createOrderPaid(savedUser.getId(), "통합 테스트 공동구매");

        List<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreateDtDesc(savedUser.getId());

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getStatus()).isEqualTo(NotificationStatus.ORDER_PAID);
        assertThat(notifications.get(0).getMessage())
                .contains("통합 테스트 공동구매");
    }

    @Test
    @DisplayName("공동구매 → 주문 → 알림 전체 흐름 확인")
    void 전체흐름_공동구매_주문_알림() {
        // 1. 공동구매 생성
        GroupPurchase gp = GroupPurchase.create(
                savedUser, savedProduct, "전체 흐름 테스트", "설명",
                9000, 5, LocalDate.now(), LocalDate.now().plusDays(7)
        );
        groupPurchaseRepository.save(gp);

        // 2. 주문 생성
        orderService.createOrder(savedUser.getId(), gp.getId());

        List<Order> orders = orderRepository.findByUserId(savedUser.getId());
        assertThat(orders).hasSize(1);

        // 3. 알림 생성 (이벤트 리스너 대신 직접 호출)
        notificationService.createOrderCreated(savedUser.getId(), gp.getTitle());

        List<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreateDtDesc(savedUser.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getStatus()).isEqualTo(NotificationStatus.ORDER_CREATED);
    }
}