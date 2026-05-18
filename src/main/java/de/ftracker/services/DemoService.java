package de.ftracker.services;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import de.ftracker.domain.model.pots.UndistributedPotAmount;
import de.ftracker.services.dtos.AuthResponse;
import de.ftracker.services.repositories.*;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class DemoService {
    private final CostManager costManager;
    private final UserRepository userRepository;
    private final PotSummaryRepository potSummaryRepository;
    private final CategoryRepository categoryRepository;
    private final JwtService jwtService;
    private final PotRepository potRepository;

    @Autowired
    public DemoService(UserRepository userRepository, JwtService jwtService, PotSummaryRepository potSummaryRepository, CategoryRepository categoryRepository, CostManager costManager, PotRepository potRepository) {
        this.userRepository = userRepository;
        this.potSummaryRepository = potSummaryRepository;
        this.categoryRepository = categoryRepository;
        this.jwtService = jwtService;
        this.costManager = costManager;
        this.potRepository = potRepository;
    }

    @Transactional
    public AuthResponse startDemo() {

        deleteExpiredDemoUsers();
        AppUser demo = createNewDemoUser();
        generateDemoData(demo);

        Instant exp = Instant.now().plus(24, ChronoUnit.HOURS);
        String token = jwtService.generateToken(demo.getId(), exp, true);

        return new AuthResponse(token);
    }

    public AppUser createNewDemoUser() {
        AppUser demoUser = new AppUser();
        demoUser.setName("Demo User");
        String uuid = UUID.randomUUID().toString();

        demoUser.setEmail("demo_" + uuid + "@demo.com");
        demoUser.setProvider("demo");
        demoUser.setProviderUserId(uuid);
        demoUser.setDemo(true);

        int hoursOfADay = 24;
        demoUser.setExpiresAt(LocalDateTime.now().plusHours(hoursOfADay));

        return userRepository.save(demoUser);

    }

    public void deleteExpiredDemoUsers() {
        List<AppUser> expiredUsers = userRepository.findExpiredDemoUsers();
        for(AppUser user: expiredUsers) {
            costManager.deleteCostByUser(user);
            potRepository.deleteByUserId(user.getId());
            potSummaryRepository.deleteByUserId(user.getId());
            categoryRepository.deleteByUser(user.getId());
            userRepository.delete(user);
        }
    }

    public void generateDemoData(AppUser user) {
        ensureInitialData(user);
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

    public AuthenticationResponse getAuthenticationResponse() {
        return null;
    }
}

