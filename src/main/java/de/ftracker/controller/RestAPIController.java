package de.ftracker.controller;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.services.CostManager;
import de.ftracker.services.MonthOverviewDTO;
import de.ftracker.services.MonthOverviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/costs")
@CrossOrigin(origins = "http://localhost:5173")
public class RestAPIController {
    private final CostManager costManager;
    private final MonthOverviewService monthOverviewService;

    @Autowired
    public RestAPIController(CostManager costManager) {
        this.costManager = costManager;
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

}
