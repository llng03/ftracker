package de.ftracker.controller;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.services.CostManager;
import de.ftracker.services.DTOs.DistributeRequest;
import de.ftracker.services.DTOs.PotOverviewDTO;
import de.ftracker.services.DTOs.TakeMoneyFromPotRequest;
import de.ftracker.services.PotManager;
import de.ftracker.services.PotOverviewDTOService;
import de.ftracker.services.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pots")
@CrossOrigin(origins = "http://localhost:5173")
public class PotController {
    private final PotOverviewDTOService potOverviewDTOService;
    private final PotManager potManager;
    private final UserRepository userRepository;

    @Autowired
    public PotController(PotOverviewDTOService potOverviewDTOService, PotManager potManager, UserRepository userRepository) {
        this.potOverviewDTOService = potOverviewDTOService;
        this.potManager = potManager;
        this.userRepository = userRepository;
    }

    @GetMapping
    public PotOverviewDTO getPotOverview(@AuthenticationPrincipal OidcUser oidcUser) {
        return potOverviewDTOService.getPotOverviewDTO(getCurrentUser(oidcUser));
    }

    @GetMapping("/potList")
    public List<BudgetPot> getPots(@AuthenticationPrincipal OidcUser oidcUser) {

        return potManager.getPots(getCurrentUser(oidcUser));
    }

    @PostMapping("/new")
    public ResponseEntity<Void> addPot(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam String newPotName) {
        potManager.addPot(newPotName, getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/distribute")
    public ResponseEntity<Void> distribute(@AuthenticationPrincipal OidcUser oidcUser,
                                           @Valid @RequestBody DistributeRequest request) {
        potManager.distribute(request.getPotId(), request.getAmount(), getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/takeMoney")
    public ResponseEntity<Void> takeMoney(@AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody TakeMoneyFromPotRequest request) {
        potManager.pay(request.getPotId(), LocalDate.now(), request.getAmount(), getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePot")
    public ResponseEntity<Void> deletePot(@AuthenticationPrincipal OidcUser oidcUser,
                                          @RequestParam Long potId) {
        potManager.deletePotById(potId, getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    private AppUser getCurrentUser(OidcUser oidcUser) {
        String providerUserId = oidcUser.getSubject();
        return userRepository.findByProviderAndProviderUserId("google", providerUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
