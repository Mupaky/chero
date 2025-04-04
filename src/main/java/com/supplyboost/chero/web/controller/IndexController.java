package com.supplyboost.chero.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String getHomePage(){
        return "home";
    }
    @GetMapping("about")
    public String getAboutPage(){
        return "about";
    }
    @GetMapping("contact")
    public String getContactPage(){
        return "contact";
    }
}
