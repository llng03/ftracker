package de.ftracker.services;

import de.ftracker.utils.MonthNavigation;
import de.ftracker.utils.MonthlySums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonthOverviewService {
    private final CostManager costManager;

    @Autowired
    public MonthOverviewService(CostManager costManager) {
        this.costManager = costManager;
    }

    public MonthOverviewDTO getMonthOverviewDTO(int year, int month) {
        MonthNavigation monthNavigation = new MonthNavigation(year, month);
        MonthOverviewDTO monthOverviewDTO = new MonthOverviewDTO();

        monthOverviewDTO.setCurrMonth(month);
        monthOverviewDTO.setCurrYear(year);

        monthOverviewDTO.setMonthsIncome(costManager.getMonthsIncome(year, month));
        monthOverviewDTO.setMonthsExpense(costManager.getMonthsExp(year, month));

        monthOverviewDTO.setMonthsFixedIncome(costManager.getMonthsFixedIncome(year, month));
        monthOverviewDTO.setMonthsFixedExpense(costManager.getMonthsFixedExp(year, month));

        monthOverviewDTO.setFixedIncome(costManager.getFixedIncome());
        monthOverviewDTO.setFixedExpense(costManager.getFixedExp());

        monthOverviewDTO.setAllMonthsIncome(costManager.getAllMonthsIncome(year, month));
        monthOverviewDTO.setAllMonthsExpense(costManager.getAllMonthsExp(year, month));

        MonthlySums monthlySums = costManager.calculateThisMonthsSums(year, month);

        monthOverviewDTO.setSumIn(monthlySums.sumIn);
        monthOverviewDTO.setSumOut(monthlySums.sumOut);
        monthOverviewDTO.setDifference(monthlySums.difference);


        return monthOverviewDTO;
    }
}
