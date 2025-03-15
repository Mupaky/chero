package com.supplyboost.chero.game.character.events;

import com.supplyboost.chero.game.character.model.GameCharacter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LevelUpNotificationManager {
    private final NotificationEventService notificationEventService;

    @Autowired
    public LevelUpNotificationManager(NotificationEventService notificationEventService) {
        this.notificationEventService = notificationEventService;
    }

    @EventListener
    public void handleLevelUp(LevelUpEvent event) {
        GameCharacter character = event.getCharacter();
        String message = "Congratulations " + character.getNickName() +
                "! You've reached level " + character.getLevel() + "!";

        notificationEventService.notify(character.getOwner(), message);
    }
}
