package com.art.main.artinus.service;

import com.art.main.artinus.dto.SubscriptionHistoryDto;
import com.art.main.artinus.entity.Channel;
import com.art.main.artinus.entity.Member;
import com.art.main.artinus.entity.SubscriptionHistory;
import com.art.main.artinus.enums.ActionType;
import com.art.main.artinus.enums.SubscriptionStatus;
import com.art.main.artinus.repository.MemberRepository;
import com.art.main.artinus.repository.SubscriptionHistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionHistoryService {

  private final MemberRepository memberRepository;
  private final SubscriptionHistoryRepository subscriptionHistoryRepository;

  public void saveSubscriptionHistory(Member member, Channel channel, ActionType actionType,
      SubscriptionStatus status) {
    SubscriptionHistory history = new SubscriptionHistory(member, channel, actionType, status);
    subscriptionHistoryRepository.save(history);
  }

  public List<SubscriptionHistoryDto> getSubscriptionHistory(String phoneNumber) {
    Member member = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new RuntimeException("FAIL : Member not found!"));

    List<SubscriptionHistory> histories = subscriptionHistoryRepository.findByMemberOrderByActionDateDesc(
        member);

    return histories.stream()
        .map(history -> SubscriptionHistoryDto.builder()
            .phoneNumber(history.getMember().getPhoneNumber())
            .channelId(history.getChannel().getId())
            .channelName(history.getChannel().getName())
            .actionType(history.getActionType())
            .status(history.getStatus())
            .actionDate(history.getActionDate())
            .build())
        .collect(Collectors.toList());
  }

  public List<SubscriptionHistoryDto> getChannelHistory(String date, Long channelId) {
    LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDateTime startOfDay = parsedDate.atStartOfDay();
    LocalDateTime endOfDay = parsedDate.atTime(LocalTime.MAX);

    List<SubscriptionHistory> histories = subscriptionHistoryRepository.findByChannelIdAndActionDateBetween(
        channelId, startOfDay, endOfDay);

    return histories.stream()
        .map(history -> SubscriptionHistoryDto.builder()
            .channelId(history.getChannel().getId())
            .channelName(history.getChannel().getName())
            .phoneNumber(history.getMember().getPhoneNumber())
            .actionType(history.getActionType())
            .status(history.getStatus())
            .actionDate(history.getActionDate())
            .build())
        .collect(Collectors.toList());
  }

}
