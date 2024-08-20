package com.art.main.artinus.entity;

import com.art.main.artinus.enums.ActionType;
import com.art.main.artinus.enums.SubscriptionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionHistory {

  public SubscriptionHistory(Member member, Channel channel, ActionType actionType,
      SubscriptionStatus status) {
    this.member = member;
    this.channel = channel;
    this.actionType = actionType;
    this.status = status;
    this.actionDate = LocalDateTime.now();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @Enumerated(EnumType.STRING)
  private ActionType actionType;

  @Enumerated(EnumType.STRING)
  private SubscriptionStatus status;

  private LocalDateTime actionDate;

}
