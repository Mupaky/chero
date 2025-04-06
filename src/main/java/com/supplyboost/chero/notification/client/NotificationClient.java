package com.supplyboost.chero.notification.client;

import com.supplyboost.chero.notification.client.dto.NotificationRequest;
import com.supplyboost.chero.notification.client.dto.NotificationResponse;
import com.supplyboost.chero.notification.client.dto.UpsertNotificationPreference;
import com.supplyboost.chero.web.dto.NotificationPreferenceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "notification-svc",  url = "${notification.service.url}")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreference notificationPreference);

    @GetMapping("/preferences")
    ResponseEntity<NotificationPreferenceResponse> getNotificationPreference(@RequestParam("userId") UUID userId);

    @PostMapping
    ResponseEntity<NotificationResponse> sentGreetings(@RequestBody NotificationRequest notificationRequest);
}
