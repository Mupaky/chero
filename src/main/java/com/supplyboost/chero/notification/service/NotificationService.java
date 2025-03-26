package com.supplyboost.chero.notification.service;

import com.supplyboost.chero.notification.client.NotificationClient;
import com.supplyboost.chero.notification.client.dto.NotificationRequest;
import com.supplyboost.chero.notification.client.dto.NotificationResponse;
import com.supplyboost.chero.notification.client.dto.UpsertNotificationPreference;
import com.supplyboost.chero.web.dto.NotificationPreferenceResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class NotificationService {
    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean isEmailEnabled, String email){

        try {
            UpsertNotificationPreference upsertNotificationPreference = UpsertNotificationPreference.builder()
                    .userId(userId)
                    .notificationEnabled(isEmailEnabled)
                    .contactInfo(email)
                    .type("EMAIL")
                    .build();

            ResponseEntity<Void> httpResponse= notificationClient.upsertNotificationPreference(upsertNotificationPreference);

            if(!httpResponse.getStatusCode().is2xxSuccessful()){
                log.error("[Feign call to notification_svc failed] Can't save user notification preference for user with id [%s].".formatted(userId));
            }
        }catch (Exception e){
            log.error("[Feign call to notification_svc failed] Can't save user notification preference for user with id [%s]. Error code [%s]".formatted(userId, e.getMessage()));
        }



    }

    public void sendGreetings(UUID userId, String username){
        try {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .userId(userId)
                    .subject("Welcome to the Underground, %s.".formatted(username))
                    .body("""
                    Dear %s,
                
                    Welcome to the Underground! We're thrilled to have you join our community. As a new member, you're about to embark on an exciting journey filled with challenges, rewards, and camaraderie.
                
                    To get started, here are a few tips:
                
                    - Explore the Underground: Dive into various quests and uncover hidden treasures.
                    - Connect with Fellow Adventurers: Join our forums and social media channels to share experiences and strategies.
                    - Customize Your Experience: Visit your profile to personalize your settings and preferences.
                
                    We're committed to providing you with an immersive and engaging experience. If you have any questions or need assistance, our support team is here to help.
                
                    Once again, welcome aboard! We look forward to seeing you thrive in the Underground.
                
                    Best regards,
                
                    The Underground Team
                    """.formatted(username))
                    .build();
            ResponseEntity<NotificationResponse> httpResponse= notificationClient.sentGreetings(notificationRequest);

            if(!httpResponse.getStatusCode().is2xxSuccessful()){
                log.error("[Feign call to notification_svc failed] Can't sent email for user with id [%s].".formatted(userId));
            }
        }catch (Exception e){
            log.error("[Feign call to notification_svc failed] Can't sent email for user with id [%s]. Error code [%s]".formatted(userId, e.getMessage()));

        }

    }

    public NotificationPreferenceResponse getNotificationPreference(UUID userId, boolean isEmailEnabled, String email) {

        try {
            ResponseEntity<NotificationPreferenceResponse> httpResponse = notificationClient.getNotificationPreference(userId);
            if(!httpResponse.getStatusCode().is2xxSuccessful()){
                throw new RuntimeException("Notification preference for user [%s] does not exist.".formatted(userId));
            }

            return httpResponse.getBody();
        } catch (FeignException e) {
            saveNotificationPreference(userId, isEmailEnabled, email);
            ResponseEntity<NotificationPreferenceResponse> httpResponse = notificationClient.getNotificationPreference(userId);
            return httpResponse.getBody();
        }

    }

    public NotificationPreferenceResponse togglePreferences(UUID userId, String email){
        NotificationPreferenceResponse response = getNotificationPreference(userId, false, email);

        response.setEnabled(!response.isEnabled());

        saveNotificationPreference(userId, response.isEnabled(), email);

        return response;
    }


}
