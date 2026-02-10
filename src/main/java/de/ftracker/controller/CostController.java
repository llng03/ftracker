package de.ftracker.controller;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.services.CostManager;
import de.ftracker.services.DTOs.DeleteEntryRequest;
import de.ftracker.services.DTOs.DistributeRequest;
import de.ftracker.services.DTOs.UpdateCostRequest;
import de.ftracker.services.DTOs.UpdateFixedCostRequest;
import de.ftracker.services.DTOs.MonthOverviewDTO;
import de.ftracker.services.MonthOverviewService;
import de.ftracker.services.PotManager;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/costs")
@CrossOrigin(origins = "http://localhost:5173")
public class CostController {
    private final CostManager costManager;
    private final PotManager potManager;
    private final MonthOverviewService monthOverviewService;

    @Autowired
    public CostController(CostManager costManager, PotManager potManager) {
        this.costManager = costManager;
        this.potManager = potManager;
        this.monthOverviewService = new MonthOverviewService(costManager);
    }

    @GetMapping
    public MonthOverviewDTO getMonthOverview(@RequestParam int year, @RequestParam int month) {
        return monthOverviewService.getMonthOverviewDTO(year, month);
    }

    @PostMapping("/fixedCost")
    public ResponseEntity<Void> addFixedIncome(@Valid @RequestBody FixedCostForm fixedCost) {
        if(fixedCost.getIsIncome()) {
            costManager.addToFixedIncome(fixedCost);
        } else {
            costManager.addToFixedExp(fixedCost);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/monthCost")
    public ResponseEntity<Void> addCost(@RequestBody Cost cost, @RequestParam int year, @RequestParam int month) {
        if(cost.getIsIncome()) {
            costManager.addMonthsIncome(year, month, cost);
        } else {
            costManager.addMonthsExp(year, month, cost);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toPots")
    public ResponseEntity<Void> addToPots(@RequestBody DistributeRequest distributeRequest,
                                          @RequestParam int year,
                                          @RequestParam int month
    ) {
        costManager.addToPot(
                year,
                month,
                potManager,
                distributeRequest.getAmount(),
                distributeRequest.getPotId()
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateCost")
    public ResponseEntity<Void> updateCost(@RequestBody UpdateCostRequest updateCostRequest,
                                           @RequestParam int year, @RequestParam int month) {
        costManager.updateCost(updateCostRequest, year, month);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateFixedCost")
    public ResponseEntity<Void> updateFixedCost(@RequestBody UpdateFixedCostRequest updateFixedCostRequest) {
        costManager.updateFixedCost(updateFixedCostRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteFixedCost")
    public ResponseEntity<Void> removeFixedCost(@RequestParam Long costId) {
        costManager.deleteFromFixedCosts(costId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteCost")
    public ResponseEntity<Void> removeCost(@RequestParam Long costId, @RequestParam int year, @RequestParam int month) {
        System.out.println("REMVOE COST");
        System.out.println(costId);
        costManager.deleteFromCosts(costId, year, month, potManager);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePotEntry")
    public ResponseEntity<Void> deletePotEntry(@RequestBody DeleteEntryRequest deleteEntryRequest) {
        costManager.deletePotEntry(deleteEntryRequest, potManager);
        return ResponseEntity.ok().build();
    }

}
