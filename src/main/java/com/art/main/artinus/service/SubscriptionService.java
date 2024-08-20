package com.art.main.artinus.service;

import com.art.main.artinus.entity.Channel;
import com.art.main.artinus.entity.Member;
import com.art.main.artinus.enums.ActionType;
import com.art.main.artinus.enums.ChannelType;
import com.art.main.artinus.enums.SubscriptionStatus;
import com.art.main.artinus.repository.ChannelRepository;
import com.art.main.artinus.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

  private final MemberRepository memberRepository;
  private final ChannelRepository channelRepository;
  private final SubscriptionHistoryService subscriptionHistoryService;

  public record MemberChannelPair(Member member, Channel channel) {

  }

  @Transactional
  public void subscribe(String phoneNumber, Long channelId, SubscriptionStatus newStatus) {
    MemberChannelPair pair = findMemberAndChannel(phoneNumber, channelId);

    if (pair.channel().getType() == ChannelType.UNSUBSCRIBE_ONLY) {
      throw new RuntimeException("FAIL : This channel is Unsubscription-only!");
    }

    try {
      pair.member().changeSubscriptionStatus(newStatus);
      memberRepository.save(pair.member());
      subscriptionHistoryService.saveSubscriptionHistory(pair.member(), pair.channel(),
          ActionType.SUBSCRIBE, newStatus);
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new RuntimeException("FAIL : Concurrent modification detected. Please try again.");
    }
  }

  @Transactional
  public void unsubscribe(String phoneNumber, Long channelId, SubscriptionStatus newStatus) {
    MemberChannelPair pair = findMemberAndChannel(phoneNumber, channelId);

    if (pair.channel().getType() == ChannelType.SUBSCRIBE_ONLY) {
      throw new RuntimeException("FAIL : This channel is subscription-only!");
    }

    try {
      pair.member().changeSubscriptionStatusForUnsubscribe(newStatus);
      memberRepository.save(pair.member());
      subscriptionHistoryService.saveSubscriptionHistory(pair.member(), pair.channel(),
          ActionType.UNSUBSCRIBE, newStatus);
    } catch (ObjectOptimisticLockingFailureException e) {
      throw new RuntimeException("FAIL : Concurrent modification detected. Please try again.");
    }
  }

  private MemberChannelPair findMemberAndChannel(String phoneNumber, Long channelId) {
    Member member = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new RuntimeException("FAIL : Member not found!"));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new RuntimeException("FAIL : Channel not found!"));

    return new MemberChannelPair(member, channel);
  }
}
