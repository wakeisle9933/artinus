package com.art.main.artinus.dto;

import com.art.main.artinus.enums.ActionType;
import com.art.main.artinus.enums.SubscriptionStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionHistoryDto {

  private Long channelId;
  private String channelName;
  private String phoneNumber;
  private ActionType actionType;
  private SubscriptionStatus status;
  private LocalDateTime actionDate;
}