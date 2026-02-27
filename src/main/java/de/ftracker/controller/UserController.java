package de.ftracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @GetMapping("/me")
    public Map<String, Object> login(@AuthenticationPrincipal OAuth2User user) {
        if( user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return Map.of(
                "name", user.getAttribute("name"),
                "email", user.getAttribute("email")
        );
    }
}
