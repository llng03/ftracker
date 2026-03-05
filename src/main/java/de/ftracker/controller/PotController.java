package de.ftracker.controller;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.pots.BudgetPot;
import de.ftracker.services.CurrentUserService;
import de.ftracker.services.dtos.DistributeRequest;
import de.ftracker.services.dtos.PotOverviewDTO;
import de.ftracker.services.dtos.TakeMoneyFromPotRequest;
import de.ftracker.services.PotManager;
import de.ftracker.services.dtos.dtoServices.PotOverviewDTOService;
import de.ftracker.services.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pots")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "http://localhost:5173")
public class PotController {
    private final PotOverviewDTOService potOverviewDTOService;
    private final PotManager potManager;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;


    @GetMapping
    public PotOverviewDTO getPotOverview(Authentication authentication) {
        AppUser user = currentUserService.requireUser(authentication);
        return potOverviewDTOService.getPotOverviewDTO(user);
    }

    @GetMapping("/potList")
    public List<BudgetPot> getPots(Authentication authentication) {
        AppUser user = currentUserService.requireUser(authentication);
        return potManager.getPots(user);
    }

    @PostMapping("/new")
    public ResponseEntity<Void> addPot(
            Authentication authentication,
            @RequestParam String newPotName) {
        AppUser user = currentUserService.requireUser(authentication);
        potManager.addPot(newPotName, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/distribute")
    public ResponseEntity<Void> distribute(Authentication authentication,
                                           @Valid @RequestBody DistributeRequest request) {
        AppUser user = currentUserService.requireUser(authentication);
        potManager.distribute(request.getPotId(), request.getAmount(), user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/takeMoney")
    public ResponseEntity<Void> takeMoney(Authentication authentication,
            @Valid @RequestBody TakeMoneyFromPotRequest request) {
        AppUser user = currentUserService.requireUser(authentication);
        potManager.pay(request.getPotId(), LocalDate.now(), request.getAmount(), user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePot")
    public ResponseEntity<Void> deletePot(Authentication authentication,
                                          @RequestParam Long potId) {
        AppUser user = currentUserService.requireUser(authentication);
        potManager.deletePotById(potId, user);
        return ResponseEntity.ok().build();
    }
}
