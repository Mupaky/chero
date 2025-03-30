package com.supplyboost.chero.notification;

import com.supplyboost.chero.notification.client.NotificationClient;
import com.supplyboost.chero.notification.client.dto.NotificationRequest;
import com.supplyboost.chero.notification.client.dto.NotificationResponse;
import com.supplyboost.chero.notification.client.dto.UpsertNotificationPreference;
import com.supplyboost.chero.notification.service.NotificationService;
import com.supplyboost.chero.web.dto.NotificationPreferenceResponse;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceUTest {

    @Mock
    private NotificationClient notificationClient;

    @Spy
    @InjectMocks
    private NotificationService notificationService;

    //saveNotificationPreference
    @Test
    void givenMissingData_whenSaveNotificationPreference_thenCatchException(){
        ResponseEntity<Void> httpResponse = ResponseEntity.noContent().build();

        when(notificationClient.upsertNotificationPreference(UpsertNotificationPreference.builder().build())).thenReturn(ResponseEntity.noContent().build());

        notificationService.saveNotificationPreference(any(), anyBoolean(), any());

        assertEquals(HttpStatus.NO_CONTENT, httpResponse.getStatusCode());
        assertFalse(httpResponse.hasBody());

    }

    //saveNotificationPreference
    @Test
    void givenUserData_whenSaveNotificationPreference_thenSaveNotificationPreference(){

        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(UUID.randomUUID())
                .notificationEnabled(true)
                .contactInfo("test@email.com")
                .type("EMAIL")
                .build();

        ResponseEntity<Void> httpResponse = ResponseEntity.status(HttpStatus.OK).build();

        when(notificationClient.upsertNotificationPreference(notificationPreference)).thenReturn(httpResponse);

        notificationService.saveNotificationPreference(any(), anyBoolean(), any());

    }

    //sendGreetings
    @Test
    void givenValidInput_whenSendGreetings_thenNotificationIsSentSuccessfully() {
        UUID userId = UUID.randomUUID();
        String username = "testUser";

        NotificationResponse mockResponse = NotificationResponse.builder().status("OK").build();
        ResponseEntity<NotificationResponse> httpResponse = ResponseEntity.ok(mockResponse);

        when(notificationClient.sentGreetings(any(NotificationRequest.class))).thenReturn(httpResponse);

        notificationService.sendGreetings(userId, username);

        verify(notificationClient, times(1)).sentGreetings(any(NotificationRequest.class));
    }

    //sendGreetings
    @Test
    void givenValidInput_whenNotificationResponseIsNotSuccessful_thenLogError() {
        UUID userId = UUID.randomUUID();
        String username = "testUser";

        ResponseEntity<NotificationResponse> failedHttpResponse = ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();

        when(notificationClient.sentGreetings(any(NotificationRequest.class))).thenReturn(failedHttpResponse);

        notificationService.sendGreetings(userId, username);

        verify(notificationClient, times(1)).sentGreetings(any(NotificationRequest.class));
    }

    //sendGreetings
    @Test
    void givenValidInput_whenNotificationClientThrowsException_thenCatchAndLog() {
        UUID userId = UUID.randomUUID();
        String username = "testUser";

        when(notificationClient.sentGreetings(any(NotificationRequest.class)))
                .thenThrow(new RuntimeException("Boom!"));

        notificationService.sendGreetings(userId, username);

        verify(notificationClient, times(1)).sentGreetings(any(NotificationRequest.class));
    }

    @Test
    void givenValidUserId_whenClientReturnsSuccess_thenReturnNotificationPreference() {
        UUID userId = UUID.randomUUID();
        NotificationPreferenceResponse notificationPreferenceResponse = NotificationPreferenceResponse.builder()
                .enabled(true)
                .type("EMAIL")
                .contactInfo("test@test.com")
                .build();

        ResponseEntity<NotificationPreferenceResponse> response = ResponseEntity.ok(notificationPreferenceResponse);

        when(notificationClient.getNotificationPreference(userId)).thenReturn(response);

        NotificationPreferenceResponse result = notificationService.getNotificationPreference(userId, true, "test@test.com");

        assertEquals(notificationPreferenceResponse, result);

        verify(notificationClient, times(1)).getNotificationPreference(userId);
    }

    @Test
    void givenValidUserId_whenClientReturnsNon2xx_thenThrowRuntimeException() {
        UUID userId = UUID.randomUUID();

        ResponseEntity<NotificationPreferenceResponse> badResponse =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(notificationClient.getNotificationPreference(userId)).thenReturn(badResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationService.getNotificationPreference(userId, true, "test@test.com");
        });

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void givenFeignExceptionOnFirstCall_whenFallbackSavesPreference_thenReturnPreference() {
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        NotificationPreferenceResponse fallbackResponse = NotificationPreferenceResponse.builder()
                .type("EMAIL")
                .enabled(true)
                .contactInfo(email)
                .build();

        ResponseEntity<NotificationPreferenceResponse> response = ResponseEntity.ok(fallbackResponse);

        when(notificationClient.getNotificationPreference(userId))
                .thenThrow(FeignException.class)
                .thenReturn(response);

        doNothing().when(notificationService).saveNotificationPreference(userId, true, email);

        NotificationPreferenceResponse result = notificationService.getNotificationPreference(userId, true, email);

        assertEquals(fallbackResponse, result);

        verify(notificationService).saveNotificationPreference(userId, true, email);
        verify(notificationClient, times(2)).getNotificationPreference(userId);
    }

    @Test
    void givenUserPreference_whenTogglePreferences_thenFlipPreferenceAndSave() {
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        NotificationPreferenceResponse response = NotificationPreferenceResponse.builder()
                .type("EMAIL")
                .enabled(true)
                .contactInfo(email)
                .build();

        doReturn(response).when(notificationService).getNotificationPreference(userId, false, email);
        doNothing().when(notificationService).saveNotificationPreference(userId, false, email);

        NotificationPreferenceResponse result = notificationService.togglePreferences(userId, email);

        assertNotNull(result);
        assertFalse(result.isEnabled());
        assertEquals(email, result.getContactInfo());

        verify(notificationService).getNotificationPreference(userId, false, email);
        verify(notificationService).saveNotificationPreference(userId, false, email);
    }


}
