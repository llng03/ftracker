package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.services.CostAggregationService;
import de.ftracker.services.DTOs.FixedCostOverviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.YearMonth;


@Service
@RequiredArgsConstructor
public class FixedCostOverviewDTOService {
    private final CostManager costManager;
    private final CostAggregationService costAggregationService;

    public FixedCostOverviewDTO getFixedCostOverviewDTO(AppUser user, int currYear, int currMonth) {
        YearMonth currYM = YearMonth.of(currYear, currMonth);
        List<FixedCost> allFixedIncome = costManager.getFixedIncome(user.getId());
        List<FixedCost> allFixedExp = costManager.getFixedExp(user.getId());

        FixedCostOverviewDTO dto = new FixedCostOverviewDTO();
        dto.setCurrentFixedIncome(costAggregationService.getPresentFixedCosts(allFixedIncome, currYM));
        dto.setCurrentFixedExpense(costAggregationService.getPresentFixedCosts(allFixedExp, currYM));

        dto.setFutureFixedIncome(costAggregationService.getFutureFixedCosts(allFixedIncome, currYM));
        dto.setFutureFixedExpense(costAggregationService.getFutureFixedCosts(allFixedExp, currYM));

        dto.setPastFixedIncome(costAggregationService.getPastFixedCosts(allFixedIncome, currYM));
        dto.setPastFixedExpense(costAggregationService.getPastFixedCosts(allFixedExp, currYM));

        return dto;
    }


}
