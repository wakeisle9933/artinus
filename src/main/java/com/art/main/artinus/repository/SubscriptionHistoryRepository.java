package com.art.main.artinus.repository;

import com.art.main.artinus.entity.Member;
import com.art.main.artinus.entity.SubscriptionHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

  List<SubscriptionHistory> findByMemberOrderByActionDateDesc(Member member);

  List<SubscriptionHistory> findByChannelIdAndActionDateBetween(Long channelId,
      LocalDateTime startOfDay, LocalDateTime endOfDay);
}
