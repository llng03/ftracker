package de.ftracker.services;

import de.ftracker.domain.model.cost.Cost;
import de.ftracker.utils.MonthNavigation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    CostManager costManager;

    @Autowired
    public StatisticsService(CostManager costManager) {
        this.costManager = costManager;
    }

    public Map<String, BigDecimal> getCostSumPerCategory(int year, int month, Long userId) {
        Map<String, BigDecimal> costSumPerCategory = new LinkedHashMap<>();

        List<Cost> monthsExp = costManager.getMonthsExp(year, month, userId);

        for (Cost cost : monthsExp) {
            String category = cost.getCategory().getCategoryName();
            if(costSumPerCategory.containsKey(category)) {
                costSumPerCategory.put(category, costSumPerCategory.get(category).add(cost.getAmount()));
            } else {
                costSumPerCategory.put(category, cost.getAmount());
            }
        }

        return costSumPerCategory
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public BigDecimal getExpenseSumWOFixedCost(int year, int month, Long userId) {
        return costManager.getMonthsExp(year, month, userId)
                .stream()
                .map(Cost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getExpenseSumWOFixedCost(YearMonth ym, Long userId) {
        return getExpenseSumWOFixedCost(ym.getYear(), ym.getMonthValue(), userId);
    }

    public int differenceToLastMonth(int year, int month, Long userId) {
        MonthNavigation monthNavigation = new MonthNavigation(year, month);
        BigDecimal thisMonthsSum = getExpenseSumWOFixedCost(year, month, userId);
        BigDecimal lastMonthsSum = getExpenseSumWOFixedCost(monthNavigation.getPrevYearMonth(), userId);

        return thisMonthsSum.subtract(lastMonthsSum).intValue();

    }



}
