package com.supplyboost.chero.notification.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class UpsertNotificationPreference {

    private UUID userId;

    private boolean notificationEnabled;

    private String type;

    private String contactInfo;


}
