package com.hayeon.groupbuy.domain.groupPurchase.service;

import com.hayeon.groupbuy.domain.groupPurchase.dto.request.CreateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.UpdateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchaseResponse;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchasePageResponse;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchaseEditResponse;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.product.repository.ProductRepository;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupPurchaseService {

    private final GroupPurchaseRepository groupPurchaseRepository;
    private final GroupPurchaseParticipationRepository groupPurchaseParticipationRepository;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final GroupPurchaseCountRedisRepository redisRepository;

    @Transactional
    public Long save(CreateGroupPurchaseRequest request) {
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("상품이 존재하지 않습니다."));

        if (!product.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("공동 구매 작성 권한이 없습니다.");
        }

        User user = userRepository.getReferenceById(userId);

        GroupPurchase groupPurchase = GroupPurchase.builder()
                .user(user)
                .product(product)
                .title(request.getTitle())
                .details(request.getDetails())
                .targetPrice(request.getTargetPrice())
                .targetParticipants(request.getTargetParticipants())
                .startDt(request.getStartDt())
                .endDt(request.getEndDt())
                .status(GroupPurchaseStatus.RECRUITING)
                .build();

        GroupPurchase saved = groupPurchaseRepository.save(groupPurchase);
        return saved.getId();
    }

    @Transactional
    public void edit(Long id, UpdateGroupPurchaseRequest request) {
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        GroupPurchase groupPurchase = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("공동구매가 존재하지 않습니다."));

        if (!groupPurchase.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        if (groupPurchase.getStatus() != GroupPurchaseStatus.RECRUITING) {
            throw new ConflictException("진행 중이거나 종료된 공동구매는 수정할 수 없습니다.");
        }

        groupPurchase.updateFromDto(request);
    }

    public GroupPurchasePageResponse getGroupPurchases(int page, int size, String keyword) {
        PageRequest pageRequest =
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDt"));
        Page<GroupPurchase> groupPurchases;

        // 검색어가 존재할 경우 > 검색 기능 실시
        if (keyword != null && !keyword.isBlank()) {
            groupPurchases = groupPurchaseRepository.findByTitleContainingOrDetailsContaining(keyword, keyword, pageRequest);
        } else {
            groupPurchases = groupPurchaseRepository.findAll(pageRequest);
        }

        List<GroupPurchaseResponse> content = groupPurchases.getContent()
                .stream()
                .map(gp -> {
                    int currentParticipants = getCurrentParticipants(gp.getId());
                    boolean isParticipated = isParticipated(gp.getId());

                    return GroupPurchaseResponse.from(
                            gp,
                            currentParticipants,
                            isParticipated
                    );
                })
                .toList();

        return new GroupPurchasePageResponse(
                content,
                groupPurchases.getNumber(),
                groupPurchases.getSize(),
                groupPurchases.getTotalPages(),
                groupPurchases.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public GroupPurchaseResponse findGroupPurchaseById(Long id) {

        GroupPurchase groupPurchase = groupPurchaseRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 공동구매입니다."));

        int currentParticipants = getCurrentParticipants(groupPurchase.getId());
        boolean isParticipated = isParticipated(groupPurchase.getId());

        return GroupPurchaseResponse.from(
                groupPurchase,
                currentParticipants,
                isParticipated
        );
    }

    @Transactional(readOnly = true)
    public GroupPurchaseEditResponse getEditData(Long id) {

        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        GroupPurchase gp = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 공동구매입니다."));

        if (!gp.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        return GroupPurchaseEditResponse.from(gp);
    }

    private int getCurrentParticipants(Long groupPurchaseId) {
        Long count = redisRepository.getCountOrDefault(groupPurchaseId);
        return count.intValue();
    }

    private boolean isParticipated(Long groupPurchaseId) {

        Long userId = SecurityUtil.getCurrentUserId().orElse(null);

        if (userId == null) return false;

        return groupPurchaseParticipationRepository
                .existsByGroupPurchaseIdAndUserIdAndStatus(
                        groupPurchaseId,
                        userId,
                        ParticipationStatus.ACTIVE
                );
    }
}