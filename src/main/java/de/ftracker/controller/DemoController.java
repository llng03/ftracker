package de.ftracker.controller;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import de.ftracker.services.DemoService;
import de.ftracker.services.dtos.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demo")
@CrossOrigin(origins = "${app.frontend.url}")
public class DemoController {
    private DemoService demoService;

    @Autowired
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping("/start")
    public AuthResponse startDemo() {
        return demoService.startDemo();
    }
}
