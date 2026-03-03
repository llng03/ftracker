package de.ftracker.controller;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public AppUser login(@AuthenticationPrincipal OAuth2User oAuth2User) {

        if( oAuth2User == null) {
            System.out.println("oauth2user is null");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String providerId = oAuth2User.getAttribute("sub");

        return userRepository.findByProviderAndProviderUserId("google", providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
