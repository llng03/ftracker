package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.services.CostAggregationService;
import de.ftracker.services.DTOs.FixedCostOverviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.YearMonth;


@Service
public class FixedCostOverviewDTOService {
    private final CostManager costManager;

    @Autowired
    public FixedCostOverviewDTOService(CostManager costManager) {
        this.costManager = costManager;
    }

    public FixedCostOverviewDTO getFixedCostOverviewDTO(int currYear, int currMonth) {
        YearMonth currYM = YearMonth.of(currYear, currMonth);
        List<FixedCost> allFixedIncome = costManager.getFixedIncome();
        List<FixedCost> allFixedExp = costManager.getFixedExp();

        CostAggregationService costAggregationService = new CostAggregationService();
        FixedCostOverviewDTO dto = new FixedCostOverviewDTO();
        dto.setCurrentFixedIncome(costAggregationService.getApplicableFixedCosts(allFixedIncome, currYM));
        dto.setCurrentFixedExpense(costAggregationService.getApplicableFixedCosts(allFixedExp, currYM));

        dto.setFutureFixedIncome(costAggregationService.getFutureFixedCosts(allFixedIncome, currYM));
        dto.setFutureFixedExpense(costAggregationService.getFutureFixedCosts(allFixedExp, currYM));

        dto.setPastFixedIncome(costAggregationService.getPastFixedCosts(allFixedIncome, currYM));
        dto.setPastFixedExpense(costAggregationService.getPastFixedCosts(allFixedExp, currYM));

        return dto;
    }


}
