package de.ftracker.services;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import de.ftracker.services.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    }

    public void generateDemoData() {

    }

    public AuthenticationResponse getAuthenticationResponse() {
        return null;
    }
}

