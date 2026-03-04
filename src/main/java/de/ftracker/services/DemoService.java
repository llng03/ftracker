package de.ftracker.services;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import de.ftracker.domain.model.AppUser;
import de.ftracker.services.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemoService {
    public UserRepository userRepository;

    public AuthenticationResponse startDemo() {
        userRepository.deleteExpiredDemoUsers();
        createNewDemoUser();
        generateDemoData();
        return getAuthenticationResponse();
    }

    public void createNewDemoUser() {
        AppUser demoUser = new AppUser();
        demoUser.setName("Demo User");
        String uuid = UUID.randomUUID().toString();

        demoUser.setEmail("demo_" + uuid + "@demo.com");
        demoUser.setProvider("demo");
        demoUser.setProviderUserId(uuid);
        demoUser.setDemo(true);

        int hoursOfADay = 24;
        demoUser.setExpiresAt(LocalDateTime.now().plusHours(hoursOfADay));

        userRepository.save(demoUser);

    }

    public void generateDemoData() {

    }

    public AuthenticationResponse getAuthenticationResponse() {
        return null;
    }
}

