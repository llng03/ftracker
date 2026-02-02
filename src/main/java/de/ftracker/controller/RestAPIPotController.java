package de.ftracker.controller;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.services.pots.*;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pots")
@CrossOrigin(origins = "http://localhost:5173")
public class RestAPIPotController {
    private final PotOverviewDTOService potOverviewDTOService;
    private final PotManager potManager;
    PotController potController;

    @Autowired
    public RestAPIPotController(PotController potController, PotOverviewDTOService potOverviewDTOService, PotManager potManager) {
        this.potController = potController;
        this.potOverviewDTOService = potOverviewDTOService;
        this.potManager = potManager;
    }

    @GetMapping
    public PotOverviewDTO getPotOverview() {
        return potOverviewDTOService.getPotOverviewDTO();
    }

    @PostMapping("/new")
    public ResponseEntity<Void> addPot(@Valid @RequestBody BudgetPot pot) {
        potManager.addPot(pot);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/distribute")
    public ResponseEntity<Void> distribute(@Valid @RequestBody DistributeRequest request) {
        potManager.distribute(request.getPotId(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/takeMoney")
    public ResponseEntity<Void> takeMoney(@Valid @RequestBody TakeMoneyFromPotRequest request) {
        potManager.pay(request.getPotId(), LocalDate.now(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePot")
    public ResponseEntity<Void> deletePot(@RequestParam Long potId) {
        potManager.deletePotById(potId);
        return ResponseEntity.ok().build();
    }
}
