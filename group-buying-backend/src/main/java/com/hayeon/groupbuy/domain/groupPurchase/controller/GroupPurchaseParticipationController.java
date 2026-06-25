package com.hayeon.groupbuy.domain.groupPurchase.controller;

import com.hayeon.groupbuy.domain.groupPurchase.dto.request.JoinGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.service.GroupPurchaseParticipationService;
import com.hayeon.groupbuy.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/group-purchases/{id}/participation")
@RequiredArgsConstructor
public class GroupPurchaseParticipationController {

    private final GroupPurchaseParticipationService groupPurchaseParticipationService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> join(
            @PathVariable Long id,
            @Valid @RequestBody JoinGroupPurchaseRequest request
    ) {
        groupPurchaseParticipationService.joinAndPublishEvent(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> cancel(
            @PathVariable Long id
    ) {
        groupPurchaseParticipationService.cancel(id);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

}