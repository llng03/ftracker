package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerUserId = oidcUser.getSubject(); // OIDC: sub
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        // Debug: garantiert im Log sichtbar
        System.out.println("-- OIDC DATA --");
        System.out.println(provider);
        System.out.println(providerUserId);
        System.out.println(email);
        System.out.println(name);

        AppUser user = userRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> {
                    AppUser u = new AppUser();
                    u.setProvider(provider);
                    u.setProviderUserId(providerUserId);
                    return u;
                });

        user.setEmail(email);
        user.setName(name);
        userRepository.save(user);

        return oidcUser;
    }
}