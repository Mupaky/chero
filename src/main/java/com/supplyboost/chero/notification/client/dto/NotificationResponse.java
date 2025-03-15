package com.supplyboost.chero.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String subject;

    private LocalDateTime createdOn;

    private String status;

    private String type;
}
