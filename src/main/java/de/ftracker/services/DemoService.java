package de.ftracker.services;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    public AuthenticationResponse startDemo() {
        deleteExpiredDemoUsers();
        createNewDemoUser();
        generateDemoData();
        return getAuthenticationResponse();
    }

    public void deleteExpiredDemoUsers() {

    }

    public void createNewDemoUser() {

    }

    public void generateDemoData() {

    }

    public AuthenticationResponse getAuthenticationResponse() {
        return null;
    }
}

