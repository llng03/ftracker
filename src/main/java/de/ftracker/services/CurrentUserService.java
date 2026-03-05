package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser requireUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            if (oauthToken.getPrincipal() instanceof OidcUser oidcUser) {
                String providerUserId = oidcUser.getSubject();
                return userRepository.findByProviderAndProviderUserId("google", providerUserId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}