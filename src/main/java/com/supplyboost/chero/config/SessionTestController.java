package com.supplyboost.chero.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SessionTestController {
    @GetMapping("/api/user/test-session")
    public ResponseEntity<?> testSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.ok(Map.of("sessionId", "null"));
        }
        return ResponseEntity.ok(Map.of("sessionId", session.getId()));
    }

    @GetMapping("/api/user/test-session2")
    public ResponseEntity<?> testSession2(HttpServletRequest request) {
        HttpSession session = request.getSession(true); // Creates a new session if one doesn't exist
        return ResponseEntity.ok(Map.of("sessionId", session.getId()));
    }
}