package com.supplyboost.chero.web.controller;

import com.supplyboost.chero.notification.service.NotificationService;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.GameCharacterHeaderResponse;
import com.supplyboost.chero.web.dto.NotificationPreferenceResponse;
import com.supplyboost.chero.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ModelAndView getNotificationPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        User user = userService.getById(authenticationMetadata.getUserId());
        GameCharacterHeaderResponse gameCharacterHeaderResponse = DtoMapper.mapToGameCharacterHeaderResponse(user.getGameCharacter());
        NotificationPreferenceResponse notificationPreferenceResponse = notificationService.getNotificationPreference(user.getId(), false, user.getEmail());

        ModelAndView modelAndView = new ModelAndView("notifications");
        modelAndView.addObject("gameCharacter", gameCharacterHeaderResponse);
        modelAndView.addObject("notificationPreference", notificationPreferenceResponse);

        return modelAndView;

    }

    @GetMapping("/preferences/toggle")
    public ModelAndView toggleNotification(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        User user = userService.getById(authenticationMetadata.getUserId());


        NotificationPreferenceResponse response = notificationService.togglePreferences(user.getId(), user.getEmail());

        return new ModelAndView("redirect:/notifications");

    }
}
