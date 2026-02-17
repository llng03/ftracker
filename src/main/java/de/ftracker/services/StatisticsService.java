package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.utils.MonthNavigation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    CostManager costManager;

    @Autowired
    public StatisticsService(CostManager costManager) {
        this.costManager = costManager;
    }

    public Map<String, BigDecimal> getCostSumPerCategory(int year, int month) {
        Map<String, BigDecimal> costSumPerCategory = new HashMap<>();

        List<Cost> monthsExp = costManager.getMonthsExp(year, month);

        for (Cost cost : monthsExp) {
            String category = cost.getCategory().getCategoryName();
            if(costSumPerCategory.containsKey(category)) {
                costSumPerCategory.put(category, costSumPerCategory.get(category).add(cost.getAmount()));
            } else {
                costSumPerCategory.put(category, cost.getAmount());
            }
        }

        return costSumPerCategory;
    }

    public BigDecimal getExpenseSumWOFixedCost(int year, int month) {
        return costManager.getMonthsExp(year, month)
                .stream()
                .map(Cost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getExpenseSumWOFixedCost(YearMonth ym) {
        return getExpenseSumWOFixedCost(ym.getYear(), ym.getMonthValue());
    }

    public int differenceToLastMonth(int year, int month) {
        MonthNavigation monthNavigation = new MonthNavigation(year, month);
        BigDecimal thisMonthsSum = getExpenseSumWOFixedCost(year, month);
        BigDecimal lastMonthsSum = getExpenseSumWOFixedCost(monthNavigation.getPrevYearMonth());

        return thisMonthsSum.subtract(lastMonthsSum).intValue();

    }



}
