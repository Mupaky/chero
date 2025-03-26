package com.supplyboost.chero.web.controller;

import com.supplyboost.chero.web.dto.LoginRequest;
import com.supplyboost.chero.web.dto.RegisterRequest;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(){
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registerRequest", RegisterRequest.builder().build());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@ModelAttribute RegisterRequest registerRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ModelAndView("auth/register");
        }
        userService.register(registerRequest);

        System.out.println("We Continue as normal");
        return new ModelAndView("redirect:/auth/login");
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(){
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {
        if(bindingResult.hasErrors()){
            return "login";
        }

        User user = userService.login(loginRequest);
        session.setAttribute("user_id", user.getId());

        System.out.println("Session ID: " + session.getId());
        System.out.println("Stored user_id in session: " + session.getAttribute("user_id"));

        return "redirect:/game/dashboard";
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session){
        session.invalidate();
        ModelAndView modelAndView = new ModelAndView("redirect:/login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }


}