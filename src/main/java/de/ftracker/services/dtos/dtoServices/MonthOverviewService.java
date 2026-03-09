package de.ftracker.services.dtos.dtoServices;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.cost.Cost;
import de.ftracker.domain.model.cost.FixedCost;
import de.ftracker.services.CostManager;
import de.ftracker.services.PotManager;
import de.ftracker.services.dtos.MonthOverviewDTO;
import de.ftracker.utils.MonthlySums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

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
        System.out.println("setCurrMonthAndYear");
        monthOverviewDTO.setCurrMonth(month);
        monthOverviewDTO.setCurrYear(year);
        YearMonth currMonth = YearMonth.of(year, month);

        CostTables thisMonthsTables = costManager.getTablesOf(currMonth);
        List<FixedCost> allUsersFixedCosts = costManager.getFixedCosts(user);

        System.out.println("getAllCategories");
        monthOverviewDTO.setAllCategories(costManager.getAllCategories(user));

        System.out.println("getMonthsIncome");
        List<Cost> monthsIncome = costManager.getMonthsIncome(thisMonthsTables, user.getId());
        monthOverviewDTO.setMonthsIncome(monthsIncome);
        System.out.println("getMonthsExp");
        List<Cost> monthsExp = costManager.getMonthsExp(thisMonthsTables, user.getId());
        monthOverviewDTO.setMonthsExpense(monthsExp);

        System.out.println("getFixedIncome");
        List<FixedCost> fixedIncome = costManager.getFixedIncome(allUsersFixedCosts);
        System.out.println("getFixedExp");
        List<FixedCost> fixedExp = costManager.getFixedExp(allUsersFixedCosts);

        System.out.println("getMonthsFixedIncome");
        List<Cost> monthsFixedIncome = costManager.getMonthsFixedIncome(fixedIncome, currMonth);
        monthOverviewDTO.setMonthsFixedIncome(monthsFixedIncome);
        System.out.println("getMonthsFixedExp");
        List<Cost> monthsFixedExp = costManager.getMonthsFixedExp(fixedExp, currMonth);
        monthOverviewDTO.setMonthsFixedExpense(monthsFixedExp);


        System.out.println("getAllMonthsIncome");
        List<Cost> allMonthsIncome = costManager.getAllMonthsIncome(fixedIncome, monthsIncome, currMonth);
        System.out.println("getAllMonthsExp");
        List<Cost> allMonthsExp = costManager.getAllMonthsExp(fixedExp, monthsExp, currMonth);

        System.out.println("calculateThisMonthsSums");
        MonthlySums monthlySums = costManager.calculatethisMonthsSums(allMonthsIncome, allMonthsExp);

        monthOverviewDTO.setSumIn(monthlySums.sumIn);
        monthOverviewDTO.setSumOut(monthlySums.sumOut);
        monthOverviewDTO.setDifference(monthlySums.difference);


        return monthOverviewDTO;
    }
}
