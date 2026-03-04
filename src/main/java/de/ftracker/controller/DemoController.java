package de.ftracker.controller;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import de.ftracker.services.DemoService;
import de.ftracker.services.dtos.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demo")
@CrossOrigin(origins = "http://localhost:5173")
public class DemoController {
    private DemoService demoService;

    @PostMapping("/start")
    public AuthResponse startDemo() {
        return demoService.startDemo();
    }
}
