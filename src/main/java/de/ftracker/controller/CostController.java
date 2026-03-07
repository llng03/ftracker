package de.ftracker.controller;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.FixedCostForm;
import de.ftracker.services.*;
import de.ftracker.services.dtos.*;
import de.ftracker.services.dtos.dtoServices.FixedCostOverviewDTOService;
import de.ftracker.services.dtos.dtoServices.MonthOverviewService;
import de.ftracker.services.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/costs")
@CrossOrigin(origins = "${app.frontend.url}")
public class CostController {
    private final CostManager costManager;
    private final PotManager potManager;
    private final MonthOverviewService monthOverviewService;
    private final FixedCostOverviewDTOService fixedCostOverviewDTOService;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;


    @GetMapping
    public MonthOverviewDTO getMonthOverview(
            Authentication authentication,
            @RequestParam int year, @RequestParam int month
    ) {
        AppUser user = currentUserService.requireUser(authentication);
        return monthOverviewService.getMonthOverviewDTO(year, month, user);

    }

    @GetMapping("/fixedCosts")
    public FixedCostOverviewDTO getFixedCosts(
            Authentication authentication,
            @RequestParam int year, @RequestParam int month
    ) {
        AppUser user = currentUserService.requireUser(authentication);
        return fixedCostOverviewDTOService.getFixedCostOverviewDTO(user, year, month);
    }

    @PostMapping("/fixedCost")
    public ResponseEntity<Void> addFixedCost(
            Authentication authentication,
            @Valid @RequestBody FixedCostForm fixedCost) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.addFixedCost(fixedCost, user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/monthCost")
    public ResponseEntity<Void> addCost(
            Authentication authentication,
            @RequestBody CostDTO costDTO,
            @RequestParam int year,
            @RequestParam int month) {

        AppUser user = currentUserService.requireUser(authentication);
        costManager.addCost(costDTO, user, year, month);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/toPots")
    public ResponseEntity<Void> addToPots(
                Authentication authentication,
                @RequestBody DistributeRequest distributeRequest,
                @RequestParam int year,
                @RequestParam int month
    ) {
        AppUser user = currentUserService.requireUser(authentication);

        costManager.addToPot(
                year,
                month,
                user,
                potManager,
                distributeRequest.getAmount(),
                distributeRequest.getPotId()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeFixedCost")
    public ResponseEntity<Void> changeFixedCost(
            Authentication authentication,
            @RequestBody UpdateFixedCostRequest updateFixedCostRequest,
            @RequestParam YearMonth changeMonth) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.changeFixedCost(user, updateFixedCostRequest, changeMonth);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/newCategory")
    public ResponseEntity<Void> addNewCategory(Authentication authentication, @RequestParam String categoryName) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.addCategory(categoryName, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateCost")
    public ResponseEntity<Void> updateCost(
            Authentication authentication,
            @RequestBody UpdateCostRequest updateCostRequest,
            @RequestParam int year, @RequestParam int month) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.updateCost(updateCostRequest, user, year,  month, potManager);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateFixedCost")
    public ResponseEntity<Void> updateFixedCost(
            Authentication authentication,
            @RequestBody UpdateFixedCostRequest updateFixedCostRequest) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.updateFixedCost(updateFixedCostRequest, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteFixedCost")
    public ResponseEntity<Void> removeFixedCost(
            Authentication authentication,
            @RequestParam Long costId) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.deleteFromFixedCosts(costId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteCost")
    public ResponseEntity<Void> removeCost(Authentication authentication,
                                           @RequestParam Long costId, @RequestParam int year,
                                           @RequestParam int month) {
        AppUser user = currentUserService.requireUser(authentication);
        costManager.deleteFromCosts(costId, user, year, month, potManager);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePotEntry")
    public ResponseEntity<Void> deletePotEntry(@RequestBody DeleteEntryRequest deleteEntryRequest) {
        costManager.deletePotEntry(deleteEntryRequest, potManager);
        return ResponseEntity.ok().build();
    }

}
