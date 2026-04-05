package com.hayeon.groupbuy.domain.groupPurchase.controller;

import com.hayeon.groupbuy.domain.groupPurchase.dto.request.CreateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.UpdateGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchaseResponse;
import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchasePageResponse;
import com.hayeon.groupbuy.domain.groupPurchase.service.GroupPurchaseService;
import com.hayeon.groupbuy.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groupPurchase")
@RequiredArgsConstructor
public class GroupPurchaseController {

    private final GroupPurchaseService groupPurchaseService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> save(@Valid @RequestBody CreateGroupPurchaseRequest request) {
        groupPurchaseService.save(request);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> edit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGroupPurchaseRequest request) {
        groupPurchaseService.edit(id, request);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<GroupPurchasePageResponse>> getGroupPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(
                CommonResponse.success(groupPurchaseService.getGroupPurchases(page, size, keyword)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<GroupPurchaseResponse>> getGroupPurchaseDetail(
            @PathVariable Long id
    ) {
        GroupPurchaseResponse response = groupPurchaseService.findGroupPurchaseById(id);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}