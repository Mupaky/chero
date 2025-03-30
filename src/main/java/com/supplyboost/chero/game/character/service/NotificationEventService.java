package com.supplyboost.chero.game.character.service;

import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventService {

    private final UserService userService;

    @Autowired
    public NotificationEventService(UserService userService) {
        this.userService = userService;
    }

    public void notify(User user, String message) {
        user.getNotifications().add(message);
        userService.save(user);
    }
}
