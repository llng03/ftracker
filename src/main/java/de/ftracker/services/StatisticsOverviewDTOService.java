package de.ftracker.services;

import de.ftracker.services.DTOs.StatisticsOverviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsOverviewDTOService {
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsOverviewDTOService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public StatisticsOverviewDTO getStatisticsOverviewDTO(int year, int month) {
        StatisticsOverviewDTO statisticsOverviewDTO = new StatisticsOverviewDTO();
        statisticsOverviewDTO.setCostSumPerCategory(
                statisticsService.getCostSumPerCategory(year, month)
        );
        statisticsOverviewDTO.setExpenseSum(statisticsService.getExpenseSumWOFixedCost(year, month));
        statisticsOverviewDTO.setDifferenceSum(statisticsService.differenceToLastMonth(year, month));

        return statisticsOverviewDTO;
    }


}
