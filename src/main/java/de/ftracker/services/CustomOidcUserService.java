package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import de.ftracker.domain.model.pots.UndistributedPotAmount;
import de.ftracker.services.repositories.CategoryRepository;
import de.ftracker.services.repositories.PotSummaryRepository;
import de.ftracker.services.repositories.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final CategoryRepository categoryRepository;
    private final PotSummaryRepository potSummaryRepository;

    private final OidcUserService delegate = new OidcUserService();
    private final UserRepository userRepository;

    public CustomOidcUserService(CategoryRepository categoryRepository, PotSummaryRepository potSummaryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.potSummaryRepository = potSummaryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
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

        ensureInitialData(user);

        return oidcUser;
    }

    private void ensureInitialData(AppUser user) {
        createCategoryIfMissing(user, "default");
        createCategoryIfMissing(user, "-> pots");
        createUsersPotSummaryIfMissing(user);
    }

    private void createUsersPotSummaryIfMissing(AppUser user) {
        if(potSummaryRepository.findByUserId(user.getId()).isEmpty()) {
            potSummaryRepository.save(new UndistributedPotAmount(user, BigDecimal.ZERO));
        }
    }

    private void createCategoryIfMissing(AppUser user, String name) {
        if (categoryRepository.findByUserAndCategoryName(user, name).isEmpty()) {
            categoryRepository.save(new Category(name, user));
        }
    }
}