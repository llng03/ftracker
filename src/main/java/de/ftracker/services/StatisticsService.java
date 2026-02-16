package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.Cost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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



}
