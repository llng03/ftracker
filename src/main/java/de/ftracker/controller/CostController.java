package de.ftracker.controller;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.services.*;
import de.ftracker.services.DTOs.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/costs")
@CrossOrigin(origins = "http://localhost:5173")
public class CostController {
    private final CostManager costManager;
    private final PotManager potManager;
    private final MonthOverviewService monthOverviewService;
    private final FixedCostOverviewDTOService fixedCostOverviewDTOService;
    private final UserRepository userRepository;



    @GetMapping
    public MonthOverviewDTO getMonthOverview(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam int year, @RequestParam int month
    ) {
        AppUser user = getCurrentUser(oidcUser);

        return monthOverviewService.getMonthOverviewDTO(year, month, user);

    }

    @GetMapping("/fixedCosts")
    public FixedCostOverviewDTO getFixedCosts(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam int year, @RequestParam int month
    ) {
        return fixedCostOverviewDTOService.getFixedCostOverviewDTO(getCurrentUser(oidcUser), year, month);
    }

    @PostMapping("/fixedCost")
    public ResponseEntity<Void> addFixedCost(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody FixedCostForm fixedCost) {
        costManager.addFixedCost(fixedCost, getCurrentUser(oidcUser));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/monthCost")
    public ResponseEntity<Void> addCost(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody CostDTO costDTO,
            @RequestParam int year,
            @RequestParam int month) {

        System.out.println("ISINCOME in controller: " + costDTO.isIncome());

        costManager.addCost(costDTO, getCurrentUser(oidcUser), year, month);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/toPots")
    public ResponseEntity<Void> addToPots(
                @AuthenticationPrincipal OidcUser oidcUser,
                @RequestBody DistributeRequest distributeRequest,
                @RequestParam int year,
                @RequestParam int month
    ) {

        costManager.addToPot(
                year,
                month,
                getCurrentUser(oidcUser),
                potManager,
                distributeRequest.getAmount(),
                distributeRequest.getPotId()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeFixedCost")
    public ResponseEntity<Void> changeFixedCost(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody UpdateFixedCostRequest updateFixedCostRequest,
            @RequestParam YearMonth changeMonth) {
        costManager.changeFixedCost(getCurrentUser(oidcUser), updateFixedCostRequest, changeMonth);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/newCategory")
    public ResponseEntity<Void> addNewCategory(@AuthenticationPrincipal OidcUser oidcUser, @RequestParam String categoryName) {
        costManager.addCategory(categoryName, getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateCost")
    public ResponseEntity<Void> updateCost(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody UpdateCostRequest updateCostRequest,
            @RequestParam int year, @RequestParam int month) {
        costManager.updateCost(updateCostRequest, getCurrentUser(oidcUser), year,  month, potManager);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateFixedCost")
    public ResponseEntity<Void> updateFixedCost(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody UpdateFixedCostRequest updateFixedCostRequest) {
        costManager.updateFixedCost(updateFixedCostRequest, getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteFixedCost")
    public ResponseEntity<Void> removeFixedCost(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam Long costId) {
        costManager.deleteFromFixedCosts(costId, getCurrentUser(oidcUser));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteCost")
    public ResponseEntity<Void> removeCost(@AuthenticationPrincipal OidcUser oidcUser,
                                           @RequestParam Long costId, @RequestParam int year,
                                           @RequestParam int month) {
        costManager.deleteFromCosts(costId, getCurrentUser(oidcUser), year, month, potManager);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePotEntry")
    public ResponseEntity<Void> deletePotEntry(@RequestBody DeleteEntryRequest deleteEntryRequest) {
        costManager.deletePotEntry(deleteEntryRequest, potManager);
        return ResponseEntity.ok().build();
    }

    private AppUser getCurrentUser(OidcUser oidcUser) {
        String providerUserId = oidcUser.getSubject();
        return userRepository.findByProviderAndProviderUserId("google", providerUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

}
