package com.supplyboost.chero.game.controller;


import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/game")
public class GameController {

    private final UserService userService;

    @Autowired
    public GameController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/dashboard")
    public ModelAndView getHomePage(HttpSession session){

        Object userIdObj = session.getAttribute("user_id");

        if (userIdObj == null) {
            return new ModelAndView("redirect:/auth/login");  // Redirect if no session
        }
        User user = userService.getById(UUID.fromString(session.getAttribute("user_id").toString()));

        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/train-stats")
    public ModelAndView getTrainingHall(HttpSession session){
        Object userIdObj = session.getAttribute("user_id");

        if (userIdObj == null) {
            return new ModelAndView("redirect:/auth/login");  // Redirect if no session
        }

        User user = userService.getById(UUID.fromString(session.getAttribute("user_id").toString()));

        ModelAndView modelAndView = new ModelAndView("train-stats");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/underground")
    public ModelAndView getUnderground(HttpSession session){
        Object userIdObj = session.getAttribute("user_id");

        if (userIdObj == null) {
            return new ModelAndView("redirect:/auth/login");  // Redirect if no session
        }

        User user = userService.getById(UUID.fromString(session.getAttribute("user_id").toString()));

        ModelAndView modelAndView = new ModelAndView("underground");
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
