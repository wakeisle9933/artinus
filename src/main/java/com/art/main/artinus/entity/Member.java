package com.art.main.artinus.entity;

import com.art.main.artinus.enums.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private SubscriptionStatus subscriptionStatus;

  @Column(name = "updated_date")
  @Version
  private LocalDateTime updatedDate;

  public void changeSubscriptionStatus(SubscriptionStatus newStatus) {
    if (this.subscriptionStatus == newStatus) {
      throw new IllegalStateException("FAIL : " + newStatus + " is already in use!");
    } else if (canChangeSubscriptionStatus(this.subscriptionStatus, newStatus)) {
      this.subscriptionStatus = newStatus;
    } else {
      throw new IllegalStateException(
          "FAIL : Unable to change subscription status to a lower tier!");
    }
  }

  private boolean canChangeSubscriptionStatus(SubscriptionStatus currentStatus,
      SubscriptionStatus newStatus) {
    return (currentStatus == SubscriptionStatus.NONE && (newStatus == SubscriptionStatus.BASIC
        || newStatus == SubscriptionStatus.PREMIUM)) ||
        (currentStatus == SubscriptionStatus.BASIC && newStatus == SubscriptionStatus.PREMIUM);
  }

  public void changeSubscriptionStatusForUnsubscribe(SubscriptionStatus newStatus) {
    if (this.subscriptionStatus == newStatus) {
      throw new IllegalStateException("FAIL : " + newStatus + " is already in use!");
    } else if (canChangeSubscriptionStatusForUnsubscribe(this.subscriptionStatus, newStatus)) {
      this.subscriptionStatus = newStatus;
    } else {
      throw new IllegalStateException(
          "FAIL : Unable to change subscription status to a higher tier!");
    }
  }

  private boolean canChangeSubscriptionStatusForUnsubscribe(SubscriptionStatus currentStatus,
      SubscriptionStatus newStatus) {
    return (currentStatus == SubscriptionStatus.PREMIUM && (newStatus == SubscriptionStatus.BASIC
        || newStatus == SubscriptionStatus.NONE)) ||
        (currentStatus == SubscriptionStatus.BASIC && newStatus == SubscriptionStatus.NONE);
  }
}
