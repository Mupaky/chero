package com.supplyboost.chero.notification;

import com.supplyboost.chero.game.character.events.LevelUpEvent;
import com.supplyboost.chero.game.character.events.LevelUpNotificationManager;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.service.NotificationEventService;
import com.supplyboost.chero.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationEventServiceUTest {

    @Mock
    private NotificationEventService notificationEventService;

    @Spy
    @InjectMocks
    private LevelUpNotificationManager levelUpNotificationManager;

    @Test
    void givenLevelUpEvent_whenHandleLevelUp_thenSendNotification() {
        User user = User.builder()
                .username("testUser")
                .build();

        GameCharacter character = GameCharacter.builder()
                .nickName("testNickName")
                .level(10)
                .owner(user)
                .build();

        LevelUpEvent event = new LevelUpEvent(this, character);

        levelUpNotificationManager.handleLevelUp(event);

        verify(notificationEventService).notify(user, "Congratulations testNickName! You've reached level 10!");
    }


}
