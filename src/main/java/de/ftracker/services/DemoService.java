package de.ftracker.services;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import de.ftracker.domain.model.AppUser;
import de.ftracker.services.dtos.AuthResponse;
import de.ftracker.services.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemoService {
    public UserRepository userRepository;
    public JwtService jwtService;

    public AuthResponse startDemo() {
        userRepository.deleteExpiredDemoUsers();
        AppUser demo = createNewDemoUser();
        generateDemoData();

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

    public void generateDemoData() {

    }

    public AuthenticationResponse getAuthenticationResponse() {
        return null;
    }
}

