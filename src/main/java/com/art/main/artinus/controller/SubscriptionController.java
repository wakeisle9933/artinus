package com.art.main.artinus.controller;

import com.art.main.artinus.dto.SubscriptionHistoryDto;
import com.art.main.artinus.enums.SubscriptionStatus;
import com.art.main.artinus.service.SubscriptionHistoryService;
import com.art.main.artinus.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscription", description = "구독 관련 API")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;
  private final SubscriptionHistoryService subscriptionHistoryService;

  @Operation(summary = "구독", description = "신규 구독 추가 및 구독 업그레이드 기능")
  @PostMapping("/subscribe")
  public ResponseEntity<String> subscribe(
      @Parameter(description = "구독할 사용자 전화번호") @RequestParam String phoneNumber,
      @Parameter(description = "구독할 채널 ID") @RequestParam Long channelId,
      @Parameter(description = "새로운 구독 상태") @RequestParam SubscriptionStatus newStatus) {
    try {
      subscriptionService.subscribe(phoneNumber, channelId, newStatus);
      return ResponseEntity.ok("SUCCEED : Subscriptions are complete");
    } catch (RuntimeException e) {
      if (e.getMessage().contains("Concurrent modification detected")) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "구독 해지", description = "기존 구독 해지 및, 구독 다운그레이드 기능")
  @PostMapping("/unsubscribe")
  public ResponseEntity<String> unsubscribe(
      @Parameter(description = "구독 해지할 사용자 전화번호") @RequestParam String phoneNumber,
      @Parameter(description = "구독 해지할 채널 ID") @RequestParam Long channelId,
      @Parameter(description = "새로운 구독 상태") @RequestParam SubscriptionStatus newStatus) {
    try {
      subscriptionService.unsubscribe(phoneNumber, channelId, newStatus);
      return ResponseEntity.ok("SUCCEED : Unsubscriptions are complete");
    } catch (RuntimeException e) {
      if (e.getMessage().contains("Concurrent modification detected")) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "번호 기반 구독 내역 조회", description = "핸드폰 번호로 구독 내역 조회")
  @GetMapping("/history")
  public ResponseEntity<?> getSubscriptionHistory(
      @Parameter(description = "사용자 전화번호") @RequestParam String phoneNumber) {
    try {
      List<SubscriptionHistoryDto> history = subscriptionHistoryService.getSubscriptionHistory(
          phoneNumber);
      if (CollectionUtils.isEmpty(history)) {
        return ResponseEntity.ok().body("SUCCEED : No Subscription history!");
      }
      return ResponseEntity.ok(history);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "날짜, 채널 기반 구독 내역 조회", description = "날짜와 채널을 입력해 구독 내역 조회")
  @GetMapping("/channel-history")
  public ResponseEntity<?> getChannelHistory(
      @Parameter(description = "조회할 날짜 양식(yyyymmdd)") @RequestParam String date,
      @Parameter(description = "채널 ID") @RequestParam Long channelId) {
    try {
      List<SubscriptionHistoryDto> history = subscriptionHistoryService.getChannelHistory(date,
          channelId);
      if (history.isEmpty()) {
        return ResponseEntity.ok()
            .body("SUCCEED : No subscription history for this channel on the given date!");
      }
      return ResponseEntity.ok(history);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}