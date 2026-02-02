package de.ftracker.controller;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.services.CostManager;
import de.ftracker.services.MonthOverviewDTO;
import de.ftracker.services.MonthOverviewService;
import de.ftracker.services.pots.DistributeRequest;
import de.ftracker.services.pots.PotManager;
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

    @DeleteMapping("/deleteFixedCost")
    public ResponseEntity<Void> removeFixedCost(@RequestParam Long costId) {
        costManager.deleteFromFixedCosts(costId);
        return ResponseEntity.ok().build();
    }

}
