package com.supplyboost.chero.user.controller;


import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/character")
    public ResponseEntity<GameCharacter> getUserCharacter(Principal principal, HttpServletRequest request, HttpSession session) {
        System.out.println("Session: " + session.getId());
        System.out.println("SecurityContext Authentication: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("Principal: " + principal);
        if (principal == null) {
            return ResponseEntity.status(401).body(null); // Unauthorized if no user is logged in
        }
        String username = principal.getName();
        User user = userService.findByUsername(username);

        if (user == null || user.getGameCharacter() == null) {
            return ResponseEntity.status(404).body(null); // User or character not found
        }
        return ResponseEntity.ok(user.getGameCharacter());
    }

}
