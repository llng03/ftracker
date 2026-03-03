package de.ftracker.services.dtos.dtoServices;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.CostManager;
import de.ftracker.services.PotManager;
import de.ftracker.services.dtos.MonthOverviewDTO;
import de.ftracker.utils.MonthlySums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonthOverviewService {
    private final CostManager costManager;
    private final PotManager potManager;

    @Autowired
    public MonthOverviewService(CostManager costManager, PotManager potManager) {
        this.costManager = costManager;
        this.potManager = potManager;
    }

    public MonthOverviewDTO getMonthOverviewDTO(int year, int month, AppUser user) {
        MonthOverviewDTO monthOverviewDTO = new MonthOverviewDTO();

        monthOverviewDTO.setCurrMonth(month);
        monthOverviewDTO.setCurrYear(year);


        monthOverviewDTO.setAllCategories(costManager.getAllCategories(user));

        monthOverviewDTO.setMonthsIncome(costManager.getMonthsIncome(year, month, user.getId()));
        monthOverviewDTO.setMonthsExpense(costManager.getMonthsExp(year, month, user.getId()));

        monthOverviewDTO.setMonthsFixedIncome(costManager.getMonthsFixedIncome(user.getId(), year, month));
        monthOverviewDTO.setMonthsFixedExpense(costManager.getMonthsFixedExp(user.getId(), year, month));

        monthOverviewDTO.setFixedIncome(costManager.getFixedIncome(user.getId()));
        monthOverviewDTO.setFixedExpense(costManager.getFixedExp(user.getId()));

        monthOverviewDTO.setAllMonthsIncome(costManager.getAllMonthsIncome(year, month, user.getId()));
        monthOverviewDTO.setAllMonthsExpense(costManager.getAllMonthsExp(year, month, user.getId()));

        MonthlySums monthlySums = costManager.calculateThisMonthsSums(year, month, user.getId());

        monthOverviewDTO.setSumIn(monthlySums.sumIn);
        monthOverviewDTO.setSumOut(monthlySums.sumOut);
        monthOverviewDTO.setDifference(monthlySums.difference);


        return monthOverviewDTO;
    }
}
